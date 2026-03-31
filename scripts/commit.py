#!/usr/bin/env python3
"""
commit.py — 파일 선택 → 커밋 메시지 자동생성 → 충돌 체크 → push → PR 생성 한 번에

사용법:
  python3 scripts/commit.py
  python3 scripts/commit.py --no-pr   (PR 생성 없이 push까지만)
"""

import sys
import os
import json
import argparse
import subprocess
import anthropic
from dotenv import load_dotenv

load_dotenv()

CLAUDE_MD_PATH = os.path.join(os.path.dirname(os.path.dirname(__file__)), "CLAUDE.md")


def load_claude_md():
    if os.path.exists(CLAUDE_MD_PATH):
        with open(CLAUDE_MD_PATH, "r", encoding="utf-8") as f:
            return f.read()
    return ""


def run(cmd, check=True):
    result = subprocess.run(cmd, capture_output=True, text=True, shell=isinstance(cmd, str))
    if check and result.returncode != 0:
        print(f"❌ 오류: {result.stderr.strip()}")
        sys.exit(1)
    return result.stdout.strip()


def get_changed_files():
    output = run("git status --porcelain")
    if not output:
        print("변경된 파일 없음")
        sys.exit(0)

    files = []
    for line in output.splitlines():
        status = line[:2].strip()
        filepath = line[3:].strip()
        status_label = {
            "M": "수정", "A": "추가", "D": "삭제", "??": "새파일", "R": "이름변경"
        }.get(status, status)
        files.append({"status": status_label, "path": filepath})
    return files


def select_files(files):
    print("\n변경된 파일 목록:\n")
    for i, f in enumerate(files, 1):
        print(f"  [{i}] [{f['status']}] {f['path']}")

    print("\n선택 (예: 1 2 3 / 전체: a / 취소: q): ", end="")
    choice = input().strip()

    if choice.lower() == "q":
        print("취소")
        sys.exit(0)
    elif choice.lower() == "a":
        return [f["path"] for f in files]
    else:
        try:
            indices = [int(x) - 1 for x in choice.split()]
            return [files[i]["path"] for i in indices]
        except:
            print("❌ 잘못된 입력")
            sys.exit(1)


def get_diff(filepaths):
    diff = ""
    for fp in filepaths:
        result = subprocess.run(
            ["git", "diff", "HEAD", "--", fp],
            capture_output=True, text=True
        )
        if not result.stdout.strip():
            result = subprocess.run(
                ["git", "diff", "--", fp],
                capture_output=True, text=True
            )
        diff += result.stdout
    return diff


def check_conflict_with_main():
    print("\n🔍 main과 충돌 체크 중...")

    fetch = subprocess.run(
        ["git", "fetch", "origin", "main"],
        capture_output=True, text=True
    )
    if fetch.returncode != 0:
        print("⚠️  fetch 실패 (네트워크 확인). 충돌 체크 스킵")
        return True

    merge_base = run("git merge-base HEAD origin/main", check=False)
    if not merge_base:
        print("⚠️  merge-base 확인 불가. 충돌 체크 스킵")
        return True

    merge_test = subprocess.run(
        ["git", "merge-tree", merge_base, "HEAD", "origin/main"],
        capture_output=True, text=True
    )

    if "<<<<<<" in merge_test.stdout:
        print("❌ main과 충돌 발생!")
        print("\n💡 해결 방법:")
        print("   git fetch origin main")
        print("   git rebase origin/main")
        print("   # 충돌 해결 후 다시 실행")
        return False

    print("✅ 충돌 없음")
    return True


def generate_commit_message(diff, filepaths):
    client = anthropic.Anthropic()
    claude_md = load_claude_md()

    files_str = "\n".join(filepaths)
    diff_preview = diff[:6000] + "\n...(truncated)" if len(diff) > 6000 else diff

    prompt = f"""다음은 프로젝트 컨텍스트야:

{claude_md}

---

변경된 파일:
{files_str}

git diff:
{diff_preview}

---

위 변경사항을 보고 커밋 메시지를 3개 추천해줘.

규칙:
- 형식: {{type}}: {{설명}}
- type은 feat/fix/refactor/chore/docs/test 중 하나
- 설명은 한국어 허용
- 50자 이내로 간결하게

반드시 아래 JSON 형식으로만 응답 (다른 텍스트 없이):
[
  {{"message": "feat: 커밋 메시지", "reason": "한 줄 설명"}},
  {{"message": "feat: 커밋 메시지2", "reason": "한 줄 설명"}},
  {{"message": "feat: 커밋 메시지3", "reason": "한 줄 설명"}}
]"""

    message = client.messages.create(
        model="claude-opus-4-5",
        max_tokens=500,
        messages=[{"role": "user", "content": prompt}],
    )

    text = message.content[0].text.strip()
    clean = text.replace("```json", "").replace("```", "").strip()
    return json.loads(clean)


def generate_pr_body(base_branch, commit_msg):
    client = anthropic.Anthropic()
    claude_md = load_claude_md()

    diff = run(f"git diff {base_branch}...HEAD", check=False)
    commit_log = run(f"git log {base_branch}...HEAD --oneline", check=False)
    diff_preview = diff[:8000] + "\n...(truncated)" if len(diff) > 8000 else diff

    prompt = f"""다음은 프로젝트 컨텍스트야:

{claude_md}

---

커밋 메시지: {commit_msg}

커밋 로그:
{commit_log}

git diff:
{diff_preview}

---

PR 본문을 아래 형식으로 작성해줘:

## What
- (무엇을 구현/변경했는지)

## Why
- (왜 이 작업이 필요했는지)

## How
- (핵심 구현 방식)

## 체크리스트
- [ ] 테스트 코드 작성
- [ ] 로컬 테스트 완료
- [ ] 관련 문서 업데이트

규칙:
- 한국어로 작성
- 구체적이고 실용적으로"""

    message = client.messages.create(
        model="claude-opus-4-5",
        max_tokens=1500,
        messages=[{"role": "user", "content": prompt}],
    )

    return message.content[0].text.strip()


def get_current_branch():
    return run("git symbolic-ref --short HEAD")


def get_repo_info():
    remote = run("git remote get-url origin", check=False)
    if "github.com" in remote:
        repo = remote.replace("https://github.com/", "").replace("git@github.com:", "").replace(".git", "")
        return repo.strip()
    return None


def main():
    parser = argparse.ArgumentParser(description="파일 선택 → 커밋 → 충돌체크 → push → PR 한 번에")
    parser.add_argument("--no-pr", action="store_true", help="PR 생성 없이 push까지만")
    parser.add_argument("--base", default="main", help="PR 기준 브랜치 (기본: main)")
    args = parser.parse_args()

    current_branch = get_current_branch()
    print(f"📍 현재 브랜치: {current_branch}")

    if current_branch == "main":
        print("⚠️  main 브랜치에서는 직접 커밋하지 마세요!")
        print("   python3 scripts/branch.py '작업내용' 으로 브랜치 먼저 만들어줘")
        sys.exit(1)

    # 1. 변경 파일 선택
    files = get_changed_files()
    selected = select_files(files)
    print(f"\n✅ 선택된 파일: {', '.join(selected)}")

    # 2. git add
    for fp in selected:
        run(["git", "add", fp])
    print("✅ git add 완료")

    # 3. 커밋 메시지 생성
    print("\n✍️  커밋 메시지 생성 중...")
    diff = get_diff(selected)
    suggestions = generate_commit_message(diff, selected)

    print("\n추천 커밋 메시지:\n")
    for i, item in enumerate(suggestions, 1):
        print(f"  [{i}] {item['message']}")
        print(f"      → {item['reason']}\n")

    print("번호 선택 (1-3) 또는 직접 입력: ", end="")
    choice = input().strip()

    if choice in ("1", "2", "3"):
        commit_msg = suggestions[int(choice) - 1]["message"]
    elif choice:
        commit_msg = choice
    else:
        print("❌ 입력 없음")
        sys.exit(1)

    # 4. git commit
    run(["git", "commit", "-m", commit_msg])
    print(f"✅ 커밋 완료: {commit_msg}")

    # 5. main 충돌 체크
    if not check_conflict_with_main():
        print("\n⛔ 충돌 해결 후 다시 실행해줘")
        sys.exit(1)

    # 6. git push
    print(f"\n📤 push 중... ({current_branch})")
    result = subprocess.run(
        ["git", "push", "origin", current_branch],
        capture_output=True, text=True
    )
    if result.returncode != 0:
        run(f"git push --set-upstream origin {current_branch}")
    print("✅ push 완료")

    # 7. CI 링크 안내
    repo = get_repo_info()
    if repo:
        print(f"\n🔄 CI 확인: https://github.com/{repo}/actions")

    # 8. PR 생성
    if args.no_pr:
        print("\n🎉 완료! (PR 생성 스킵)")
        return

    gh_check = subprocess.run(["which", "gh"], capture_output=True)
    if gh_check.returncode != 0:
        print("\n⚠️  gh CLI 없음. PR은 GitHub에서 직접 올려줘")
        print("   설치: brew install gh")
        return

    print(f"\n✍️  PR 본문 생성 중...")
    pr_body = generate_pr_body(args.base, commit_msg)

    print("\n" + "=" * 50)
    print(pr_body)
    print("=" * 50)

    print("\nPR 제목 입력 (엔터치면 커밋 메시지 사용): ", end="")
    pr_title = input().strip() or commit_msg

    print(f"\n📬 PR 생성 중...")
    result = subprocess.run(
        ["gh", "pr", "create",
         "--title", pr_title,
         "--body", pr_body,
         "--base", args.base],
        capture_output=True, text=True
    )

    if result.returncode == 0:
        print(f"✅ PR 생성 완료!")
        print(f"   {result.stdout.strip()}")
    else:
        print(f"❌ PR 생성 실패: {result.stderr}")
        print("   GitHub에서 직접 올려줘")


if __name__ == "__main__":
    main()


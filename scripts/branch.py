"""
branch.py — 커밋 메시지/작업 내용 → 브랜치명 자동 생성

사용법:
  python3 scripts/branch.py "JWT 로그인 구현, Redis refresh token 저장"
  python3 scripts/branch.py -t feature "로그아웃 시 토큰 블랙리스트 처리"
  echo "작업 내용" | python3 scripts/branch.py
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


def generate_branch_names(commit_msg: str, branch_type: str = None) -> list[dict]:
    client = anthropic.Anthropic()
    claude_md = load_claude_md()

    type_hint = (
        f'브랜치 타입은 반드시 "{branch_type}"으로 시작해야 해.'
        if branch_type
        else "작업 내용을 보고 feature/fix/refactor/chore/docs/test 중 적절한 타입을 골라줘."
    )

    prompt = f"""다음은 프로젝트 컨텍스트야:

{claude_md}

---

작업 내용: "{commit_msg}"

위 내용을 보고 Git 브랜치명 3개를 추천해줘.

규칙:
- {type_hint}
- 형식: {{type}}/{{kebab-case-description}}
- 영어 소문자 + 하이픈만 사용 (언더스코어 금지)
- 2~4 단어로 간결하게
- 동사로 시작 권장

반드시 아래 JSON 형식으로만 응답해 (다른 텍스트 없이):
[
  {{"branch": "feature/branch-name", "reason": "한 줄 설명"}},
  {{"branch": "feature/branch-name-2", "reason": "한 줄 설명"}},
  {{"branch": "feature/branch-name-3", "reason": "한 줄 설명"}}
]"""

    message = client.messages.create(
        model="claude-opus-4-5",
        max_tokens=500,
        messages=[{"role": "user", "content": prompt}],
    )

    text = message.content[0].text.strip()
    clean = text.replace("```json", "").replace("```", "").strip()
    return json.loads(clean)


def pick_branch(branches: list[dict]) -> str:
    print("\n추천 브랜치명:\n")
    for i, item in enumerate(branches, 1):
        print(f"  [{i}] {item['branch']}")
        print(f"      → {item['reason']}\n")

    while True:
        choice = input("번호 선택 (1-3) 또는 직접 입력: ").strip()
        if choice in ("1", "2", "3"):
            return branches[int(choice) - 1]["branch"]
        elif choice:
            return choice


def main():
    parser = argparse.ArgumentParser(description="커밋 메시지 → 브랜치명 자동 생성")
    parser.add_argument("message", nargs="?", help="커밋 메시지 또는 작업 내용")
    parser.add_argument("-t", "--type", help="브랜치 타입 강제 지정 (feature, fix, ...)")
    parser.add_argument("--checkout", action="store_true", help="선택 후 git checkout -b 실행")
    parser.add_argument("--no-pick", action="store_true", help="첫 번째 추천 브랜치명만 출력")
    args = parser.parse_args()

    # stdin 또는 인자에서 메시지 읽기
    if args.message:
        commit_msg = args.message
    elif not sys.stdin.isatty():
        commit_msg = sys.stdin.read().strip()
    else:
        print("사용법: python3 scripts/branch.py '작업 내용'")
        sys.exit(1)

    print(f"🔍 분석 중: {commit_msg[:60]}{'...' if len(commit_msg) > 60 else ''}")

    branches = generate_branch_names(commit_msg, args.type)

    if args.no_pick:
        print(branches[0]["branch"])
        return

    selected = pick_branch(branches)

    if args.checkout:
        result = subprocess.run(["git", "checkout", "-b", selected], capture_output=True, text=True)
        if result.returncode == 0:
            print(f"\n✅ 브랜치 생성 완료: {selected}")
        else:
            print(f"\n❌ git checkout 실패: {result.stderr}")
    else:
        print(f"\n✅ 선택된 브랜치: {selected}")
        print(f"\n   git checkout -b {selected}")


if __name__ == "__main__":
    main()

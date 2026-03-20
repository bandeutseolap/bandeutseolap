<template>
  <div class="page-content">
    <div class="page-header board-header-row">
      <div>
        <h1 class="section-title">Board</h1>
        <p class="section-desc">공지사항 및 일반 게시글 목록을 확인할 수 있습니다.</p>
      </div>

      <div class="board-header-actions">
        <button class="btn btn-common">글쓰기</button>
      </div>
    </div>

    <!-- 검색 / 필터 영역 -->
    <section class="panel">
      <div class="panel-body">
        <div class="grid-12 board-filter-grid">
          <div class="col-4">
            <label class="board-label">검색어</label>
            <input
                type="text"
                class="form-control"
                placeholder="제목 또는 작성자를 입력하세요"
            />
          </div>

          <div class="col-2">
            <label class="board-label">상태</label>
            <select class="form-control">
              <option>전체</option>
              <option>공지</option>
              <option>일반</option>
              <option>보관</option>
            </select>
          </div>

          <div class="col-2">
            <label class="board-label">정렬</label>
            <select class="form-control">
              <option>최신순</option>
              <option>오래된순</option>
              <option>조회순</option>
            </select>
          </div>

          <div class="col-4 board-filter-actions">
            <button class="btn btn-secondary">초기화</button>
            <button class="btn btn-common">조회</button>
          </div>
        </div>
      </div>
    </section>

    <!-- 목록 요약 -->
    <section class="board-summary-row">
      <div class="text-soft">총 <strong>128</strong>건</div>
      <div class="text-muted">정렬: 최신순</div>
    </section>

    <!-- 게시글 목록 -->
    <section class="panel">
      <div class="panel-body board-list-wrap">
        <table class="table-basic board-table">
          <colgroup>
            <col style="width: 8%" />
            <col style="width: 44%" />
            <col style="width: 12%" />
            <col style="width: 12%" />
            <col style="width: 14%" />
            <col style="width: 10%" />
          </colgroup>
          <thead>
          <tr>
            <th>번호</th>
            <th>제목</th>
            <th>작성자</th>
            <th>상태</th>
            <th>등록일</th>
            <th>조회수</th>
          </tr>
          </thead>
          <tbody>
          <tr>
            <td>128</td>
            <td>
              <a href="#" class="board-title-link">
                프로젝트 문서 관리 정책 변경 안내
              </a>
            </td>
            <td>관리자</td>
            <td><span class="chip chip-common">공지</span></td>
            <td>2026-03-20</td>
            <td>214</td>
          </tr>

          <tr>
            <td>127</td>
            <td>
              <a href="#" class="board-title-link">
                3월 셋째 주 업무 공유사항
              </a>
            </td>
            <td>김민수</td>
            <td><span class="chip chip-info">일반</span></td>
            <td>2026-03-19</td>
            <td>81</td>
          </tr>

          <tr>
            <td>126</td>
            <td>
              <a href="#" class="board-title-link">
                신규 프로젝트 생성 절차 초안 검토 요청
              </a>
            </td>
            <td>이서연</td>
            <td><span class="chip chip-success">진행</span></td>
            <td>2026-03-18</td>
            <td>57</td>
          </tr>

          <tr>
            <td>125</td>
            <td>
              <a href="#" class="board-title-link">
                게시판 UI 시안 검토 결과 공유
              </a>
            </td>
            <td>박지훈</td>
            <td><span class="chip chip-warn">검토중</span></td>
            <td>2026-03-17</td>
            <td>39</td>
          </tr>

          <tr>
            <td>124</td>
            <td>
              <a href="#" class="board-title-link">
                임시 저장 데이터 정리 일정 안내
              </a>
            </td>
            <td>최유진</td>
            <td><span class="chip chip-error">보류</span></td>
            <td>2026-03-16</td>
            <td>22</td>
          </tr>
          </tbody>
        </table>
      </div>
    </section>

    <!-- 페이지네이션 -->
    <section class="board-pagination">
      <button class="btn btn-sm btn-ghost">이전</button>
      <button class="btn btn-sm btn-common">1</button>
      <button class="btn btn-sm btn-outline-common">2</button>
      <button class="btn btn-sm btn-outline-common">3</button>
      <button class="btn btn-sm btn-outline-common">4</button>
      <button class="btn btn-sm btn-outline-common">5</button>
      <button class="btn btn-sm btn-ghost">다음</button>
    </section>
  </div>
</template>

<script>
export default {
  name: 'BoardView'
}
</script>

<style scoped>
.board-header-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: var(--space-4);
}

.board-header-actions {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.board-label {
  display: inline-block;
  margin-bottom: var(--space-2);
  font-size: var(--font-size-sm);
  font-weight: 600;
  color: var(--color-text-soft);
}

.board-filter-grid {
  align-items: end;
}

.board-filter-actions {
  display: flex;
  align-items: end;
  justify-content: flex-end;
  gap: var(--space-3);
}

.board-summary-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-4);
}

.board-list-wrap {
  padding: 0;
}

.board-table th,
.board-table td {
  vertical-align: middle;
}

.board-title-link {
  display: inline-block;
  color: var(--color-text);
  font-weight: 500;
}

.board-title-link:hover {
  color: var(--color-primary-700);
  text-decoration: underline;
}

.board-pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-2);
}
</style>
<script>
import { fetchBoardList } from '../services/boardService'

export default {
  name: 'BoardListView',
  data() {
    return {
      loading: false,
      error: '',
      totalCount: 0,
      boardItems: [],
    }
  },
  methods: {
    async loadBoardList() {
      this.loading = true
      this.error = ''

      try {
        const response = await fetchBoardList()

        this.boardItems = response.content || []
        this.totalCount = response.numberOfElements || 0
      } catch (err) {
        this.error = err.message || '목록을 불러오지 못했습니다.'
      } finally {
        this.loading = false
      }
    },
  },
  created() {
    this.loadBoardList()
  },
}
</script>

<template>
  <div class="page-content">
    <div class="page-header board-header-row">
      <div>
        <h1 class="section-title">Board</h1>
        <p class="section-desc">공지사항 및 일반 게시글 목록입니다.</p>
      </div>

      <div class="board-header-actions">
        <button class="btn btn-common">글쓰기</button>
      </div>
    </div>

    <section class="board-summary-row">
      <div class="text-soft">
        총 <strong>{{ totalCount }}</strong>건
      </div>
      <div class="text-muted">정렬: 최신순</div>
    </section>

    <section class="panel">
      <div class="panel-body board-list-wrap">
        <div v-if="loading" class="board-empty-state">
          목록을 불러오는 중입니다.
        </div>

        <div v-else-if="error" class="board-empty-state">
          {{ error }}
        </div>

        <table v-else class="table-basic board-table">
          <colgroup>
            <col style="width: 10%" />
            <col style="width: 50%" />
            <col style="width: 22%" />
            <col style="width: 18%" />
          </colgroup>
          <thead>
          <tr>
            <th>번호</th>
            <th>제목</th>
            <th>생성일시</th>
            <th>작성자</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="item in boardItems" :key="item.boardId">
            <td>{{ item.boardId }}</td>
            <td>
              <router-link
                  :to="`/boards/${item.boardId}`"
                  class="board-title-link"
              >
                {{ item.title }}
              </router-link>
            </td>
            <td>{{ item.writtenAt }}</td>
            <td>{{ item.writtenBy }}</td>
          </tr>

          <tr v-if="!boardItems.length">
            <td colspan="5" class="board-empty-state">
              게시글이 없습니다.
            </td>
          </tr>
          </tbody>
        </table>
      </div>
    </section>
  </div>
</template>

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

.board-empty-state {
  padding: 40px 16px;
  text-align: center;
  color: var(--color-text-muted);
}
</style>
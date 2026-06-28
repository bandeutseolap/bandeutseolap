<script>
import { fetchBoardDetail, deleteBoard } from '@/services/boardService'
import YesNoModal from '@/components/common/YesNoModal.vue'
import BoardContent from '@/components/common/BoardContent.vue'

export default {
  name: 'BoardDetailView',
  components: {
    YesNoModal,
    BoardContent
  },
  data() {
    return {
      loading: false,
      deleting: false,
      showDeleteModal: false,
      error: '',
      board: null,
    }
  },
  methods: {
    async loadBoardDetail() {
      this.loading = true
      this.error = ''

      try {
        const boardId = this.$route.params.id
        const response = await fetchBoardDetail(boardId)

        this.board = response || null
      } catch (err) {
        this.error = err.message || '상세 정보를 불러오지 못했습니다.'
      } finally {
        this.loading = false
      }
    },
    getStatusClass(status) {
      const statusMap = {
        공지: 'chip chip-common',
        진행: 'chip chip-info',
        검토중: 'chip chip-warn',
        완료: 'chip chip-success',
        보류: 'chip chip-error',
        등록: 'chip chip-common',
      }

      return statusMap[status] || 'chip chip-info'
    },
    goToEdit(id) {
      //if(!this.writtenBy) return alert('작성자만 수정할 수 있습니다.')
      this.$router.push(`/boards/${id}/edit`)
    },
    async handleDelete() {
      this.deleting = true
      try {
        const boardId = this.$route.params.id
        await deleteBoard(boardId)
        this.$router.push(`/boards`);
      } catch (err) {
        this.error = err.message || '삭제에 실패했습니다.'
      } finally {
        this.deleting = false
        this.showDeleteModal = false
      }
    }
  },
  created() {
    this.loadBoardDetail()
  },
  watch: {
    '$route.params.id'() {
      this.loadBoardDetail()
    },
  },
}
</script>

<template>
  <div class="page-content">
    <section class="panel">
      <div v-if="loading" class="panel-body board-detail-empty">
        게시글을 불러오는 중입니다.
      </div>

      <div v-else-if="error" class="panel-body board-detail-empty">
        {{ error }}
      </div>

      <template v-else-if="board">
        <div class="panel-header">
          <div class="board-detail-header">
            <div class="board-detail-title-wrap">
              <h1 class="section-title board-detail-title">
                {{ board.title }}
              </h1>
            </div>

            <div class="board-detail-status">
              <span :class="getStatusClass(board.status)">

              </span>
            </div>
          </div>

          <div class="board-meta-row">
            <div class="board-meta-item">
              <span class="board-meta-label">번호</span>
              <span>{{ board.boardId }}</span>
            </div>
            <div class="board-meta-item">
              <span class="board-meta-label">작성자</span>
              <span>{{ board.writtenBy }}</span>
            </div>
            <div class="board-meta-item">
              <span class="board-meta-label">생성일시</span>
              <span>{{ board.writtenAt }}</span>
            </div>
          </div>
        </div>

        <!-- <div class="panel-body">
          <div class="board-content-text">
            {{ board.content }}
          </div>
        </div> -->

        <BoardContent
          :model-value="board.content"
          :editable="false"
          :initial-attachments="board.files"
        />

        <div class="panel-footer board-detail-footer">
          <router-link to="/boards" class="btn btn-secondary">
            목록
          </router-link>
          <div class="board-detail-actions">
            <button class="btn btn-common" @click="goToEdit(board.boardId)">수정</button>
            <button
            class="btn btn-error"
            @click="showDeleteModal = true"
            >
             삭제
            </button>
            <YesNoModal
              v-model="showDeleteModal"
              title="게시글 삭제"
              message="삭제하시겠습니까?"
              subMessage="삭제된 파일은 복구할 수 없습니다."
              :loading="deleting"
              @confirm="handleDelete"
            />
          </div>
        </div>
      </template>
    </section>
  </div>
</template>

<style scoped>
.board-detail-empty {
  padding: 48px 20px;
  text-align: center;
  color: var(--color-text-muted);
}

.board-detail-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: var(--space-4);
}

.board-detail-title-wrap {
  flex: 1;
}

.board-detail-title {
  margin: 0;
}

.board-detail-status {
  display: flex;
  align-items: center;
}

.board-meta-row {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-4) var(--space-6);
  margin-top: var(--space-4);
  padding-top: var(--space-4);
  border-top: 1px solid var(--color-border);
}

.board-meta-item {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  color: var(--color-text-soft);
  font-size: var(--font-size-sm);
}

.board-meta-label {
  font-weight: 600;
  color: var(--color-text);
}

.board-content-text {
  min-height: 260px;
  white-space: pre-line;
  line-height: 1.8;
  color: var(--color-text);
}

.board-detail-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-4);
}

.board-detail-actions {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}
</style>
<script>
import { fetchBoardDetail, createBoard, updateBoard } from '@/services/boardService'
import BoardContent from '@/components/common/BoardContent.vue'

export default {
  name: 'BoardFormView',
  components: {
    BoardContent
  },
  data() {
    return {
      loading: false,
      submitting: false,
      error: '',
      form: {
        title: '',
        content: '',
        visibleYn: true,
        fixedTopYn: false,
        noticeYn: false,
        boardAreaCd: 'SERVICE',
        openTargetCd: 'ALL',
        //deleteFileIds: [0],
        files: []
      },
    }
  },
  computed: {
    isEditMode() {
      return this.$route.name === 'boards-edit'
    },
    boardId() {
      return this.$route.params.id
    },
    pageTitle() {
      return this.isEditMode ? '게시글 수정' : '게시글 작성'
    },
  },
  methods: {
    async loadBoard() {
      this.loading = true
      this.error = ''
      try {
        const response = await fetchBoardDetail(this.boardId)
        console.log("게시글 수정 " + JSON.stringify(response))
        this.form = {
          title: response.title || '',
          content: response.content || '',
          visibleYn: response.visibleYn ?? true,
          fixedTopYn: response.fixedTopYn ?? false,
          noticeYn: response.noticeYn ?? false,
          boardAreaCd: response.boardAreaCd || '',
          openTargetCd: response.openTargetCd || '',
          //deleteFileIds: response.deleteFileIds || []
          files: response.files || []
        }
      } catch (err) {
        this.error = err.message || '게시글을 불러오지 못했습니다.'
      } finally {
        this.loading = false
      }
    },
    async handleSubmit() {
      if (!this.form.title.trim()) {
        this.error = '제목을 입력해주세요.'
        return
      }
      if (!this.form.content.trim()) {
        this.error = '내용을 입력해주세요.'
        return
      }

      this.submitting = true
      this.error = ''

      try {
        //TODO: 파일 POST API 호출 (FileController)
        if (this.isEditMode) {
          await updateBoard(this.boardId, this.form)
          this.$router.push(`/boards/${this.boardId}`)
        } else {
          const response = await createBoard(this.form)
          this.$router.push(`/boards/${response.boardId}`)
        }
      } catch (err) {
        //this.error = err.message || '저장에 실패했습니다.'
        console.log('에러 상태:', err.response.status)
        console.log('에러 메시지:', err.response.data)  // ← 서버 에러 메시지 확인
      } finally {
        this.submitting = false
      }
    },
    handleCancel() {
      if (this.isEditMode) {
        this.$router.push(`/boards/${this.boardId}`)
      } else {
        this.$router.push('/boards')
      }
    },
  },
  created() {
    if (this.isEditMode) {
      this.loadBoard()
    }
  },
}
</script>

<template>
  <div class="page-content">
    <div class="page-header">
      <h1 class="section-title">{{ pageTitle }}</h1>
    </div>

    <section class="panel">
      <div v-if="loading" class="panel-body form-empty">
        게시글을 불러오는 중입니다.
      </div>

      <template v-else>
        <div class="panel-body">
          <div v-if="error" class="form-error-message">
            {{ error }}
          </div>

          <div class="form-group">
            <label class="form-label">제목 <span class="form-required">*</span></label>
            <input
              v-model="form.title"
              type="text"
              class="form-control"
              placeholder="제목을 입력하세요."
              maxlength="200"
            />
          </div>

          <div class="form-row">
            <div class="form-group form-group-inline">
              <span class="form-label">영역</span>
              <div class="form-radio-group">
                <label class="form-radio-label">
                  <input v-model="form.boardAreaCd" type="radio" value="SERVICE" class="form-radio" />
                  서비스
                </label>
                <label class="form-radio-label">
                  <input v-model="form.boardAreaCd" type="radio" value="PROJECT" class="form-radio" />
                  프로젝트
                </label>
              </div>
            </div>
            <div class="form-group form-group-inline">
              <span class="form-label">공개 대상</span>
              <div class="form-radio-group">
                <label class="form-radio-label">
                  <input v-model="form.openTargetCd" type="radio" value="ALL" class="form-radio" />
                  전체
                </label>
                <label class="form-radio-label">
                  <input v-model="form.openTargetCd" type="radio" value="LOGIN" class="form-radio" />
                  사용자
                </label>
              </div>
            </div>
          </div>

          <div class="form-check-row">
            <label class="form-check-label">
              <input v-model="form.visibleYn" type="checkbox" class="form-check" />
              공개
            </label>
            <label class="form-check-label">
              <input v-model="form.noticeYn" type="checkbox" class="form-check" />
              공지
            </label>
            <label class="form-check-label">
              <input v-model="form.fixedTopYn" type="checkbox" class="form-check" />
              상단 고정
            </label>
          </div>

          <div class="form-group">
            <label class="form-label">내용 <span class="form-required">*</span></label>
            <!-- <textarea
              v-model="form.content"
              class="form-control form-textarea"
              placeholder="내용을 입력하세요."
            /> -->
            <!-- TODO: 위지위그로 교체 (v-model : 양방향 바인딩 (작성/수정 페이지)) -->
            <!-- :initial-attachments="form.files" -->
            <BoardContent
              v-model="form.content"
              :editable="true"
              @update:attachments="form.files = $event"
              :initial-attachments="form.files"
            />
          </div>
        </div>

        <div class="panel-footer form-footer">
          <button class="btn btn-secondary" @click="handleCancel">취소</button>
          <button
            class="btn btn-common"
            :disabled="submitting"
            @click="handleSubmit"
          >
            {{ submitting ? '저장 중...' : '저장' }}
          </button>
        </div>
      </template>
    </section>
  </div>
</template>

<style scoped>
.form-empty {
  padding: 48px 20px;
  text-align: center;
  color: var(--color-text-muted);
}

.form-error-message {
  margin-bottom: var(--space-4);
  padding: var(--space-3) var(--space-4);
  background: var(--color-error-50);
  color: var(--color-error-700);
  border: 1px solid var(--color-error-200);
  border-radius: var(--radius-md);
  font-size: var(--font-size-sm);
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
  margin-bottom: var(--space-5);
}

.form-row {
  display: flex;
  gap: var(--space-4);
  margin-bottom: var(--space-5);
}

.form-group-inline {
  flex: 1;
  margin-bottom: 0;
}

.form-label {
  font-size: var(--font-size-sm);
  font-weight: 600;
  color: var(--color-text-soft);
}

.form-required {
  color: var(--color-error-500);
  margin-left: 2px;
}

.form-textarea {
  height: 360px;
  padding: var(--space-3);
  resize: vertical;
  line-height: 1.8;
}

.form-radio-group {
  display: flex;
  gap: var(--space-5);
}

.form-radio-label {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--font-size-sm);
  color: var(--color-text-soft);
  cursor: pointer;
}

.form-radio {
  width: 16px;
  height: 16px;
  cursor: pointer;
  accent-color: var(--color-primary-600);
}

.form-check-row {
  display: flex;
  gap: var(--space-6);
  margin-bottom: var(--space-5);
}

.form-check-label {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--font-size-sm);
  color: var(--color-text-soft);
  cursor: pointer;
}

.form-check {
  width: 16px;
  height: 16px;
  cursor: pointer;
  accent-color: var(--color-primary-600);
}

.form-footer {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: var(--space-3);
}
</style>

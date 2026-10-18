<template>
  <div class="pms-page is-narrow">
    <div class="page-header head-row">
      <div>
        <h1>업무 등록</h1>
        <p>{{ currentProject.name }} · 새 업무를 등록합니다.</p>
      </div>
      <button type="button" class="btn btn-outline" @click="goBack">← 목록으로</button>
    </div>

    <div class="card">
      <div class="fcard-body">
        <div class="form-row">
          <label for="task-title">업무명<span class="req">*</span></label>
          <input id="task-title" v-model="form.title" class="form-control" placeholder="예) 게시판 API 개발" maxlength="100" />
          <p v-if="errors.title" class="field-error">{{ errors.title }}</p>
        </div>

        <div class="form-grid-2">
          <div class="form-row">
            <label for="task-assignee">담당자</label>
            <select id="task-assignee" v-model="form.assignee" class="form-control">
              <option v-for="m in members" :key="m.id" :value="m.loginId">{{ m.loginId }}</option>
            </select>
          </div>
          <div class="form-row">
            <label for="task-status">상태</label>
            <select id="task-status" v-model="form.status" class="form-control">
              <option v-for="(meta, code) in statusOptions" :key="code" :value="code">{{ meta.label }}</option>
            </select>
          </div>
        </div>

        <div class="form-row">
          <label for="task-due">마감일</label>
          <input id="task-due" v-model="form.dueDate" type="date" class="form-control" />
        </div>

        <div class="form-row">
          <label for="task-desc">설명</label>
          <textarea id="task-desc" v-model="form.description" class="form-control" rows="5" placeholder="업무 내용을 입력해주세요."></textarea>
        </div>
      </div>

      <div class="fcard-footer">
        <button type="button" class="btn btn-outline" @click="goBack">취소</button>
        <button type="button" class="btn btn-primary" :disabled="submitting" @click="onSubmit">
          {{ submitting ? '저장 중...' : '등록' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import sampleStore from '@/store/sampleStore'
import sampleViewMixin from '@/mixins/sampleViewMixin'
import { TASK_STATUS } from '@/utils/workMeta'

export default {
  name: 'TaskFormView',
  mixins: [sampleViewMixin],
  data() {
    return {
      statusOptions: TASK_STATUS,
      form: {
        title: '',
        assignee: sampleStore.state.currentUser.loginId,
        status: 'WAIT',
        dueDate: '',
        description: '',
      },
      errors: {},
      submitting: false,
    }
  },
  computed: {
    members() {
      return sampleStore.state.members
    },
  },
  methods: {
    validate() {
      const e = {}
      if (!this.form.title.trim()) e.title = '업무명을 입력해주세요.'
      this.errors = e
      return Object.keys(e).length === 0
    },
    async onSubmit() {
      if (!this.validate() || this.submitting) return
      this.submitting = true
      try {
        await sampleStore.addTask({ ...this.form, title: this.form.title.trim() })
        sampleStore.setFlash('업무가 등록되었습니다.')
        this.$router.push({ name: 'TaskList' })
      } catch (err) {
        // TODO: 공통 에러 처리(토스트/알림) 컨벤션에 맞춰 연결
        console.error('업무 등록 실패', err)
      } finally {
        this.submitting = false
      }
    },
    goBack() {
      this.$router.push({ name: 'TaskList' })
    },
  },
}
</script>

<style scoped>
.head-row {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: var(--space-4);
}
</style>

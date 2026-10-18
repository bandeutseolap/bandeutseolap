<template>
  <div class="pms-page is-narrow">
    <div class="page-header head-row">
      <div>
        <h1>이슈 등록</h1>
        <p>{{ currentProject.name }} · 새 이슈/리스크를 등록합니다.</p>
      </div>
      <button type="button" class="btn btn-outline" @click="goBack">← 목록으로</button>
    </div>

    <div class="card">
      <div class="fcard-body">
        <div class="form-row">
          <label for="issue-title">제목<span class="req">*</span></label>
          <input id="issue-title" v-model="form.title" class="form-control" placeholder="예) 로그인 토큰 만료 시 무한 리다이렉트" maxlength="150" />
          <p v-if="errors.title" class="field-error">{{ errors.title }}</p>
        </div>

        <div class="form-grid-2">
          <div class="form-row">
            <label for="issue-severity">심각도</label>
            <select id="issue-severity" v-model="form.severity" class="form-control">
              <option v-for="(meta, code) in severityOptions" :key="code" :value="code">{{ meta.label }}</option>
            </select>
          </div>
          <div class="form-row">
            <label for="issue-type">유형</label>
            <select id="issue-type" v-model="form.type" class="form-control">
              <option v-for="(meta, code) in typeOptions" :key="code" :value="code">{{ meta.label }}</option>
            </select>
          </div>
        </div>

        <div class="form-grid-2">
          <div class="form-row">
            <label for="issue-assignee">담당자</label>
            <select id="issue-assignee" v-model="form.assignee" class="form-control">
              <option v-for="m in members" :key="m.id" :value="m.loginId">{{ m.loginId }}</option>
            </select>
          </div>
          <div class="form-row">
            <label for="issue-status">상태</label>
            <select id="issue-status" v-model="form.status" class="form-control">
              <option v-for="(meta, code) in statusOptions" :key="code" :value="code">{{ meta.label }}</option>
            </select>
          </div>
        </div>

        <div class="form-row">
          <label for="issue-desc">내용</label>
          <textarea id="issue-desc" v-model="form.description" class="form-control" rows="6" placeholder="재현 방법, 영향 범위 등을 입력해주세요."></textarea>
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
import { ISSUE_SEVERITY, ISSUE_TYPE, ISSUE_STATUS } from '@/utils/workMeta'

export default {
  name: 'IssueFormView',
  mixins: [sampleViewMixin],
  data() {
    return {
      severityOptions: ISSUE_SEVERITY,
      typeOptions: ISSUE_TYPE,
      statusOptions: ISSUE_STATUS,
      form: {
        title: '',
        severity: 'MED',
        type: 'BUG',
        assignee: sampleStore.state.currentUser.loginId,
        status: 'WAIT',
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
      if (!this.form.title.trim()) e.title = '제목을 입력해주세요.'
      this.errors = e
      return Object.keys(e).length === 0
    },
    async onSubmit() {
      if (!this.validate() || this.submitting) return
      this.submitting = true
      try {
        await sampleStore.addIssue({ ...this.form, title: this.form.title.trim() })
        sampleStore.setFlash('이슈가 등록되었습니다.')
        this.$router.push({ name: 'IssueList' })
      } catch (err) {
        // TODO: 공통 에러 처리(토스트/알림) 컨벤션에 맞춰 연결
        console.error('이슈 등록 실패', err)
      } finally {
        this.submitting = false
      }
    },
    goBack() {
      this.$router.push({ name: 'IssueList' })
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

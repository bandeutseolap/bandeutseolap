<template>
  <div class="pms-page is-narrow">
    <div class="page-header head-row">
      <div>
        <h1>문서 작성</h1>
        <p>{{ currentProject.name }} · 템플릿을 선택해 문서를 작성합니다.</p>
      </div>
      <button type="button" class="btn btn-outline" @click="goBack">← 목록으로</button>
    </div>

    <div class="card">
      <div class="fcard-body">
        <div class="form-grid-2">
          <div class="form-row">
            <label for="doc-title">문서 제목<span class="req">*</span></label>
            <input id="doc-title" v-model="form.title" class="form-control" placeholder="예) 10월 3주차 회의록" maxlength="150" />
            <p v-if="errors.title" class="field-error">{{ errors.title }}</p>
          </div>
          <div class="form-row">
            <label for="doc-template">템플릿</label>
            <select id="doc-template" v-model="selectedTemplateId" class="form-control" @change="applyTemplate">
              <option :value="null">템플릿 없음</option>
              <option v-for="tpl in templates" :key="tpl.id" :value="tpl.id">{{ tpl.name }}</option>
            </select>
          </div>
        </div>

        <div class="form-row">
          <label for="doc-content">내용<span class="req">*</span></label>
          <textarea id="doc-content" v-model="form.content" class="form-control" rows="14" placeholder="문서 내용을 입력해주세요."></textarea>
          <p v-if="errors.content" class="field-error">{{ errors.content }}</p>
        </div>
      </div>

      <div class="fcard-footer">
        <button type="button" class="btn btn-outline" @click="goBack">취소</button>
        <button type="button" class="btn btn-primary" :disabled="submitting" @click="onSubmit">
          {{ submitting ? '저장 중...' : '저장' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import sampleStore from '@/store/sampleStore'
import sampleViewMixin from '@/mixins/sampleViewMixin'

export default {
  name: 'DocumentFormView',
  mixins: [sampleViewMixin],
  data() {
    return {
      form: { title: '', content: '' },
      selectedTemplateId: null,
      // 마지막으로 채워 넣은 템플릿 본문 — 사용자가 직접 수정한 내용을 덮어쓰지 않기 위해 비교용으로 보관
      lastAppliedContent: '',
      errors: {},
      submitting: false,
    }
  },
  computed: {
    templates() {
      return sampleStore.state.templates
    },
    selectedTemplate() {
      return this.templates.find((t) => t.id === this.selectedTemplateId) || null
    },
  },
  created() {
    // 템플릿 목록의 '문서 작성' 버튼에서 ?templateId= 로 넘어오면 해당 템플릿을 미리 선택한다
    const id = Number(this.$route.query.templateId)
    if (id && this.templates.some((t) => t.id === id)) {
      this.selectedTemplateId = id
      this.applyTemplate()
    }
  },
  methods: {
    applyTemplate() {
      const next = this.selectedTemplate ? this.selectedTemplate.content : ''
      // 내용이 비어 있거나, 직전에 채운 템플릿 그대로일 때만 교체한다
      if (!this.form.content.trim() || this.form.content === this.lastAppliedContent) {
        this.form.content = next
        this.lastAppliedContent = next
      }
    },
    validate() {
      const e = {}
      if (!this.form.title.trim()) e.title = '문서 제목을 입력해주세요.'
      if (!this.form.content.trim()) e.content = '내용을 입력해주세요.'
      this.errors = e
      return Object.keys(e).length === 0
    },
    async onSubmit() {
      if (!this.validate() || this.submitting) return
      this.submitting = true
      try {
        await sampleStore.addDocument({
          title: this.form.title.trim(),
          content: this.form.content,
          templateName: this.selectedTemplate ? this.selectedTemplate.name : '',
        })
        sampleStore.setFlash('문서가 저장되었습니다.')
        this.$router.push({ name: 'DocumentList' })
      } catch (err) {
        // TODO: 공통 에러 처리(토스트/알림) 컨벤션에 맞춰 연결
        console.error('문서 저장 실패', err)
      } finally {
        this.submitting = false
      }
    },
    goBack() {
      this.$router.push({ name: 'DocumentList' })
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

<template>
  <div class="pms-page is-narrow">
    <div class="page-header head-row">
      <div>
        <h1>템플릿 등록</h1>
        <p>{{ currentProject.name }} · 문서 작성에 사용할 템플릿을 등록합니다.</p>
      </div>
      <button type="button" class="btn btn-outline" @click="goBack">← 목록으로</button>
    </div>

    <div class="card">
      <div class="fcard-body">
        <div class="form-row">
          <label for="tpl-name">템플릿명<span class="req">*</span></label>
          <input id="tpl-name" v-model="form.name" class="form-control" placeholder="예) 장애 보고서 템플릿" maxlength="100" />
          <p v-if="errors.name" class="field-error">{{ errors.name }}</p>
        </div>

        <div class="form-row">
          <label>공개 범위</label>
          <div class="radio-group">
            <label><input v-model="form.scope" type="radio" value="프로젝트" /> 프로젝트</label>
            <label><input v-model="form.scope" type="radio" value="공용" /> 공용</label>
          </div>
        </div>

        <div class="form-row">
          <label for="tpl-desc">설명</label>
          <input id="tpl-desc" v-model="form.description" class="form-control" placeholder="템플릿 용도를 간단히 적어주세요." maxlength="200" />
        </div>

        <div class="form-row">
          <label for="tpl-content">본문<span class="req">*</span></label>
          <textarea id="tpl-content" v-model="form.content" class="form-control" rows="12" placeholder="문서 작성 시 기본으로 채워질 내용을 입력해주세요."></textarea>
          <p v-if="errors.content" class="field-error">{{ errors.content }}</p>
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

export default {
  name: 'TemplateFormView',
  mixins: [sampleViewMixin],
  data() {
    return {
      form: { name: '', scope: '프로젝트', description: '', content: '' },
      errors: {},
      submitting: false,
    }
  },
  methods: {
    validate() {
      const e = {}
      if (!this.form.name.trim()) e.name = '템플릿명을 입력해주세요.'
      if (!this.form.content.trim()) e.content = '본문을 입력해주세요.'
      this.errors = e
      return Object.keys(e).length === 0
    },
    async onSubmit() {
      if (!this.validate() || this.submitting) return
      this.submitting = true
      try {
        await sampleStore.addTemplate({ ...this.form, name: this.form.name.trim() })
        sampleStore.setFlash('템플릿이 등록되었습니다.')
        this.$router.push({ name: 'TemplateList' })
      } catch (err) {
        // TODO: 공통 에러 처리(토스트/알림) 컨벤션에 맞춰 연결
        console.error('템플릿 등록 실패', err)
      } finally {
        this.submitting = false
      }
    },
    goBack() {
      this.$router.push({ name: 'TemplateList' })
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

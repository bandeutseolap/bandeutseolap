<template>
  <!-- 사유 입력이 필수인 확인 모달 (엑셀 다운로드, 즉시 물리 삭제, 계정 정지 등) -->
  <div v-if="modelValue" class="modal-overlay" @click.self="close">
    <div class="modal" role="dialog" aria-modal="true" :aria-label="title">
      <div class="modal-header"><h3 class="modal-title">{{ title }}</h3></div>
      <div class="modal-body">
        <p v-if="message" class="modal-message">{{ message }}</p>
        <p v-if="subMessage" class="modal-sub">{{ subMessage }}</p>
        <div class="reason-field">
          <label :for="inputId">사유<span class="req">*</span></label>
          <textarea
            :id="inputId"
            ref="input"
            v-model="reason"
            class="form-control"
            rows="3"
            maxlength="200"
            placeholder="처리 사유를 입력해주세요."
          ></textarea>
          <p v-if="error" class="field-error">{{ error }}</p>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" @click="close">취소</button>
        <button type="button" class="btn" :class="danger ? 'btn-danger' : 'btn-primary'" :disabled="loading" @click="submit">
          {{ loading ? '처리 중...' : confirmText }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
let seq = 0

export default {
  name: 'ReasonModal',
  props: {
    modelValue: { type: Boolean, default: false },
    title: { type: String, default: '사유 입력' },
    message: { type: String, default: '' },
    subMessage: { type: String, default: '' },
    confirmText: { type: String, default: '확인' },
    danger: { type: Boolean, default: false },
    loading: { type: Boolean, default: false },
  },
  emits: ['update:modelValue', 'confirm'],
  data() {
    return { reason: '', error: '', inputId: `reason-${++seq}` }
  },
  watch: {
    // 열릴 때마다 입력값을 비우고 포커스를 준다
    modelValue(open) {
      if (open) {
        this.reason = ''
        this.error = ''
        this.$nextTick(() => this.$refs.input && this.$refs.input.focus())
      }
    },
  },
  methods: {
    close() {
      this.$emit('update:modelValue', false)
    },
    submit() {
      if (!this.reason.trim()) {
        this.error = '사유를 입력해주세요.'
        return
      }
      this.$emit('confirm', this.reason.trim())
    },
  },
}
</script>

<style scoped>
.reason-field { margin-top: var(--space-4); }
.reason-field label {
  display: block;
  margin-bottom: 6px;
  font-size: var(--font-size-xs);
  font-weight: 600;
  color: var(--color-text-soft);
}
.req { margin-left: 2px; color: var(--color-error-500); }
</style>

<template>
  <div class="modal-overlay" @click.self="$emit('cancel')">
    <div class="modal-box">
      <div class="modal-header">
        <h3>{{ title }}</h3>
        <button class="modal-close" @click="$emit('cancel')">✕</button>
      </div>

      <div class="confirm-body">
        <div class="icon-danger">!</div>
        <p>{{ message }}</p>
        <!-- 삭제 대상명 등 강조 표시가 필요한 경우에만 렌더 -->
        <p v-if="highlight"><strong>{{ highlight }}</strong></p>
      </div>

      <div class="modal-footer">
        <button class="btn btn-outline" @click="$emit('cancel')">{{ cancelText }}</button>
        <button class="btn btn-primary" :style="confirmBtnStyle" @click="$emit('confirm')">
          {{ confirmText }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
/**
 * YesNoModal
 * - 삭제/확인 등 단순 Yes/No 판단이 필요한 모든 화면에서 재사용하는 공용 모달.
 * - 이미 프로젝트에 존재한다면 이 파일은 참고용이며, 신규라면 그대로 사용 가능.
 */
export default {
  name: 'ProjectYesNoModal',
  props: {
    title: { type: String, default: '확인' },
    message: { type: String, required: true },
    highlight: { type: String, default: '' },
    confirmText: { type: String, default: '삭제' },
    cancelText: { type: String, default: '취소' },
    // 삭제류 액션이 아닌 일반 확인 모달로도 쓸 수 있도록 danger 여부를 옵션화
    danger: { type: Boolean, default: true },
  },
  emits: ['confirm', 'cancel'],
  computed: {
    confirmBtnStyle() {
      return this.danger ? { background: 'var(--color-danger)' } : {};
    },
  },
};
</script>

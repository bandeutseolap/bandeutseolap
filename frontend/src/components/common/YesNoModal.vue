<template>
  <!-- @click.self는 오버레이 자체를 클릭했을 때만 닫히고, 모달 내부 클릭은 무시 -->
  <div v-if="modelValue" class="modal-overlay" @click.self="$emit('update:modelValue', false)">
    <div class="modal">
      <div class="modal-header">
        <!-- <h3 class="modal-title">게시글 삭제</h3> -->
        <h3 class="modal-title">{{ title }}</h3>
      </div>
      <div class="modal-body">
        <p class="modal-message">{{ message }}</p>
        <p class="modal-sub">{{ subMessage }}</p>
      </div>
      <div class="modal-footer">
        <button class="btn btn-secondary" @click="$emit('update:modelValue', false)">아니오</button>
        <button class="btn btn-danger" :disabled="loading" @click="$emit('confirm')">
          {{ loading ? '처리 중...' : '예' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'YesNoModal',
  props :{
    modelValue: {           // v-model 바인딩 (모달 열림/닫힘)
      type: Boolean,
      default: false,
    },
    title: {
      type: String,
      default: '확인',
    },
    message: {
      type: String,
      required: true,
    },
    subMessage: {
      type: String,
      default: '',
    },
    loading: {
      type: Boolean,
      default: false,
    },
  },
  emits: ['update:modelValue', 'confirm']
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal {
  background: #fff;
  border-radius: 10px;
  width: 380px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  overflow: hidden;
}

.modal-header {
  padding: 20px 24px 0;
}

.modal-title {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0;
}

.modal-body {
  padding: 12px 24px 20px;
}

.modal-message {
  font-size: 14px;
  color: #333;
  margin: 0 0 4px;
}

.modal-sub {
  font-size: 13px;
  color: #888;
  margin: 0;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding: 16px 24px;
  border-top: 1px solid #f0f0f0;
}

.btn-danger {
  background: #e53e3e;
  color: #fff;
  border: none;
  padding: 8px 20px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
}

.btn-danger:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
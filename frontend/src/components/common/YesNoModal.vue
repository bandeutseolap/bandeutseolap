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
/* 오버레이/박스/버튼 기본 스타일은 common.css 의 .modal-overlay .modal .btn-* 사용.
   여기서는 이 모달만의 여백/문구 스타일만 정의한다. */
.modal {
  width: 380px;
}

.modal-header {
  padding: var(--space-5) var(--space-6) 0;
  border-bottom: none;
}

.modal-body {
  padding: var(--space-3) var(--space-6) var(--space-5);
}

.modal-message {
  margin: 0 0 4px;
  font-size: var(--font-size-md);
  color: var(--color-text);
}

.modal-sub {
  margin: 0;
  font-size: var(--font-size-sm);
  color: var(--color-text-muted);
}

.modal-footer {
  padding: var(--space-4) var(--space-6);
}
</style>

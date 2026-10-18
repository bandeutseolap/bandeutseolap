<template>
  <!-- 메시지가 있을 때만 하단에 토스트를 표시한다. 표시/해제 타이밍은 부모가 message 로 제어. -->
  <div v-if="message" class="toast" role="status">{{ message }}</div>
</template>

<script>
export default {
  name: 'AppToast',
  props: {
    message: { type: String, default: '' },
  },
}
</script>

import sampleStore from '@/store/sampleStore'

/**
 * 샘플 화면(멤버/문서/업무/이슈/계정 등) 공통 로직
 * - currentProject : 사이드바에서 선택한 '내 프로젝트' (페이지 부제목에 사용)
 * - showToast      : 하단 토스트 표시 (2초 후 자동 해제)
 * - showFlash      : 등록 화면에서 넘겨준 완료 메시지를 목록 화면에서 한 번만 표시
 */
export default {
  data() {
    return {
      toastMessage: '',
      toastTimer: null,
    }
  },
  computed: {
    currentProject() {
      return sampleStore.getSelectedProject() || { name: '' }
    },
  },
  beforeUnmount() {
    clearTimeout(this.toastTimer)
  },
  methods: {
    showToast(message) {
      this.toastMessage = message
      clearTimeout(this.toastTimer)
      this.toastTimer = setTimeout(() => {
        this.toastMessage = ''
      }, 2000)
    },
    showFlash() {
      const message = sampleStore.consumeFlash()
      if (message) this.showToast(message)
    },
  },
}

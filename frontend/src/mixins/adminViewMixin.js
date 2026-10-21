import sampleViewMixin from '@/mixins/sampleViewMixin'
import adminStore from '@/store/adminStore'

/**
 * 관리자 화면 공통: 토스트 + 업무 오류(BusinessError.code) → 안내 문구 변환
 * (서버 연동 시에도 응답의 오류 코드를 err.code 로 맞춰 주면 화면 수정이 필요 없다)
 */
export default {
  mixins: [sampleViewMixin],
  methods: {
    // 오류 문구는 읽을 시간이 필요하므로 일반 토스트(2초)보다 길게 보여준다
    showToast(message, duration = 2500) {
      this.toastMessage = message
      clearTimeout(this.toastTimer)
      this.toastTimer = setTimeout(() => {
        this.toastMessage = ''
      }, duration)
    },
    fail(err) {
      const message = adminStore.ERROR_MESSAGES[err && err.code]
      if (!message) console.error(err)
      this.showToast(message || '처리 중 오류가 발생했습니다.', 4000)
    },
  },
}

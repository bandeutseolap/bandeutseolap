import { reactive } from 'vue'
import { getAccessToken, removeToken, getLoginId, saveLoginId, removeLoginId } from '@/utils/authToken'

/**
 * 로그인 상태 store (reactive singleton)
 * -----------------------------------------------------------
 * 토큰은 localStorage 에만 있어 Vue 가 변화를 감지하지 못한다.
 * 헤더가 로그인/로그아웃에 즉시 반응하도록 상태를 여기서 한 번 더 들고 있는다.
 *  - 로그인 성공      : authService.fetchUserLogin → login() + 내 정보 조회 → setProfile()
 *  - 로그아웃/401 만료 : logout()
 *  - 다른 탭에서 변경 : storage 이벤트로 sync()
 * -----------------------------------------------------------
 */
const state = reactive({
  loggedIn: !!getAccessToken(),
  loginId: getLoginId() || '',
  // 내 정보(이름/이메일/역할). authService.loadMyProfile() 이 채운다
  profile: null,
})

function login(loginId) {
  saveLoginId(loginId)
  state.loginId = loginId
  state.loggedIn = true
}

function setProfile(profile) {
  state.profile = profile
}

/** 화면 상태만 정리한다. 서버 로그아웃 요청은 authService.requestLogout() 이 담당 */
function logout() {
  removeToken()
  removeLoginId()
  state.loggedIn = false
  state.loginId = ''
  state.profile = null
}

/** localStorage 의 현재 값으로 상태를 다시 맞춘다 */
function sync() {
  const loggedIn = !!getAccessToken()
  const loginId = loggedIn ? getLoginId() || '' : ''
  // 다른 탭에서 다른 계정으로 바뀐 경우 이전 계정의 프로필이 남지 않도록 비운다
  if (loginId !== state.loginId) state.profile = null
  state.loggedIn = loggedIn
  state.loginId = loginId
}

// 다른 탭에서 로그인/로그아웃한 경우도 이 탭의 헤더에 반영
if (typeof window !== 'undefined') {
  window.addEventListener('storage', sync)
}

export default { state, login, setProfile, logout, sync }

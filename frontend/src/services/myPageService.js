import dayjs from 'dayjs'
import sampleStore from '@/store/sampleStore'
import authStore from '@/store/authStore'
import { loadMyProfile } from '@/services/authService'

/**
 * 마이페이지 API 모음
 * -----------------------------------------------------------
 * 백엔드 API 가 없어 샘플 데이터(sampleStore.myPage)를 읽고 쓴다.
 * 실제 API 가 생기면 각 함수의 본문만 TODO 에 적힌 호출로 교체하면 화면은 수정이 필요 없다.
 * 반환값은 복사본이라 화면에서 수정해도 store 가 바뀌지 않는다(서버 응답과 같은 동작).
 * -----------------------------------------------------------
 */
const data = () => sampleStore.state.myPage
const copy = (value) => JSON.parse(JSON.stringify(value))
const now = () => dayjs().format('YYYY-MM-DD HH:mm')

// ---- 내 정보 ----
export async function fetchMyInfo() {
  // TODO(API 연동): const { data } = await api.get('/mypage/profile')
  const profile = await loadMyProfile() // 이름/이메일 (헤더와 같은 소스)
  return { name: profile.name, email: profile.email, ...copy(data().info) }
}

export async function saveMyInfo({ name, email, phone, birthDate, job, nationality, imagePath, imageUrl }) {
  // TODO(API 연동): await api.put('/mypage/profile', { name, email, phone, birthDate, job, nationality, imagePath })
  //                 프로필 이미지는 별도 업로드 API 로 올린 뒤 받은 image_path 를 보낸다
  await sampleStore.saveProfile(authStore.state.loginId || sampleStore.state.currentUser.loginId, { name, email })
  Object.assign(data().info, { phone, birthDate, job, nationality, imagePath, imageUrl })
  await loadMyProfile({ force: true }) // 헤더 프로필에도 바로 반영
}

// ---- 보안 설정 ----
export async function fetchSecurity() {
  // TODO(API 연동): const { data } = await api.get('/mypage/security')
  return copy(data().security)
}

export async function changePassword({ current, next }) {
  // TODO(API 연동): await api.put('/mypage/password', { current, next })
  //                 비밀번호는 store/로그에 남기지 않는다. 현재 비밀번호 검증은 서버가 한다.
  void current
  void next
}

export async function saveMfa(method) {
  // TODO(API 연동): await api.put('/mypage/mfa', { method })  → 서버가 인증을 마친 시각을 내려준다
  const security = data().security
  security.mfaMethod = method
  security.mfaVerifiedAt = method === 'NONE' ? '' : now()
  return copy(security)
}

// ---- 로그인 세션 (Redis) ----
export async function fetchSessions() {
  // TODO(API 연동): const { data } = await api.get('/mypage/sessions')
  return copy(data().sessions)
}

export async function revokeSession(sessionId) {
  // TODO(API 연동): await api.delete(`/mypage/sessions/${sessionId}`)  → 서버가 Redis 세션 삭제
  data().sessions = data().sessions.filter((s) => s.id !== sessionId)
}

export async function revokeAllSessions() {
  // TODO(API 연동): await api.delete('/mypage/sessions')  → 현재 세션 포함 전체 삭제
  data().sessions = []
}

// ---- 동의 관리 ----
export async function fetchConsents() {
  // TODO(API 연동): const { data } = await api.get('/mypage/consents')
  return copy(data().consents)
}

export async function withdrawConsent(consentId) {
  // TODO(API 연동): await api.post(`/mypage/consents/${consentId}/withdraw`)
  const consent = data().consents.find((c) => c.id === consentId)
  if (!consent) return
  consent.agreed = false
  consent.withdrawnAt = now()
}

export async function agreeConsent(consentId) {
  // TODO(API 연동): await api.post(`/mypage/consents/${consentId}/agree`)
  const consent = data().consents.find((c) => c.id === consentId)
  if (!consent) return
  consent.agreed = true
  consent.agreedAt = now()
  consent.withdrawnAt = ''
}

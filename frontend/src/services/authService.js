// API 호출 위한 모듈
import api from '@/services/api/api'
import { saveAccessToken, saveRefreshToken } from '@/utils/authToken'
import authStore from '@/store/authStore'
import sampleStore from '@/store/sampleStore'

// 로그인 API 호출
export async function fetchUserLogin({ loginId, password }) {
    const response = await api.post('/auth/login', {
        loginId,
        password,
    })

    const { accessToken, refreshToken } = response.data

    if (!accessToken || !refreshToken) {
        throw new Error('로그인 응답에 토큰 정보가 없습니다.')
    }

    saveAccessToken(accessToken)
    saveRefreshToken(refreshToken)

    // 헤더가 로그인 상태(프로필)로 전환되도록 알린다
    authStore.login(loginId)

    // 내 정보는 부가 정보이므로 조회에 실패해도 로그인 자체는 성공으로 처리한다
    try {
        await loadMyProfile({ force: true })
    } catch (error) {
        console.error('내 정보 조회 실패', error)
    }
}

// 내 정보 조회 API 호출
// 지금은 백엔드 API 가 없어 샘플 데이터를 돌려준다.
export async function fetchMyProfile() {
    // TODO(API 연동): 아래 샘플 응답을 지우고 실제 API 호출로 교체
    //   const response = await api.get('/auth/me')
    //   return response.data   // { loginId, name, email, role, description }
    const base = sampleStore.state.currentUser
    const loginId = authStore.state.loginId || base.loginId
    const member = sampleStore.state.members.find((m) => m.loginId === loginId)
    const edited = sampleStore.state.profileEdits[loginId] || {}

    return {
        loginId,
        name: edited.name || (loginId === base.loginId ? base.name : loginId),
        email: edited.email || (loginId === base.loginId ? base.email : `${loginId}@example.com`),
        role: member ? member.role : '멤버',
        description: member ? member.desc : '',
    }
}

// 같은 시점에 여러 곳(헤더, 마이페이지)에서 부르더라도 요청은 한 번만 보내기 위한 보관소
let inflightProfile = null

// 내 정보를 조회해 authStore 에 저장한다. 이미 있으면 재사용(force 로 다시 조회)
export async function loadMyProfile({ force = false } = {}) {
    if (!force && authStore.state.profile) return authStore.state.profile
    if (!inflightProfile) {
        inflightProfile = fetchMyProfile()
            .then((profile) => {
                authStore.setProfile(profile)
                return profile
            })
            .finally(() => {
                inflightProfile = null
            })
    }
    return inflightProfile
}

// 로그아웃 API 호출
// 지금은 백엔드 API 가 없어 아무 것도 호출하지 않는다.
export async function requestLogout() {
    // TODO(API 연동): 서버가 Redis 의 토큰을 폐기하도록 요청
    //   await api.post('/auth/logout', { refreshToken: getRefreshToken() })
    // 이 요청이 실패해도 화면 로그아웃은 진행해야 하므로, 호출하는 쪽(ProfileMenu)에서 예외를 처리한다.
}

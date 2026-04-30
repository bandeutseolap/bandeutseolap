// API 호출 위한 모듈
import api from '@/services/api/api'
import { saveAccessToken, saveRefreshToken } from '@/utils/authToken'

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

}

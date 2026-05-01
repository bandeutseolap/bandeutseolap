/* 공통 Axios API 클라이언트 설정 파일 : Vue 프론트엔드에서 백엔드 API를 호출할 때 Access Token을 자동으로 붙이고, 인증 실패 시 로그인 페이지로 보내는 공통 API 통신 모듈 */

import axios from 'axios'
import router from '@/router/index'
import { getAccessToken, removeToken } from '@/utils/authToken'

const api = axios.create({
    baseURL: "/api", // 추후 env 파일로 변경 필요
    timeout: 10000, // 요청 제한 시간
})

// 요청 인터셉터 : API 요청 전 저장된 Access Token을 Authorization 헤더에 추가
api.interceptors.request.use((config) => {
    const token = getAccessToken()

    if (token) {
        config.headers.Authorization = `Bearer ${token}`
    }

    return config
})

// 응답 인터셉터 : API 응답이 401 Unauthorized인 경우 토큰을 제거하고 로그인 화면으로 이동
api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response && error.response.status === 401) {
            removeToken()
            router.push('/auth/login')
        }

        return Promise.reject(error)
    }
)

export default api
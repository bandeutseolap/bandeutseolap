import axios from 'axios'
import router from '@/router/index'
import { getAccessToken, removeAccessToken } from '@/utils/authToken'

const api = axios.create({
    baseURL: "http://localhost:8080/api", // 추후 env 파일로 변경 필요
    timeout: 10000,
})

api.interceptors.request.use((config) => {
    const token = getAccessToken()

    if (token) {
        config.headers.Authorization = `Bearer ${token}`
    }

    return config
})

api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response && error.response.status === 401) {
            removeAccessToken()
            router.push('/auth/login')
        }

        return Promise.reject(error)
    }
)

export default api
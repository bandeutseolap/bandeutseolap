/* 인증 토큰 관리 유틸 */

const ACCESS_TOKEN_KEY = 'APP_ACCESS_TOKEN'; // LocalStorage 에 Access Token 값을 저장하기 위한 Key 값
const REFRESH_TOKEN_KEY = 'APP_REFRESH_TOKEN'; // LocalStorage 에 Refresh Token 값을 저장하기 위한 Key 값

// 로그인 성공 후 발급받은 Access Token 저장
export function saveAccessToken(token) {
    localStorage.setItem(ACCESS_TOKEN_KEY, token)
}

// 로그인 성공 후 발급받은 Refresh Token 저장
export function saveRefreshToken(token) {
    localStorage.setItem(REFRESH_TOKEN_KEY, token)
}

// Access Token 조회
export function getAccessToken() {
    return localStorage.getItem(ACCESS_TOKEN_KEY)
}

// Refresh Token 조회
export function getRefreshToken() {
    return localStorage.getItem(REFRESH_TOKEN_KEY)
}

// Access Token 제거
export function removeAccessToken() {
    localStorage.removeItem(ACCESS_TOKEN_KEY)
}

// Refresh Token 제거
export function removeRefreshToken() {
    localStorage.removeItem(REFRESH_TOKEN_KEY)
}

// 전체 토큰 제거
export function removeToken() {
    removeAccessToken()
    removeRefreshToken()
}
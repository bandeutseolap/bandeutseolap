const TOKEN_KEY = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGVzIjpbIlJPTEVfVVNFUiJdLCJpYXQiOjE3NzY4NjEzODQsImV4cCI6MTc3Njg2NDk4NH0.Te4fakXwMZgnHfIM5IivfdgLn5py1be_uTLra6i12d8'; // Web TokenDTO

export function saveToken(token) {
    localStorage.setItem(TOKEN_KEY, token)
}

export function getAccessToken() {
    return localStorage.getItem(TOKEN_KEY)
}

export function removeAccessToken() {
    localStorage.removeItem(TOKEN_KEY)
}
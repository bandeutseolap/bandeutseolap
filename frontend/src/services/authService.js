// API 호출 위한 모듈
import api from '@/services/api/api'

// Login
export async function fetchUserLogin() {

    // API 호출
    const response = await axios.post('/auth/login', {
        headers: {
            Authorization: `Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGVzIjpbIlJPTEVfVVNFUiJdLCJpYXQiOjE3NzY4NjEzODQsImV4cCI6MTc3Njg2NDk4NH0.Te4fakXwMZgnHfIM5IivfdgLn5py1be_uTLra6i12d8`
        }
    })
    return response.data;
}

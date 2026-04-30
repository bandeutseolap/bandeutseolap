// API 호출 위한 모듈
import api from '@/services/api/api'

// 목록 조회
export async function fetchBoardList() {
    const response = await api.get('/board');
    return response.data;
}

// 상세 조회
export async function fetchBoardDetail(boardId) {
    const response = await api.get(`/board/${boardId}`);
    return response.data;
}
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

// 게시글 작성
export async function createBoard(data) {
    const response = await api.post('/board', data);
    return response.data;
}

// 게시글 수정
export async function updateBoard(boardId, data) {
    const response = await api.put(`/board/${boardId}`, data);
    return response.data;
}

// 게시글 삭제
export async function deleteBoard(boardId) {
  const response = await api.delete(`/board/${boardId}`)
  return response.data;
}
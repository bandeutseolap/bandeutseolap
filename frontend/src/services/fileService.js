// API 호출 위한 모듈
import api from '@/services/api/api'

// 파일 다운로드
export async function fileDownload(boardId, fileId) {
    const response = await api.get(`/board/${boardId}/files/${fileId}`, {
      responseType: 'blob'
    });
    return response;
}

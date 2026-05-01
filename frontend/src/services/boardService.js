import { boardList } from './mock/boardList.mock.js'
import { boardContent } from './mock/boardContent.mock.js'
// API 호출 위한 모듈
import axios from 'axios'

// 목록 조회
export async function fetchBoardList() {

    return {
        data: boardList,
        totalCount: boardList.length,
    }
    /* API 호출 전환 시
    const response = await axios.get('/api/boards')
    return response.data
    */
}

// 상세 조회
export async function fetchBoardDetail(boardId) {

    const id = Number(boardId)

    const boardItem = boardList.find(item => item.id === id)
    const boardDetail = boardContent.find(item => item.boardId === id)

    if (!boardItem) {
        throw new Error('게시글을 찾을 수 없습니다.')
    }

    return {
        data: {
            ...boardItem,
            content: boardDetail ? boardDetail.content : '',
        },
    }
    /* API 호출 전환 시
    const response = await axios.get(`/api/boards/${boardId}`)
    return response.data
    */
}
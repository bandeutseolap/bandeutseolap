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
    const formData = new FormData();

    // files 제외한 나머지 데이터를 "request" 키로 JSON Blob 전송
    // files = [File, File]
    // requestData = { title: '제목', content: '내용', visibleYn: true }
    const { files, ...requestData } = data

    // Blob
    // → 문자열을 application/json 타입의 바이너리 데이터로 변환
    // → 서버가 @RequestPart로 JSON 파싱 가능하게 함
    formData.append(
      'request',
      new Blob([JSON.stringify(requestData)], { type: 'application/json' })
    )

    // 파일 별도 append
    if (files && files.length > 0) {
      files.forEach((file) => formData.append('files', file))
    }

    /*
      {
        "title":"위지위그 적용 완료",
        "content":"<h2>파일 첨부 기능 개발 중 ..</h2><p></p>",
        "visibleYn":true,
        "fixedTopYn":false,
        "noticeYn":false,
        "boardAreaCd":"SERVICE",
        "openTargetCd":"ALL"
        ---------------------
        "files":[
          {},{}
        ]
      }
    */

    /*
    formData.append('title', '111');
    formData.append('content', '파일 첨부 기능 개발 중')
    formData.append('visibleYn', true)
    formData.append('fixedTopYn', false)
    formData.append('noticeYn', false)
    formData.append('boardAreaCd', 'SERVICE')
    formData.append('openTargetCd', 'ALL')
    */


    // ✅ 전송 전 전체 값 확인
    console.log('===== formData 전송 데이터 =====')
    for (let [key, value] of formData.entries()) {
      console.log(`${key}:`, value)
    }
    console.log('================================')

    const response = await api.post('/board', formData,
    {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    }
    );
    //const response = await api.post('/board', data);
    return response.data;
}

// 게시글 수정
export async function updateBoard(boardId, data) {
    // data => formData
    console.log("updateBoard data" + JSON.stringify(data) );

    const formData = new FormData();

    const { files, ...requestData } = data

      // Blob
      // → 문자열을 application/json 타입의 바이너리 데이터로 변환
      // → 서버가 @RequestPart로 JSON 파싱 가능하게 함
      formData.append(
        'request',
        new Blob([JSON.stringify(requestData)], { type: 'application/json' })
      )

      // 파일 별도 append
      if (files && files.length > 0) {
        files.forEach((file) => formData.append('files', file))
      }

    const response = await api.put(`/board/${boardId}`, formData,
    {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    }
    );
    return response.data;
}

// 게시글 삭제
export async function deleteBoard(boardId) {
  const response = await api.delete(`/board/${boardId}`)
  return response.data;
}
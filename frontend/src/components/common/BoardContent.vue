<script>
import { fileDownload } from '@/services/fileService'
import { Editor, EditorContent } from '@tiptap/vue-3'
import StarterKit from '@tiptap/starter-kit'
import Image from '@tiptap/extension-image'
//import { Table } from '@tiptap/extension-table'
import { TextStyle } from '@tiptap/extension-text-style'
import Color from '@tiptap/extension-color'
import Highlight from '@tiptap/extension-highlight'
import TextAlign from '@tiptap/extension-text-align'

export default {
  name: 'BoardContent',
  components: {
    EditorContent,  // 반드시 등록
  },
  props :{
    modelValue: {
      type: String,
      default: '',
    },
    editable: {
      type: Boolean,
      default: false
    },
    initialAttachments: {
      type: Array,
      default: () => []
    }
  },
  emits: ['update:modelValue', 'update:attachments'],
  data() {
    return {
      editor : null,
      attachments : [...this.initialAttachments],
      deleteFileIds: [], // 삭제할 파일 ID 추적
      downloadStatus: null,
    }
  },
  computed: {
    sanitizedContent() {
      // modelValue(board.content)를 그대로 반환
      return this.modelValue
    },
    normalizedAttachments() {
      return this.attachments.map((file) => ({
        name: file.name || file.originFileName,  // 둘 중 있는 것 사용
        size: file.size || file.fileSize,
        statCd: file.fileStatusCd,
        // 원본 데이터 보존
        raw: file,
      }))
    }
  },
  mounted() {
    this.editor = new Editor({
      content: this.modelValue,
      extensions: [
        StarterKit,
        Image.configure({
          allowBase64: true,  // base64 이미지 허용해야 수정 모드에서 이미지 조회됨
        }),
        //Table,
        TextStyle,
        Color,
        Highlight,
        TextAlign.configure({ types: ['heading', 'paragraph'] }),
      ],
      editable: true,
      enablePasteRules: false,
      editorProps: {
          handlePaste: (view, event) => { // 이미지 붙여넣기
            const items = Array.from(event.clipboardData?.items || [])
              const imageItem = items.find((item) => item.type.startsWith('image/'))

              if (imageItem) {
                event.preventDefault()
                const file = imageItem.getAsFile()
                this.insertImageFromFile(file)
                return true // 기본 붙여넣기 동작 막음
              }
              console.log(event);
              return false // 이미지 아니면 기본 동작 진행
          },
      },
      onUpdate: ({ editor }) => {
        //console.log('타이핑됨:', editor.getHTML()) // 여기서 확인
        this.$emit('update:modelValue', editor.getHTML()) // 변경된 HTML을 부모(board.content)에 전달
      },
    })
  },
  beforeUnmount() {
    this.editor?.destroy()
  },
  methods: {
    triggerFileInput() {
     // console.log("triggerFileInput: " + JSON.stringify(this.$refs.fileInput))
      this.$refs.fileInput.click()
    },
    handleFileChange(e) {
      const files = Array.from(e.target.files)

      files.forEach((file) => {
        console.log('선택된 파일:', file)
        this.attachments.push(file)
        this.$emit('update:attachments',this.attachments)
      })

      e.target.value = ''
    },
    // 파일 사이즈 체크 - 파일 크기 포맷 (1024 → 1KB)
    formatSize(bytes) {
      // B
      if(bytes < 1024) return bytes + 'B'
      // KB
      if(bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + 'KB'
      // MB (1024B => 1KB, 1024KB => 1MB)
      return (bytes / (1024*1024)).toFixed(1) + 'MB'
    },
    // TODO: 파일 전송 상태
    // 파일 삭제
    removeAttachment(index) {
      console.log(this.attachments)
      console.log(index)
      if(index === undefined) {
        this.attachments.forEach((file) => {
          if(file.fileId) this.deleteFileIds.push(file.fileId)
        })
        this.attachments.splice(0, this.attachments.length);
      } else {
        const fileId = this.attachments[index].fileId
        if(fileId) this.deleteFileIds.push(fileId)
        this.attachments.splice(index, 1); // 교체/삭제 or 없으면 추가
      }
      console.log(this.deleteFileIds)
      this.$emit('update:attachments', this.attachments)
      this.$emit('update:deleteFileIds', this.deleteFileIds)
    },
    // TODO: 전체 파일 다운로드: GET /board/{boardId}/files/download (zip)
    // zip으로 묶어서 다운로드
    async downloadAttachment(index) {
      try {
        this.downloadStatus = 'loading'

        const boardId = this.$route.params.id
        const fileId = this.attachments[index].fileId
        const fileName = this.attachments[index].originFileName
        const response = await fileDownload(boardId, fileId);

        // 데이터를 다운로드로 처리
        const url = window.URL.createObjectURL(new Blob([response.data])) // 브라우저 메모리 안에서 접근 가능한 임시 URL을 생성
        //console.log("url " + url)
        const link = document.createElement('a')
        link.href = url
        link.setAttribute('download', fileName)
        document.body.appendChild(link)
        link.click()
        link.remove()
        window.URL.revokeObjectURL(url) // 메모리에서 해제

        this.downloadStatus = null // 성공 시 패널 닫기
      } catch (err) {
        console.error('다운로드 실패:', err)
        this.downloadStatus = 'error'
        setTimeout(() => {
          this.downloadStatus = null
        }, 3000)
      } finally {
          console.log("3 " + this.downloadStatus)
      }
    },
    // TODO: 이미지 본문 삽입( 추천 : api.post('/file/image-upload' )
    insertImageFromFile(file) {
        const reader = new FileReader()
        reader.onload = (e) => {
          this.editor.chain().focus().setImage({ src: e.target.result }).run()
        }
        reader.readAsDataURL(file)

        // 첨부파일 목록에도 추가하고 싶다면
        // this.attachments.push(file)
        // this.$emit('update:attachments', this.attachments)
      },
  },
  // watch는 부모에서 값이 바뀔 때만 실행
  watch: {
    /*
      타이핑 → onUpdate → $emit → 부모 board.content 변경
      → props modelValue 변경 → watch 실행
    */
    modelValue(newVal) {
      console.log("watch :" + this.editor.getHTML());
      /*if (this.editor && this.editor.getHTML() !== newVal) {
        this.editor.commands.setContent(newVal, false)
      }*/
    }
  }
}
</script>

<template>
  <div>
    <div class="board-content-text">
      <!-- 툴바 옵션 -->
      <div v-if="editable" class="toolbar">
        <button @click="editor.chain().focus().toggleBold().run()">B</button>
        <button @click="editor.chain().focus().toggleItalic().run()">I</button>
        <button @click="editor.chain().focus().toggleStrike().run()">S</button>
        <button @click="editor.chain().focus().toggleHeading({ level: 1 }).run()">H1</button>
        <button @click="editor.chain().focus().toggleHeading({ level: 2 }).run()">H2</button>
        <button @click="editor.chain().focus().toggleBulletList().run()">UL</button>
        <button @click="editor.chain().focus().toggleOrderedList().run()">OL</button>
        <!-- 파일 첨부 버튼 -->
        <button @click="triggerFileInput">📎 파일첨부</button>
        <input
          ref="fileInput"
          type="file"
          multiple
          style="display: none"
          @change="handleFileChange"
        />
      </div>

      <! --{{ modelValue }} -->
      <!--
        - 텍스트 선택 후 B / I / S 버튼으로 서식 적용
        - H1 / H2 로 제목 변환
        - 목록 버튼으로 리스트 생성
        - 파일첨부 버튼으로 이미지/파일 첨부 테스트
       -->
      <!-- data()의 editor 인스턴스 연결 -->
      <editor-content
        v-if="editable"
        :editor="editor"
        class="tiptap-editor"
      />
      <!-- 뷰어 모드 -->
      <div
        v-else
        v-html="sanitizedContent"
        class="viewer"
      />
      <!-- 파일 첨부 시, 첨부 파일 목록  -->
      <div v-if="attachments.length > 0 && editable" class="attachments">
        <p class="attachments-title">첨부파일 ({{ attachments.length }})
          <button v-if="editable" @click="removeAttachment()">모두 삭제</button>
        </p>
        <ul v-if="editable" class="attachments-ul">
          <li v-for="(file, index) in normalizedAttachments" :key="index" class="attachment-item">
            <span>📄 {{ file.name }}</span>
            <div class="attachment-etc">
              <span class="att-status">{{ file.statCd }}</span>
              <span class="file-size">{{ formatSize(file.size) }}</span>
              <button v-if="editable" @click="removeAttachment(index)" class="attachment-button">✕</button>
            </div>
          </li>
        </ul>
      </div>
      <!-- 뷰어 모드 -->
      <div v-if="attachments.length > 0 && !editable" class="attachments-viewer">
        <p class="attachments-title">첨부파일 ({{ attachments.length }})
          <!-- TODO: 파일 모두 다운로드 기능  -->
          <button @click="">모두 저장</button>
        </p>
        <ul class="attachments-ul">
          <li v-for="(file, index) in attachments" :key="index" class="attachment-item">
            <label class="attachment-name">📄 {{ file.originFileName }}</label>
            <div class="attachment-etc">
              <!-- TODO: 파일 만료 기간 -->
              <!-- TODO: 파일 상태 한글로 -->
              <span class="att-status">{{ file.fileStatusCd }}</span>
              <span class="file-size">{{ formatSize(file.fileSize) }}</span>
              <button class="attachment-button" @click="downloadAttachment(index)">다운로드</button>
            </div>
          </li>
        </ul>
        <!-- 다운로드 상태 패널 -->
        <div v-if="downloadStatus === 'loading'" class="download-panel loading">
          <i class="ti ti-loader-2" aria-hidden="true"></i>
          파일 다운로드 중..
        </div>

        <div v-if="downloadStatus === 'error'" class="download-panel error">
          <i class="ti ti-alert-circle" aria-hidden="true"></i>
          파일 다운로드에 실패하였습니다
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.toolbar {
  display: flex;
  gap: 4px;
  padding: 8px;
  border: 1px solid var(--color-border-strong);;
  border-bottom: none;
  border-radius: 4px 4px 0 0;
  background: #f5f7f9;
}
.toolbar button {
  padding: 4px 8px;
  border: 1px solid #ccc;
  border-radius: 3px;
  background: #fff;
  cursor: pointer;
}
.toolbar button:hover {
  background: #e9e9e9;
}
.viewer {
  min-height: 200px;
  padding: 12px 12px 12px 25px;
}
.tiptap-editor {
  border: 1px solid #ddd;
  border-radius: 0 0 4px 4px;
  min-height: 200px;
  padding: 12px;
}
:deep(.ProseMirror) {
  outline: none;          /* 검은 테두리 제거 */
  padding-left: 12px;     /* 내부 여백 추가 */
  min-height: 200px;
  line-height: 1;
}

/* focus 시 테두리 색만 변경 (검은색 제거) */
:deep(.tiptap-editor) {
  border: 0.5px solid #ddd;
  //border-top: none;
  border-radius: 0 0 4px 4px;
}

:deep(.tiptap-editor:focus-within) {
  border-color: #4a9eff;  /* 포커스 시 파란색 테두리 */
  outline: none;
}
.attachments {
  margin-top: 8px;
  border: 1px solid #ddd;   /* 에디터와 동일한 테두리 */
  border-radius: 4px;        /* 에디터와 동일한 radius */
  background: #fff;          /* 흰 배경으로 통일 */
  overflow: hidden
}
.attachments-viewer {
  margin: 20px;
  border: 1px solid #ddd;   /* 에디터와 동일한 테두리 */
  border-radius: 4px;        /* 에디터와 동일한 radius */
  background: #fff;          /* 흰 배경으로 통일 */
  overflow: hidden
}
.attachments-ul {
  padding-left: 25px;
}
.attachment-etc {
  margin-left: auto;
}
.attachments-title {
  font-size: 14px;
  font-weight: 500;
  color: #555;
  padding: 8px 12px;
  background: #f5f7f9;       /* 툴바와 동일한 배경 */
  border-bottom: 1px solid #ddd;
  margin: 0;
}
.attachments-title button {
  border: none;
  background: none;
  cursor: pointer;
  color: #999;
  font-size: 14px;
  padding: 0 4px;
  line-height: 1;
}
.attachment-item {
  display: flex;
  align-items: center;
  //gap: 8px;
  padding: 8px 12px 12px 0px;
  font-size: 14px;
  border-bottom: 1px solid #f0f0f0;
  color: #333;
}

.attachment-item:last-child {
  border-bottom: none;
}
/* 파일 아이콘
.attachment-item i {
  font-size: 16px;
  color: var(--color-text-secondary);
  flex-shrink: 0;
}*/
.att-status {
  width: 50px;        /* 고정 너비 */
  text-align: right;  /* 오른쪽 정렬 */
  padding: 0px 15px;
  font-size: 13px;
  color: #999;
  flex-shrink: 0;
}

.file-size {
  padding: 0px 15px;
  color: var(--color-text-tertiary);
  font-size: 13px;
}

.attachment-name {
  border: none;
  background: none;
  //cursor: pointer;
  color: var(--color-text-tertiary);
  font-size: 14px;
  padding: 0 4px;
  line-height: 1;
}

.attachment-button {
  border: none;
  background: none;
  cursor: pointer;
  color: #999;
  font-size: 14px;
  padding: 0 4px;
  line-height: 1;
}

/*.attachment-item button {
  border: none;
  background: none;
  cursor: pointer;
  color: #999;
  font-size: 14px;
  padding: 0 4px;
  line-height: 1;
}*/

.attachment-item button:hover {
  color: #e24b4a;
}

.download-panel {
  position: relative;
  /* bottom: 24px; */
  /* right: 24px; */
  padding: 12px 20px;
  border-radius: 8px;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
  z-index: 1000;
}

.download-panel.loading {
  background: #e6f1fb;
  color: #185fa5;
}

.download-panel.error {
  background: #fcebeb;
  color: #a32d2d;
}

.download-panel i {
  font-size: 16px;
}

.loading i {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>
<script>
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
      editor : null, // mounted()에서 Editor 인스턴스로 교체됨
      attachments : [...this.initialAttachments]
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
        Image,
        //Table,
        TextStyle,
        Color,
        Highlight,
        TextAlign.configure({ types: ['heading', 'paragraph'] }),
      ],
      editable: true,
      enablePasteRules: false,
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
      //console.log("triggerFileInput: " + JSON.stringify(this.$refs.fileInput))
      this.$refs.fileInput.click()
    },
    handleFileChange(e) {
      const files = Array.from(e.target.files)
      console.log('선택된 파일:', files) // 1. 파일 선택 확인

      files.forEach((file) => {
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
      if(!index) {
        this.attachments.splice(0, this.attachments.length);
      } else {
        this.attachments.splice(index, 1); // 교체/삭제 or 없으면 추가
      }
      this.$emit('update:attachments', this.attachments)
    }
    // TODO: 이미지 본문 삽입
  },
  watch: {
    /*
      타이핑 → onUpdate → $emit → 부모 board.content 변경
      → props modelValue 변경 → watch 실행
    */
    modalValue(newVal){
      // TODO watch는 부모에서 값이 바뀔 때만 실행 - 수정 취소 후 원본으로 되돌리기, 임시저장 불러오기
      console.log('watch newVal:  ', newVal)
      //if(this.editor && this.editor.getHTML() != newVal){
        //setContent
      //}
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
              <!-- TODO: 파일 전송 상태 -->
              <span class="file-size">대기중</span>
              <span class="file-size">{{ formatSize(file.size) }}</span>
              <button v-if="editable" @click="removeAttachment(index)">✕</button>
            </div>
          </li>
        </ul>
      </div>
      <!-- 뷰어 모드 -->
      <div v-if="attachments.length > 0 && !editable" class="attachments-viewer">
        <p class="attachments-title">첨부파일 ({{ attachments.length }})
          <button @click="">모두 저장</button>
        </p>
        <ul class="attachments-ul">
          <li v-for="(file, index) in attachments" :key="index" class="attachment-item">
            <!-- TODO: 글자 클릭 시 다운로드  -->
            <span>📄 {{ file.originFileName }}</span>
            <div class="attachment-etc">
              <span class="file-size">{{ formatSize(file.fileSize) }}</span>
              <!-- TODO: 다운로드 아이콘 -->
              <button @click="">다운로드</button>
            </div>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<style scoped>
.toolbar {
  display: flex;
  gap: 4px;
  padding: 8px;
  border: 1px solid #ddd;
  border-bottom: none;
  border-radius: 4px 4px 0 0;
  background: #f9f9f9;
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
  border-top: none;
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
  background: #f9f9f9;       /* 툴바와 동일한 배경 */
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

.file-size {
  padding: 0px 15px;
  color: #999;
  font-size: 13px;
}

.attachment-item button {
  border: none;
  background: none;
  cursor: pointer;
  color: #999;
  font-size: 14px;
  padding: 0 4px;
  line-height: 1;
}

.attachment-item button:hover {
  color: #e24b4a;
}
</style>
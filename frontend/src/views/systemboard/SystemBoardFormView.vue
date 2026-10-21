<template>
  <div class="pms-page is-narrow">
    <div class="page-header head-row">
      <div>
        <h1>{{ isEdit ? '게시글 수정' : '글쓰기' }}</h1>
        <p>시스템 게시판 › {{ area.label }}</p>
      </div>
      <button type="button" class="btn btn-outline" @click="goBack">← 취소</button>
    </div>

    <div v-if="forbidden" class="card"><div class="empty-state">{{ forbiddenMessage }}</div></div>

    <div v-else class="card">
      <div class="fcard-body">
        <div class="form-row">
          <label>구분</label>
          <div><span class="chip chip-common">{{ area.label }}</span> <span class="field-hint inline">탭에서 자동으로 지정됩니다.</span></div>
        </div>

        <div class="form-row">
          <label for="sb-title">제목<span class="req">*</span></label>
          <input id="sb-title" v-model="form.title" class="form-control" maxlength="200" />
          <p class="field-hint right">{{ form.title.length }} / 200</p>
          <p v-if="errors.title" class="field-error">{{ errors.title }}</p>
        </div>

        <div class="form-row">
          <label for="sb-content">본문<span class="req">*</span></label>
          <textarea id="sb-content" v-model="form.content" class="form-control" rows="12"></textarea>
          <p v-if="errors.content" class="field-error">{{ errors.content }}</p>
          <p v-else-if="isEdit" class="field-hint">본문을 수정하면 새 버전(v{{ (board ? board.currentVersion : 1) + 1 }})으로 저장됩니다.</p>
        </div>

        <!-- 첨부 -->
        <div class="form-row">
          <label>첨부파일</label>
          <ul v-if="existingFiles.length" class="file-list">
            <li v-for="f in existingFiles" :key="f.fileId" :class="{ removed: removeIds.includes(f.fileId) }">
              <span>📎 {{ f.originFileName }} <span class="text-muted">({{ formatBytes(f.fileSize) }})</span></span>
              <button type="button" class="btn btn-text" @click="toggleRemove(f.fileId)">{{ removeIds.includes(f.fileId) ? '삭제 취소' : '삭제' }}</button>
            </li>
          </ul>
          <ul v-if="newFiles.length" class="file-list">
            <li v-for="(f, i) in newFiles" :key="f.name + i"><span>🆕 {{ f.name }} <span class="text-muted">({{ formatBytes(f.size) }})</span></span><button type="button" class="btn btn-text" @click="newFiles.splice(i, 1)">제거</button></li>
          </ul>
          <button type="button" class="btn btn-outline btn-sm" @click="$refs.picker.click()">파일 선택</button>
          <input ref="picker" type="file" multiple class="hidden-input" tabindex="-1" aria-hidden="true" @change="onPick" />
          <p v-if="errors.file" class="field-error">{{ errors.file }}</p>
          <p v-else class="field-hint">최대 {{ MAX_FILES }}개, 파일당 {{ MAX_MB }}MB까지. (샘플: 실제 업로드는 하지 않고 파일 정보만 등록합니다)</p>
        </div>

        <div class="form-grid-2">
          <div class="form-row">
            <label for="sb-open">열람 대상<span class="req">*</span></label>
            <select id="sb-open" v-model="form.openTargetCd" class="form-control" :disabled="areaKey === 'free'">
              <option v-for="(m, c) in openTargets" :key="c" :value="c">{{ m.label }}</option>
            </select>
            <p v-if="areaKey === 'free'" class="field-hint">자유게시판은 로그인 사용자로 고정됩니다.</p>
          </div>
          <div class="form-row option-col">
            <label>옵션</label>
            <label v-if="areaKey === 'notice' || isAdmin" class="check"><input v-model="form.fixedTopYn" type="checkbox" /> 상단 고정 <span class="text-muted">(영역당 최대 {{ fixedMax }}개)</span></label>
            <label v-if="areaKey === 'notice'" class="check"><input v-model="form.noticeYn" type="checkbox" /> 알림 발송 <span class="text-muted">(게시 시 전체 사용자)</span></label>
            <label v-if="areaKey === 'notice' || isAdmin" class="check"><input v-model="form.visibleYn" type="checkbox" /> 노출</label>
            <p v-if="areaKey === 'free' && !isAdmin" class="field-hint">상단 고정과 노출 여부는 관리자만 지정할 수 있습니다.</p>
          </div>
        </div>
      </div>

      <div class="fcard-footer">
        <button type="button" class="btn btn-outline" @click="goBack">취소</button>
        <button type="button" class="btn btn-secondary" :disabled="submitting" @click="submit('TEMP')">임시저장</button>
        <button type="button" class="btn btn-primary" :disabled="submitting" @click="submit('PUBLISHED')">{{ submitting ? '저장 중...' : '게시' }}</button>
      </div>
    </div>
    <AppToast :message="toastMessage" />
  </div>
</template>

<script>
import AppToast from '@/components/common/AppToast.vue'
import adminStore from '@/store/adminStore'
import sampleStore from '@/store/sampleStore'
import adminViewMixin from '@/mixins/adminViewMixin'
import { BOARD_AREAS, OPEN_TARGETS, formatBytes } from '@/utils/adminMeta'

const MAX_FILES = 5
const MAX_MB = 50

export default {
  name: 'SystemBoardFormView',
  components: { AppToast },
  mixins: [adminViewMixin],
  data() {
    return {
      MAX_FILES, MAX_MB, openTargets: OPEN_TARGETS,
      form: { title: '', content: '', openTargetCd: 'ALL', fixedTopYn: false, noticeYn: false, visibleYn: true },
      newFiles: [], removeIds: [], errors: {}, submitting: false, loaded: false,
    }
  },
  computed: {
    isEdit() { return !!this.$route.params.boardId },
    board() { return this.isEdit ? adminStore.getBoard(Number(this.$route.params.boardId)) : null },
    areaKey() {
      if (this.isEdit) return this.board && this.board.areaCd === 'SYS_FREE' ? 'free' : 'notice'
      return this.$route.query.area === 'free' ? 'free' : 'notice'
    },
    area() { return BOARD_AREAS[this.areaKey] },
    isAdmin() { return adminStore.isAdmin() },
    fixedMax() { return adminStore.state.systemVariables.BOARD_FIXED_MAX },
    existingFiles() { return this.board ? this.board.files : [] },
    forbidden() { return this.isEdit ? !this.board || !adminStore.canEditBoard(this.board) : !adminStore.canWriteIn(this.areaKey) },
    forbiddenMessage() { return this.isEdit && !this.board ? '게시글을 찾을 수 없습니다.' : '작성(수정) 권한이 없습니다.' },
  },
  created() {
    if (this.board) {
      const b = this.board
      this.form = { title: b.title, content: b.content, openTargetCd: b.openTargetCd, fixedTopYn: b.fixedTopYn, noticeYn: b.noticeYn, visibleYn: b.visibleYn }
    }
    if (this.areaKey === 'free') this.form.openTargetCd = 'LOGIN'
  },
  methods: {
    formatBytes,
    toggleRemove(id) { this.removeIds = this.removeIds.includes(id) ? this.removeIds.filter((x) => x !== id) : [...this.removeIds, id] },
    onPick(event) {
      const picked = Array.from(event.target.files || [])
      event.target.value = ''
      const keptExisting = this.existingFiles.filter((f) => !this.removeIds.includes(f.fileId)).length
      if (keptExisting + this.newFiles.length + picked.length > MAX_FILES) { this.errors = { ...this.errors, file: `첨부파일은 최대 ${MAX_FILES}개까지 등록할 수 있습니다.` }; return }
      if (picked.some((f) => f.size > MAX_MB * 1024 * 1024)) { this.errors = { ...this.errors, file: `파일당 ${MAX_MB}MB를 넘을 수 없습니다.` }; return }
      this.errors = { ...this.errors, file: '' }
      this.newFiles.push(...picked.map((f) => ({ name: f.name, size: f.size })))
    },
    async submit(statusCd) {
      const e = {}
      if (!this.form.title.trim()) e.title = '제목을 입력해주세요.'
      if (statusCd === 'PUBLISHED' && !this.form.content.trim()) e.content = '게시하려면 본문을 입력해주세요.' // 임시저장은 본문 없이도 가능
      this.errors = { ...e, file: this.errors.file }
      if (Object.keys(e).length || this.submitting) return
      this.submitting = true
      const payload = { ...this.form, title: this.form.title.trim(), areaKey: this.areaKey, postStatusCd: statusCd, attachments: this.newFiles, removeFileIds: this.removeIds }
      try {
        let boardId
        if (this.isEdit) { boardId = this.board.boardId; await adminStore.updateBoard(boardId, payload) }
        else boardId = await adminStore.createBoard(payload)
        sampleStore.setFlash(statusCd === 'TEMP' ? '임시저장했습니다.' : this.isEdit ? '게시글을 수정했습니다.' : '게시글을 등록했습니다.')
        if (statusCd === 'TEMP') this.$router.push({ name: 'SystemBoardList', query: { area: this.areaKey } })
        else this.$router.push({ name: 'SystemBoardDetail', params: { boardId }, query: { area: this.areaKey } })
      } catch (err) { this.fail(err) } finally { this.submitting = false }
    },
    goBack() {
      if (this.isEdit && this.board) this.$router.push({ name: 'SystemBoardDetail', params: { boardId: this.board.boardId }, query: { area: this.areaKey } })
      else this.$router.push({ name: 'SystemBoardList', query: { area: this.areaKey } })
    },
  },
}
</script>

<style scoped>
.field-hint.inline { display: inline; margin-left: var(--space-2); }
.field-hint.right { text-align: right; }
.hidden-input { display: none; }
.file-list { margin: 0 0 var(--space-2); padding: 0; list-style: none; }
.file-list li { display: flex; align-items: center; justify-content: space-between; gap: var(--space-2); padding: 5px var(--space-3); border: 1px solid var(--color-border); border-radius: var(--radius-md); margin-bottom: 4px; font-size: var(--font-size-sm); }
.file-list li.removed { background: var(--color-error-50); color: var(--color-text-faint); text-decoration: line-through; }
.option-col .check { display: flex; align-items: center; gap: 8px; margin: 0 0 8px; font-size: var(--font-size-sm); font-weight: 500; color: var(--color-text); cursor: pointer; }
</style>

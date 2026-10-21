<template>
  <div class="pms-page">
    <div class="page-header">
      <h1>파일 탐색기</h1>
      <p>관리자 › 스토리지 관리 · 전체 파일 {{ rows.length }}건</p>
    </div>

    <div class="explorer" :class="{ 'has-detail': detail }">
      <section class="card">
        <form class="search-panel" @submit.prevent="search">
          <div class="search-grid">
            <div class="form-row"><label for="sf-name">원본 파일명</label><input id="sf-name" v-model="form.name" class="form-control" /></div>
            <div class="form-row"><label for="sf-ext">확장자</label><input id="sf-ext" v-model="form.ext" class="form-control" placeholder="pdf" /></div>
            <div class="form-row">
              <label for="sf-mime">MIME 유형</label>
              <select id="sf-mime" v-model="form.mime" class="form-control"><option value="">전체</option><option v-for="m in mimes" :key="m" :value="m">{{ m }}</option></select>
            </div>
            <div class="form-row">
              <label for="sf-status">파일 상태</label>
              <select id="sf-status" v-model="form.statusCd" class="form-control"><option value="">전체</option><option v-for="(m, c) in fileStatus" :key="c" :value="c">{{ m.label }}</option></select>
            </div>
            <div class="form-row">
              <label for="sf-target">연결 대상 유형</label>
              <select id="sf-target" v-model="form.targetType" class="form-control">
                <option value="">전체</option><option v-for="(m, c) in targetTypes" :key="c" :value="c">{{ m.label }}</option><option value="NONE">연결 없음</option>
              </select>
            </div>
            <div class="form-row">
              <label for="sf-project">프로젝트</label>
              <select id="sf-project" v-model="form.projectId" class="form-control"><option value="">전체</option><option v-for="p in projects" :key="p.projectId" :value="p.projectId">{{ p.name }}</option></select>
            </div>
            <div class="form-row"><label for="sf-up">업로더</label><input id="sf-up" v-model="form.uploader" class="form-control" /></div>
            <div class="form-row">
              <label for="sf-size">크기 구간</label>
              <select id="sf-size" v-model="form.sizeRange" class="form-control"><option value="">전체</option><option v-for="r in sizeRanges" :key="r.code" :value="r.code">{{ r.label }}</option></select>
            </div>
            <div class="form-row span-2">
              <label>업로드 기간</label>
              <div class="range-inputs">
                <input v-model="form.from" type="date" class="form-control" aria-label="업로드 시작일" /><span>~</span><input v-model="form.to" type="date" class="form-control" aria-label="업로드 종료일" />
              </div>
            </div>
          </div>
          <div class="search-actions">
            <button type="button" class="btn btn-outline" @click="reset">초기화</button>
            <button type="submit" class="btn btn-primary">검색</button>
          </div>
        </form>

        <div class="bulk-bar">
          <span class="bulk-count">{{ selected.length }}건 선택</span>
          <button type="button" class="btn btn-outline-error btn-sm" :disabled="!selected.length" @click="confirmSchedule = true">삭제 예정 처리</button>
        </div>

        <div class="table-wrap">
          <table class="table-nowrap">
            <thead>
              <tr>
                <th class="col-check"><input type="checkbox" :checked="allOnPage" aria-label="현재 페이지 전체 선택" @change="togglePage($event.target.checked)" /></th>
                <th>원본 파일명</th><th>확장자</th><th>크기</th><th>MIME</th><th>상태</th><th>연결 대상</th><th>파일 역할</th><th>업로더</th><th>업로드일시</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="pageRows.length === 0" class="empty-row"><td colspan="10">조건에 맞는 파일이 없습니다.</td></tr>
              <tr v-for="f in pageRows" :key="f.fileId" :class="{ 'row-active': detail && detail.fileId === f.fileId }">
                <td class="col-check"><input v-model="selected" type="checkbox" :value="f.fileId" :aria-label="`${f.originFileName} 선택`" /></td>
                <td><button type="button" class="link-btn" @click="openDetail(f)">{{ f.originFileName }}</button></td>
                <td class="mono">{{ f.fileExt }}</td>
                <td>{{ formatBytes(f.fileSize) }}</td>
                <td class="mime">{{ f.mimeType }}</td>
                <td><span class="chip" :class="fileStatus[f.fileStatusCd].chip">{{ fileStatus[f.fileStatusCd].label }}</span></td>
                <td>{{ f.targetLabel }}</td>
                <td>{{ f.fileRoleCd ? fileRole[f.fileRoleCd] : '-' }}</td>
                <td>{{ f.uploadedBy }}</td>
                <td>{{ f.uploadedAt }}</td>
              </tr>
            </tbody>
          </table>
        </div>
        <PaginationBar v-model:page="page" :total="rows.length" :size="size" />
      </section>

      <!-- 상세 패널: storage_key 는 이 관리자 화면에서만 노출한다 -->
      <aside v-if="detail" class="card detail-panel" aria-label="파일 상세">
        <div class="card-head">
          <span class="card-title">파일 상세</span>
          <button type="button" class="btn btn-ghost btn-sm" aria-label="상세 닫기" @click="detail = null">닫기</button>
        </div>
        <dl class="detail-list">
          <div><dt>원본 파일명</dt><dd>{{ detail.originFileName }}</dd></div>
          <div><dt>상태</dt><dd><span class="chip" :class="fileStatus[detail.fileStatusCd].chip">{{ fileStatus[detail.fileStatusCd].label }}</span></dd></div>
          <div><dt>크기 / MIME</dt><dd>{{ formatBytes(detail.fileSize) }} · {{ detail.mimeType }}</dd></div>
          <div><dt>저장소 키</dt><dd class="mono">{{ detail.storageKey }}</dd></div>
          <div><dt>저장 파일명</dt><dd class="mono">{{ detail.storedFileName }}</dd></div>
          <div><dt>파일 해시</dt><dd class="mono">{{ detail.fileHash }}</dd></div>
          <div><dt>업로드</dt><dd>{{ detail.uploadedBy }} · {{ detail.uploadedAt }}</dd></div>
          <div><dt>삭제 예정일시</dt><dd>{{ detail.deleteScheduledAt || '-' }}</dd></div>
          <div><dt>물리 삭제</dt><dd>{{ detail.physicalDeletedAt ? `완료 · ${detail.physicalDeletedAt}` : '아니오' }}</dd></div>
        </dl>
        <div class="detail-sub">연결 이력</div>
        <ul v-if="detail.links.length" class="link-history">
          <li v-for="(l, i) in detail.links" :key="i">
            <strong>[{{ targetTypes[l.targetType].label }}] {{ l.targetName }}</strong>
            <span v-if="l.targetDeleted" class="chip chip-error">대상 삭제됨</span>
            <span class="cell-sub">{{ l.attachedBy }} · {{ l.attachedAt }}</span>
          </li>
        </ul>
        <p v-else class="detail-empty">연결된 대상이 없습니다.</p>

        <div class="detail-actions">
          <button type="button" class="btn btn-outline btn-sm" :disabled="detail.fileStatusCd === 'DELETED'" @click="onDownload">다운로드</button>
          <button type="button" class="btn btn-outline-error btn-sm" :disabled="!['ACTIVE', 'UPLOADING'].includes(detail.fileStatusCd)" @click="scheduleOne">삭제 예정 처리</button>
          <button type="button" class="btn btn-outline btn-sm" :disabled="detail.fileStatusCd !== 'DELETE_SCHEDULED'" @click="onCancel">삭제 취소</button>
        </div>
        <p class="notice-box security">
          <code>storage_key</code>는 저장 경로를 그대로 드러내므로 관리자 상세에서만 보여주고, 일반 API 응답에는 포함하지 않습니다.
          다운로드는 항상 <code>file_id</code> 기반 엔드포인트에서 권한을 검사한 뒤 제공해야 합니다.
        </p>
      </aside>
    </div>

    <YesNoModal v-model="confirmSchedule" title="삭제 예정 처리" :message="`선택한 ${selected.length}건을 삭제 예정으로 처리할까요?`" :sub-message="`${graceDays}일 유예 후 배치가 물리 삭제합니다. 그 전에는 삭제 취소할 수 있습니다.`" @confirm="scheduleSelected" />
    <AppToast :message="toastMessage" />
  </div>
</template>

<script>
import AppToast from '@/components/common/AppToast.vue'
import PaginationBar from '@/components/common/PaginationBar.vue'
import YesNoModal from '@/components/common/YesNoModal.vue'
import adminStore from '@/store/adminStore'
import adminViewMixin from '@/mixins/adminViewMixin'
import { FILE_STATUS, TARGET_TYPES, FILE_ROLE, SIZE_RANGES, formatBytes } from '@/utils/adminMeta'

const blank = () => ({ name: '', ext: '', mime: '', statusCd: '', targetType: '', projectId: '', uploader: '', sizeRange: '', from: '', to: '' })

export default {
  name: 'StorageFilesView',
  components: { AppToast, PaginationBar, YesNoModal },
  mixins: [adminViewMixin],
  data() {
    // 프로젝트별 사용량의 '파일 목록' 링크(?projectId=)로 들어오면 해당 프로젝트로 필터를 미리 채운다
    const form = { ...blank(), projectId: this.$route.query.projectId ? Number(this.$route.query.projectId) : '' }
    return { fileStatus: FILE_STATUS, targetTypes: TARGET_TYPES, fileRole: FILE_ROLE, sizeRanges: SIZE_RANGES, form, applied: { ...form }, page: 1, size: 10, selected: [], detailId: null, confirmSchedule: false }
  },
  computed: {
    projects() { return adminStore.state.projects },
    mimes() { return [...new Set(adminStore.state.files.map((f) => f.mimeType))] },
    graceDays() { return adminStore.state.systemVariables.DELETE_GRACE_DAYS },
    rows() { return adminStore.searchFiles(this.applied) },
    pageRows() { return this.rows.slice((this.page - 1) * this.size, this.page * this.size) },
    allOnPage() { return this.pageRows.length > 0 && this.pageRows.every((f) => this.selected.includes(f.fileId)) },
    // 상세는 id 로 다시 찾아 항상 최신 상태를 보여준다
    detail: {
      get() { return this.detailId ? adminStore.searchFiles({}).find((f) => f.fileId === this.detailId) || null : null },
      set(v) { this.detailId = v ? v.fileId : null },
    },
  },
  methods: {
    formatBytes,
    search() { this.applied = { ...this.form }; this.page = 1; this.selected = [] },
    reset() { this.form = blank(); this.search() },
    togglePage(checked) {
      const ids = this.pageRows.map((f) => f.fileId)
      this.selected = checked ? [...new Set([...this.selected, ...ids])] : this.selected.filter((id) => !ids.includes(id))
    },
    openDetail(f) { this.detailId = f.fileId },
    async onDownload() {
      // TODO(API 연동): file_id 기반 다운로드 엔드포인트(권한 검사 후 스트리밍 또는 짧은 만료의 서명 URL)로 교체. 지금은 이력만 남긴다.
      await adminStore.logFileDownload(this.detail.fileId)
      this.showToast('다운로드 이력을 남겼습니다. (샘플: 실제 파일은 내려받지 않습니다)')
    },
    async scheduleOne() { await this.schedule([this.detail.fileId]) },
    async scheduleSelected() { this.confirmSchedule = false; await this.schedule(this.selected); this.selected = [] },
    async schedule(ids) {
      const n = await adminStore.scheduleDelete(ids)
      this.showToast(n ? `${n}건을 삭제 예정으로 처리했습니다.` : '삭제 예정으로 바꿀 수 있는 파일이 없습니다. (사용 중/업로드 중 파일만 가능)')
    },
    async onCancel() {
      try { await adminStore.cancelDelete(this.detail.fileId); this.showToast('삭제를 취소했습니다.') } catch (err) { this.fail(err) }
    },
  },
}
</script>

<style scoped>
.explorer { display: grid; grid-template-columns: minmax(0, 1fr); gap: var(--space-4); align-items: start; }
.explorer.has-detail { grid-template-columns: minmax(0, 1fr) 360px; }
.row-active td { background: var(--color-primary-50); }
.mime { max-width: 150px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; font-size: 11.5px; color: var(--color-text-muted); }
.detail-panel { position: sticky; top: 0; }
.detail-list { margin: 0; padding: var(--space-2) var(--space-4); }
.detail-list > div { padding: 8px 0; border-bottom: 1px solid var(--color-border); }
.detail-list dt { margin-bottom: 2px; font-size: 11.5px; font-weight: 600; color: var(--color-text-muted); }
.detail-list dd { margin: 0; font-size: var(--font-size-sm); }
.detail-sub { padding: var(--space-3) var(--space-4) 0; font-size: var(--font-size-sm); font-weight: 700; }
.link-history { margin: 0; padding: var(--space-2) var(--space-4); list-style: none; }
.link-history li { padding: 6px 0; font-size: var(--font-size-sm); }
.detail-empty { margin: 0; padding: var(--space-2) var(--space-4); font-size: var(--font-size-sm); color: var(--color-text-muted); }
.detail-actions { display: flex; flex-wrap: wrap; gap: var(--space-2); padding: var(--space-3) var(--space-4); }
.security { margin: 0 var(--space-4) var(--space-4); font-size: 12px; }
@media (max-width: 1100px) { .explorer.has-detail { grid-template-columns: minmax(0, 1fr); } .detail-panel { position: static; } }
</style>

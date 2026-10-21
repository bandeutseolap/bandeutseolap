<template>
  <div class="pms-page">
    <div class="page-header">
      <h1>정합성 점검</h1>
      <p>관리자 › 스토리지 관리 · 고아 파일 / 끊긴 참조 / 중복 파일</p>
    </div>

    <section class="card">
      <div class="tabs inner-tabs" role="tablist">
        <button v-for="t in tabs" :key="t.key" type="button" role="tab" class="tab-btn" :class="{ active: tab === t.key }" :aria-selected="tab === t.key" @click="changeTab(t.key)">
          {{ t.label }} ({{ t.count }})
        </button>
      </div>

      <!-- 고아 파일 -->
      <template v-if="tab === 'orphans'">
        <p class="notice-box tab-note">
          <code>UPLOADING</code> 상태로 {{ ttl }}시간(<code>ORPHAN_FILE_TTL_HOURS</code>)이 지난 파일, 또는 어떤 연결 테이블에도 연결되지 않은 <code>ACTIVE</code> 파일입니다.
        </p>
        <div class="bulk-bar"><span class="bulk-count">{{ selected.length }}건 선택</span><button type="button" class="btn btn-outline-error btn-sm" :disabled="!selected.length" @click="confirmOpen = true">일괄 삭제 예정 처리</button></div>
        <div class="table-wrap">
          <table class="table-nowrap">
            <thead><tr><th class="col-check"><input type="checkbox" :checked="allChecked(report.orphans)" aria-label="전체 선택" @change="toggleAll(report.orphans, $event.target.checked)" /></th><th>파일명</th><th>상태</th><th>크기</th><th>업로더</th><th>업로드일시</th><th>경과 시간</th></tr></thead>
            <tbody>
              <tr v-if="!report.orphans.length" class="empty-row"><td colspan="7">고아 파일이 없습니다.</td></tr>
              <tr v-for="f in report.orphans" :key="f.fileId">
                <td class="col-check"><input v-model="selected" type="checkbox" :value="f.fileId" :aria-label="`${f.originFileName} 선택`" /></td>
                <td><strong>{{ f.originFileName }}</strong></td>
                <td><span class="chip" :class="fileStatus[f.fileStatusCd].chip">{{ fileStatus[f.fileStatusCd].label }}</span></td>
                <td>{{ formatBytes(f.fileSize) }}</td><td>{{ f.uploadedBy }}</td><td>{{ f.uploadedAt }}</td><td>{{ f.elapsedHours }}시간</td>
              </tr>
            </tbody>
          </table>
        </div>
      </template>

      <!-- 끊긴 참조 -->
      <template v-else-if="tab === 'dangling'">
        <p class="notice-box tab-note">연결 테이블에는 연결이 남아 있지만, 연결된 대상(게시글/문서/프로젝트)이 Soft Delete 된 파일입니다.</p>
        <div class="bulk-bar"><span class="bulk-count">{{ selected.length }}건 선택</span><button type="button" class="btn btn-outline-error btn-sm" :disabled="!selected.length" @click="confirmOpen = true">삭제 예정 처리</button></div>
        <div class="table-wrap">
          <table class="table-nowrap">
            <thead><tr><th class="col-check"><input type="checkbox" :checked="allChecked(report.dangling)" aria-label="전체 선택" @change="toggleAll(report.dangling, $event.target.checked)" /></th><th>파일명</th><th>크기</th><th>대상 유형</th><th>대상</th><th>대상 ID</th></tr></thead>
            <tbody>
              <tr v-if="!report.dangling.length" class="empty-row"><td colspan="6">끊긴 참조가 없습니다.</td></tr>
              <tr v-for="f in report.dangling" :key="f.fileId">
                <td class="col-check"><input v-model="selected" type="checkbox" :value="f.fileId" :aria-label="`${f.originFileName} 선택`" /></td>
                <td><strong>{{ f.originFileName }}</strong></td><td>{{ formatBytes(f.fileSize) }}</td>
                <td>{{ targetTypes[brokenLink(f).targetType].label }}</td><td>{{ brokenLink(f).targetName }} <span class="chip chip-error">삭제됨</span></td><td class="mono">{{ brokenLink(f).targetId }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </template>

      <!-- 중복 파일 -->
      <template v-else>
        <p class="notice-box tab-note">파일 해시(<code>file_hash</code>)가 같은 파일 그룹입니다. 조회 전용이며, 향후 중복 제거 저장 정책을 판단하는 자료로 씁니다.</p>
        <div class="table-wrap">
          <table class="table-nowrap">
            <thead><tr><th>해시</th><th>파일 수</th><th>중복 용량</th><th>파일 목록</th></tr></thead>
            <tbody>
              <tr v-if="!report.duplicates.length" class="empty-row"><td colspan="4">중복 파일이 없습니다.</td></tr>
              <tr v-for="g in report.duplicates" :key="g.hash">
                <td class="mono">{{ g.hash }}</td><td>{{ g.count }}개</td><td>{{ formatBytes(g.wastedSize) }}</td>
                <td><span v-for="f in g.files" :key="f.fileId" class="dup-file">{{ f.originFileName }} <span class="text-muted">({{ f.targetLabel }})</span></span></td>
              </tr>
            </tbody>
          </table>
        </div>
      </template>
    </section>

    <YesNoModal v-model="confirmOpen" title="삭제 예정 처리" :message="`선택한 ${selected.length}건을 삭제 예정으로 처리할까요?`" :sub-message="`${graceDays}일 유예 후 물리 삭제되며, 그 전에는 삭제 취소할 수 있습니다.`" @confirm="onSchedule" />
    <AppToast :message="toastMessage" />
  </div>
</template>

<script>
import AppToast from '@/components/common/AppToast.vue'
import YesNoModal from '@/components/common/YesNoModal.vue'
import adminStore from '@/store/adminStore'
import adminViewMixin from '@/mixins/adminViewMixin'
import { FILE_STATUS, TARGET_TYPES, formatBytes } from '@/utils/adminMeta'

export default {
  name: 'StorageIntegrityView',
  components: { AppToast, YesNoModal },
  mixins: [adminViewMixin],
  data() { return { tab: 'orphans', selected: [], confirmOpen: false, fileStatus: FILE_STATUS, targetTypes: TARGET_TYPES } },
  computed: {
    report() { return adminStore.integrityReport() },
    ttl() { return adminStore.state.systemVariables.ORPHAN_FILE_TTL_HOURS },
    graceDays() { return adminStore.state.systemVariables.DELETE_GRACE_DAYS },
    tabs() {
      return [
        { key: 'orphans', label: '고아 파일', count: this.report.orphans.length },
        { key: 'dangling', label: '끊긴 참조', count: this.report.dangling.length },
        { key: 'duplicates', label: '중복 파일', count: this.report.duplicates.length },
      ]
    },
  },
  methods: {
    formatBytes,
    changeTab(key) { this.tab = key; this.selected = [] },
    brokenLink(f) { return f.links.find((l) => l.targetDeleted) },
    allChecked(list) { return list.length > 0 && list.every((f) => this.selected.includes(f.fileId)) },
    toggleAll(list, checked) { this.selected = checked ? list.map((f) => f.fileId) : [] },
    async onSchedule() {
      const n = await adminStore.scheduleDelete(this.selected)
      this.confirmOpen = false
      this.selected = []
      this.showToast(`${n}건을 삭제 예정으로 처리했습니다.`)
    },
  },
}
</script>

<style scoped>
.inner-tabs { margin: 0; padding: 0 var(--space-4); }
.tab-note { margin: var(--space-3) var(--space-4) 0; }
.dup-file { display: block; font-size: var(--font-size-sm); line-height: 1.7; }
</style>

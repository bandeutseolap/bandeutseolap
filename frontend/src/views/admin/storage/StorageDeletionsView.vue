<template>
  <div class="pms-page">
    <div class="page-header">
      <h1>삭제 예정 / 물리 삭제 관리</h1>
      <p>관리자 › 스토리지 관리 · 삭제 유예 {{ graceDays }}일</p>
    </div>

    <div class="stack">
      <!-- 배치 상태 -->
      <section class="kpi-row kpi-3">
        <div class="kpi-card"><div class="kpi-label">마지막 물리 삭제 배치</div><div class="kpi-val kpi-small">{{ batch.lastRunAt }}</div><div class="kpi-sub">실행일시</div></div>
        <div class="kpi-card"><div class="kpi-label">처리 건수</div><div class="kpi-val">{{ batch.processed }}건</div><div class="kpi-sub is-ok">정상 삭제</div></div>
        <div class="kpi-card"><div class="kpi-label">실패 건수</div><div class="kpi-val">{{ batch.failed }}건</div><div class="kpi-sub" :class="{ 'is-danger': batch.failed }">{{ batch.failed ? '로그 확인 필요' : '실패 없음' }}</div></div>
      </section>

      <section class="card">
        <div class="tabs inner-tabs" role="tablist">
          <button type="button" role="tab" class="tab-btn" :class="{ active: tab === 'scheduled' }" :aria-selected="tab === 'scheduled'" @click="tab = 'scheduled'">삭제 예정 ({{ scheduled.length }})</button>
          <button type="button" role="tab" class="tab-btn" :class="{ active: tab === 'deleted' }" :aria-selected="tab === 'deleted'" @click="tab = 'deleted'">물리 삭제 완료 ({{ deleted.length }})</button>
        </div>

        <div class="table-wrap">
          <table class="table-nowrap">
            <thead>
              <tr>
                <th>원본 파일명</th><th>크기</th><th>연결 대상</th><th>삭제 예정일시</th><th>남은 기간</th><th>물리 삭제</th><th>물리 삭제일시</th>
                <th v-if="tab === 'scheduled'" class="col-actions">관리</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="rows.length === 0" class="empty-row"><td :colspan="tab === 'scheduled' ? 8 : 7">대상 파일이 없습니다.</td></tr>
              <tr v-for="f in rows" :key="f.fileId">
                <td><strong>{{ f.originFileName }}</strong></td>
                <td>{{ formatBytes(f.fileSize) }}</td>
                <td>{{ f.targetLabel }} <span v-if="f.links.some((l) => l.targetDeleted)" class="chip chip-error">대상 삭제됨</span></td>
                <td>{{ f.deleteScheduledAt || '-' }}</td>
                <td><span v-if="f.daysLeft !== null" class="chip" :class="f.daysLeft <= 1 ? 'chip-error' : 'chip-warn'">D-{{ Math.max(f.daysLeft, 0) }}</span><span v-else>-</span></td>
                <td><span class="chip" :class="f.physicalDeletedAt ? 'chip-error' : 'badge-muted'">{{ f.physicalDeletedAt ? '완료' : '대기' }}</span></td>
                <td>{{ f.physicalDeletedAt || '-' }}</td>
                <td v-if="tab === 'scheduled'" class="col-actions">
                  <button type="button" class="btn btn-text" @click="onCancel(f)">삭제 취소</button>
                  <button type="button" class="btn btn-danger-text" @click="askPurge(f)">즉시 물리 삭제</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>

      <p class="notice-box">
        삭제 취소는 연결된 대상이 Soft Delete 상태가 <strong>아닐 때만</strong> 가능합니다. 즉시 물리 삭제는 되돌릴 수 없어 사유 입력과 2차 확인을 거치며, 이력으로 남습니다.
      </p>
    </div>

    <!-- 즉시 물리 삭제: 사유 → 2차 확인 -->
    <ReasonModal
      v-model="reasonOpen" title="즉시 물리 삭제" :message="target ? `'${target.originFileName}' 파일을 즉시 삭제합니다.` : ''"
      sub-message="저장소의 실제 파일이 삭제되며 복구할 수 없습니다." confirm-text="다음" danger @confirm="onReason"
    />
    <YesNoModal v-model="finalOpen" title="최종 확인" :message="target ? `'${target.originFileName}' 을(를) 정말 삭제할까요?` : ''" sub-message="이 작업은 되돌릴 수 없습니다." @confirm="onPurge" />
    <AppToast :message="toastMessage" />
  </div>
</template>

<script>
import AppToast from '@/components/common/AppToast.vue'
import ReasonModal from '@/components/common/ReasonModal.vue'
import YesNoModal from '@/components/common/YesNoModal.vue'
import adminStore from '@/store/adminStore'
import adminViewMixin from '@/mixins/adminViewMixin'
import { formatBytes } from '@/utils/adminMeta'

export default {
  name: 'StorageDeletionsView',
  components: { AppToast, ReasonModal, YesNoModal },
  mixins: [adminViewMixin],
  data() { return { tab: 'scheduled', target: null, reasonOpen: false, finalOpen: false, reason: '' } },
  computed: {
    graceDays() { return adminStore.state.systemVariables.DELETE_GRACE_DAYS },
    batch() { return adminStore.state.deleteBatch },
    all() { return adminStore.searchFiles({}) },
    scheduled() { return this.all.filter((f) => f.fileStatusCd === 'DELETE_SCHEDULED').sort((a, b) => a.deleteScheduledAt.localeCompare(b.deleteScheduledAt)) },
    deleted() { return this.all.filter((f) => f.fileStatusCd === 'DELETED') },
    rows() { return this.tab === 'scheduled' ? this.scheduled : this.deleted },
  },
  methods: {
    formatBytes,
    async onCancel(f) {
      try { await adminStore.cancelDelete(f.fileId); this.showToast('삭제를 취소했습니다. 파일이 사용 중 상태로 복원되었습니다.') } catch (err) { this.fail(err) }
    },
    askPurge(f) { this.target = f; this.reasonOpen = true },
    onReason(reason) { this.reason = reason; this.reasonOpen = false; this.finalOpen = true },
    async onPurge() {
      await adminStore.purgeFile(this.target.fileId, this.reason)
      this.finalOpen = false
      this.showToast('파일을 물리 삭제했습니다.')
    },
  },
}
</script>

<style scoped>
.kpi-3 { grid-template-columns: repeat(3, minmax(0, 1fr)); }
.kpi-small { font-size: 20px; letter-spacing: -0.5px; }
.inner-tabs { margin: 0; padding: 0 var(--space-4); }
@media (max-width: 700px) { .kpi-3 { grid-template-columns: minmax(0, 1fr); } }
</style>

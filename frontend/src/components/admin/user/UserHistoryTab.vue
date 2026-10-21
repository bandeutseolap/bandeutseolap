<template>
  <section class="card">
    <div class="card-head"><span class="card-title">변경이력 <span class="text-muted">조회 전용</span></span></div>
    <div class="table-wrap">
      <table>
        <thead><tr><th>작업일시</th><th>작업자</th><th>작업유형</th><th>변경 필드 (이전 → 이후)</th><th>사유</th></tr></thead>
        <tbody>
          <tr v-if="rows.length === 0" class="empty-row"><td colspan="5">변경 이력이 없습니다.</td></tr>
          <tr v-for="h in rows" :key="h.histId">
            <td>{{ h.workedAt }}</td><td>{{ h.workedBy }}</td>
            <td><span class="chip badge-muted">{{ h.actionType }}</span></td>
            <td>
              <span v-if="!h.changes.length" class="text-muted">-</span>
              <span v-for="c in h.changes" :key="c.field" class="cell-sub-block"><code>{{ c.field }}</code> {{ c.before }} → <strong>{{ c.after }}</strong></span>
            </td>
            <td>{{ h.reason || '-' }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>

<script>
import adminStore from '@/store/adminStore'

export default {
  name: 'UserHistoryTab',
  props: { user: { type: Object, required: true } },
  computed: { rows() { return adminStore.historyOf(this.user.userId) } },
}
</script>

<style scoped>
.cell-sub-block { display: block; font-size: var(--font-size-xs); line-height: 1.7; }
code { padding: 1px 5px; border-radius: var(--radius-sm); background: var(--color-surface-muted); font-size: 11.5px; }
</style>

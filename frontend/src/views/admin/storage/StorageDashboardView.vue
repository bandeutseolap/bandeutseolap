<template>
  <div class="pms-page">
    <div class="page-header">
      <h1>스토리지 대시보드</h1>
      <p>관리자 › 스토리지 관리 · 파일 사용 현황 요약</p>
    </div>

    <div class="stack">
      <!-- 요약 카드 -->
      <section class="kpi-row kpi-5">
        <div v-for="c in cards" :key="c.label" class="kpi-card">
          <div class="kpi-label">{{ c.label }}</div>
          <div class="kpi-val">{{ c.value }}</div>
          <div class="kpi-sub" :class="c.tone">{{ c.sub }}</div>
        </div>
      </section>

      <div class="two-col">
        <!-- 영역별 사용량 -->
        <section class="card">
          <div class="card-head"><span class="card-title">영역별 사용량</span></div>
          <div class="donut-wrap">
            <div class="donut" :style="{ background: donutGradient }" role="img" :aria-label="donutLabel"><div class="donut-hole"><strong>{{ formatBytes(summary.totalSize) }}</strong><span>전체</span></div></div>
            <ul class="legend">
              <li v-for="s in areaSlices" :key="s.key">
                <span class="swatch" :style="{ background: s.color }"></span>
                <span class="legend-name">{{ s.label }}</span>
                <span class="legend-val">{{ formatBytes(s.size) }} · {{ s.percent }}%</span>
              </li>
            </ul>
          </div>
        </section>

        <!-- 상위 프로젝트 -->
        <section class="card">
          <div class="card-head"><span class="card-title">사용량 TOP 10 프로젝트</span><router-link :to="{ name: 'StorageProjects' }" class="btn btn-ghost btn-sm">전체 보기</router-link></div>
          <ul class="top-list">
            <li v-for="p in top" :key="p.projectId">
              <div class="top-line"><span class="top-name">{{ p.name }}</span><span class="top-val">{{ formatBytes(p.activeFileTotalSize) }} / {{ formatBytes(p.quota) }}</span></div>
              <div class="progress-track" role="progressbar" :aria-valuenow="Math.min(p.percent, 100)" aria-valuemin="0" aria-valuemax="100" :aria-label="`${p.name} 쿼터 사용률`">
                <div class="progress-fill" :class="p.percent >= 100 ? 'danger' : p.percent >= 80 ? 'warn' : ''" :style="{ width: Math.min(p.percent, 100) + '%' }"></div>
              </div>
              <span class="top-pct" :class="{ over: p.percent >= 80 }">{{ p.percent }}%</span>
            </li>
          </ul>
        </section>
      </div>

      <!-- 업로드 추이 -->
      <section class="card">
        <div class="card-head">
          <span class="card-title">업로드 추이 (최근 30일)</span>
          <span class="text-muted trend-sum">총 {{ trendTotal.count }}건 · {{ formatBytes(trendTotal.size) }}</span>
        </div>
        <div class="trend" role="img" :aria-label="`최근 30일 업로드 ${trendTotal.count}건`">
          <div v-for="d in trend" :key="d.date" class="trend-col" :title="`${d.date} · ${d.count}건 · ${formatBytes(d.size)}`">
            <div class="trend-bar" :style="{ height: Math.max(4, (d.count / trendMax) * 100) + '%' }"></div>
            <span class="trend-label">{{ d.date.endsWith('01') || d === trend[0] || d === trend[trend.length - 1] ? d.date : '' }}</span>
          </div>
        </div>
      </section>

      <!-- 확장자 분포 -->
      <section class="card">
        <div class="card-head"><span class="card-title">확장자 분포</span></div>
        <div class="table-wrap">
          <table>
            <thead><tr><th>확장자</th><th>파일 수</th><th>용량</th><th class="bar-col">비중</th></tr></thead>
            <tbody>
              <tr v-for="e in exts" :key="e.ext">
                <td class="mono">.{{ e.ext }}</td><td>{{ e.count }}</td><td>{{ formatBytes(e.size) }}</td>
                <td class="bar-col"><div class="progress-track"><div class="progress-fill" :style="{ width: (e.size / summary.totalSize) * 100 + '%' }"></div></div></td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>

      <p class="notice-box">
        대시보드 집계는 <code>app_file</code> 전체를 스캔합니다. 파일 수가 많아지면 일 단위 집계 테이블(<code>dm_storage_daily</code>)을 배치로 쌓아 두고 이 화면은 그 테이블을 읽도록 바꾸세요.
        업로드 추이는 그 방식을 가정한 샘플 데이터입니다.
      </p>
    </div>
  </div>
</template>

<script>
import adminStore from '@/store/adminStore'
import { formatBytes, TARGET_TYPES } from '@/utils/adminMeta'

const COLORS = ['var(--color-primary-600)', 'var(--color-success-500)', 'var(--color-warning-500)', 'var(--color-accent-500)', 'var(--color-neutral-300)']

export default {
  name: 'StorageDashboardView',
  computed: {
    summary() { return adminStore.storageSummary() },
    cards() {
      const s = this.summary
      return [
        { label: '전체 파일', value: `${s.totalCount}건`, sub: '삭제 완료 제외', tone: '' },
        { label: '전체 용량', value: formatBytes(s.totalSize), sub: '삭제 완료 제외', tone: 'is-primary' },
        { label: '활성 용량', value: formatBytes(s.activeSize), sub: '사용 중 파일', tone: 'is-ok' },
        { label: '삭제 예정 용량', value: formatBytes(s.scheduledSize), sub: '유예 기간 후 물리 삭제', tone: s.scheduledSize ? 'is-warn' : '' },
        { label: '업로드 중 (고아 후보)', value: `${s.uploadingCount}건`, sub: '정합성 점검에서 확인', tone: s.uploadingCount ? 'is-danger' : '' },
      ]
    },
    areaSlices() {
      const usage = adminStore.usageByArea()
      const total = Object.values(usage).reduce((n, a) => n + a.size, 0) || 1
      return Object.entries(usage).map(([key, v], i) => ({
        key, size: v.size, color: COLORS[i], label: key === 'NONE' ? '연결 없음' : TARGET_TYPES[key].label, percent: Math.round((v.size / total) * 1000) / 10,
      }))
    },
    // conic-gradient: '색 시작% 끝%' 구간을 이어 붙여 도넛 차트를 그린다
    donutGradient() {
      let at = 0
      const stops = this.areaSlices.filter((s) => s.size > 0).map((s) => { const from = at; at += s.percent; return `${s.color} ${from}% ${at}%` })
      return `conic-gradient(${stops.join(', ') || 'var(--color-neutral-200) 0 100%'})`
    },
    donutLabel() { return this.areaSlices.map((s) => `${s.label} ${s.percent}%`).join(', ') },
    top() { return adminStore.topProjects(10) },
    trend() { return adminStore.state.uploadTrend },
    trendMax() { return Math.max(...this.trend.map((d) => d.count), 1) },
    trendTotal() { return { count: this.trend.reduce((n, d) => n + d.count, 0), size: this.trend.reduce((n, d) => n + d.size, 0) } },
    exts() { return adminStore.extDistribution() },
  },
  methods: { formatBytes },
}
</script>

<style scoped>
.kpi-5 { grid-template-columns: repeat(5, minmax(0, 1fr)); }
.donut-wrap { display: flex; align-items: center; gap: var(--space-6); padding: var(--space-5); }
.donut { position: relative; flex-shrink: 0; width: 170px; height: 170px; border-radius: 50%; }
.donut-hole {
  position: absolute; inset: 28px; display: flex; flex-direction: column; align-items: center; justify-content: center;
  border-radius: 50%; background: var(--color-surface); font-size: 12px; color: var(--color-text-muted);
}
.donut-hole strong { font-size: var(--font-size-md); color: var(--color-text); }
.legend { flex: 1; min-width: 0; margin: 0; padding: 0; list-style: none; }
.legend li { display: flex; align-items: center; gap: var(--space-2); padding: 5px 0; font-size: var(--font-size-sm); }
.swatch { width: 10px; height: 10px; flex-shrink: 0; border-radius: 3px; }
.legend-name { flex: 1; }
.legend-val { font-variant-numeric: tabular-nums; color: var(--color-text-soft); }

.top-list { margin: 0; padding: var(--space-3) var(--space-4); list-style: none; }
.top-list li { display: grid; grid-template-columns: minmax(0, 1fr) 48px; gap: 2px var(--space-3); padding: 7px 0; }
.top-line { display: flex; justify-content: space-between; gap: var(--space-2); grid-column: 1 / 2; font-size: var(--font-size-sm); }
.top-name { overflow: hidden; white-space: nowrap; text-overflow: ellipsis; font-weight: 600; }
.top-val { flex-shrink: 0; font-size: 11.5px; color: var(--color-text-muted); }
.top-list .progress-track { grid-column: 1 / 2; }
.top-pct { grid-row: 1 / 3; grid-column: 2; align-self: center; text-align: right; font-size: 12px; font-weight: 700; font-variant-numeric: tabular-nums; }
.top-pct.over { color: var(--color-warning-700); }
.progress-fill.warn { background: var(--color-warning-500); }
.progress-fill.danger { background: var(--color-error-500); }

.trend { display: flex; align-items: flex-end; gap: 4px; height: 170px; padding: var(--space-4) var(--space-4) 24px; }
.trend-col { position: relative; display: flex; flex: 1; flex-direction: column; justify-content: flex-end; height: 100%; min-width: 0; }
.trend-bar { width: 100%; border-radius: 3px 3px 0 0; background: var(--color-primary-400); transition: background-color 0.12s ease; }
.trend-col:hover .trend-bar { background: var(--color-primary-700); }
.trend-label { position: absolute; bottom: -20px; left: 50%; transform: translateX(-50%); font-size: 10px; white-space: nowrap; color: var(--color-text-muted); }
.trend-sum { font-size: var(--font-size-xs); }
.bar-col { width: 34%; }

@media (max-width: 1100px) { .kpi-5 { grid-template-columns: repeat(3, minmax(0, 1fr)); } }
@media (max-width: 700px) {
  .kpi-5 { grid-template-columns: repeat(2, minmax(0, 1fr)); }
  .donut-wrap { flex-direction: column; align-items: flex-start; }
}
</style>

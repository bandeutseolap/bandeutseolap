<template>
  <div class="page-content">
    <div class="page-header">
      <h1>대시보드</h1>
      <p>프로젝트와 수행단계 현황을 한눈에 확인합니다.</p>
    </div>

    <!-- KPI -->
    <section class="kpi-row">
      <div v-for="kpi in kpis" :key="kpi.label" class="kpi-card">
        <div class="kpi-label">{{ kpi.label }}</div>
        <div class="kpi-val">{{ kpi.value }}</div>
        <div class="kpi-sub" :class="kpi.tone">{{ kpi.sub }}</div>
      </div>
    </section>

    <div class="two-col">
      <!-- 프로젝트 현황 -->
      <section class="card">
        <div class="card-head">
          <span class="card-title">프로젝트 현황</span>
          <router-link to="/projects" class="btn btn-ghost btn-sm">전체 보기</router-link>
        </div>

        <ul v-if="projectRows.length" class="project-list">
          <li v-for="p in projectRows" :key="p.id" class="project-row">
            <div class="project-main">
              <div class="project-name">{{ p.name }}</div>
              <div class="project-meta">
                <span class="chip" :class="p.statusChip">{{ p.statusLabel }}</span>
                <span>{{ p.code }}</span>
                <span>{{ p.manager || '-' }}</span>
              </div>
            </div>
            <div class="project-progress">
              <div class="progress-track">
                <div class="progress-fill" :class="p.fillClass" :style="{ width: p.percent + '%' }"></div>
              </div>
              <span class="progress-pct">{{ p.percent }}%</span>
            </div>
          </li>
        </ul>
        <div v-else class="empty-state">등록된 프로젝트가 없습니다.</div>
      </section>

      <!-- 수행단계 -->
      <section class="card">
        <div class="card-head">
          <span class="card-title">수행단계</span>
          <span v-if="focusProject" class="focus-name">{{ focusProject.name }}</span>
        </div>

        <div v-if="focusPhases.length" class="stage-bar">
          <div v-for="ph in focusPhases" :key="ph.id" class="stage-row">
            <div class="stage-name">{{ ph.name }}</div>
            <span class="chip" :class="phaseChip(ph.status)">{{ phaseLabel(ph.status) }}</span>
          </div>
        </div>
        <div v-else class="empty-state">등록된 수행단계가 없습니다.</div>
      </section>
    </div>
  </div>
</template>

<script>
// TODO(API 연동): 현재는 PMS mock store 를 그대로 읽어 집계한다.
//                 백엔드 연동 시 store 의 조회 로직만 교체하면 이 화면은 수정 없이 동작한다.
import projectStore from '@/store/projectStore'
import phaseStore from '@/store/phaseStore'

const PROJECT_STATUS = {
  READY: { label: '준비중', chip: 'badge-muted' },
  IN_PROGRESS: { label: '진행중', chip: 'chip-common' },
  HOLD: { label: '보류', chip: 'chip-warn' },
  DONE: { label: '완료', chip: 'chip-success' }
}

const PHASE_STATUS = {
  READY: { label: '대기', chip: 'badge-muted' },
  IN_PROGRESS: { label: '진행중', chip: 'chip-common' },
  DONE: { label: '완료', chip: 'chip-success' }
}

export default {
  name: 'DashboardView',
  computed: {
    projects() {
      return projectStore.state.projects
    },
    phasesByProject() {
      return phaseStore.state.phasesByProject
    },
    countByStatus() {
      // 상태별 건수를 한 번의 순회로 계산
      const counts = { READY: 0, IN_PROGRESS: 0, HOLD: 0, DONE: 0 }
      this.projects.forEach((p) => {
        if (p.status in counts) counts[p.status] += 1
      })
      return counts
    },
    kpis() {
      const c = this.countByStatus
      return [
        { label: '전체 프로젝트', value: this.projects.length, sub: '등록된 프로젝트', tone: '' },
        { label: '진행중', value: c.IN_PROGRESS, sub: '수행 중인 프로젝트', tone: 'is-primary' },
        { label: '완료', value: c.DONE, sub: '종료된 프로젝트', tone: 'is-ok' },
        { label: '준비중 · 보류', value: c.READY + c.HOLD, sub: `보류 ${c.HOLD}건`, tone: c.HOLD > 0 ? 'is-warn' : '' }
      ]
    },
    projectRows() {
      return this.projects.map((p) => {
        const phases = this.phasesByProject[p.id] || []
        const done = phases.filter((ph) => ph.status === 'DONE').length
        // 수행단계가 없으면 0%, 있으면 완료 단계 비율
        const percent = phases.length ? Math.round((done / phases.length) * 100) : 0
        const meta = PROJECT_STATUS[p.status] || PROJECT_STATUS.READY
        return {
          ...p,
          percent,
          statusLabel: meta.label,
          statusChip: meta.chip,
          fillClass: percent === 100 ? 'ok' : p.status === 'HOLD' ? 'warn' : ''
        }
      })
    },
    // 수행단계 카드는 진행중 프로젝트를 우선, 없으면 첫 프로젝트를 보여준다.
    focusProject() {
      return this.projects.find((p) => p.status === 'IN_PROGRESS') || this.projects[0] || null
    },
    focusPhases() {
      if (!this.focusProject) return []
      return phaseStore.listByProject(this.focusProject.id)
    }
  },
  methods: {
    phaseLabel(status) {
      return (PHASE_STATUS[status] || PHASE_STATUS.READY).label
    },
    phaseChip(status) {
      return (PHASE_STATUS[status] || PHASE_STATUS.READY).chip
    }
  }
}
</script>

<style scoped>
/* kpi / card-head / two-col / progress-* / empty-state 는 common.css 공용 클래스를 사용한다. */
.focus-name {
  overflow: hidden;
  max-width: 55%;
  white-space: nowrap;
  text-overflow: ellipsis;
  font-size: 12px;
  color: var(--color-text-muted);
}

.project-list {
  margin: 0;
  padding: 0;
  list-style: none;
}

.project-row {
  display: flex;
  align-items: center;
  gap: var(--space-4);
  padding: 12px var(--space-4);
  border-bottom: 1px solid var(--color-border);
}
.project-row:last-child { border-bottom: none; }
.project-row:hover { background: var(--color-surface-muted); }

.project-main { flex: 1; min-width: 0; }

.project-name {
  margin-bottom: 4px;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  font-size: var(--font-size-sm);
  font-weight: 600;
}

.project-meta {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  font-size: 11.5px;
  color: var(--color-text-muted);
}

.project-progress {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  width: 180px;
  flex-shrink: 0;
}

.stage-bar { padding: var(--space-2) var(--space-4); }

.stage-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px solid var(--color-border);
}
.stage-row:last-child { border-bottom: none; }

.stage-name {
  font-size: var(--font-size-sm);
  font-weight: 600;
  color: var(--color-text-soft);
}

@media (max-width: 700px) {
  .project-progress { width: 110px; }
}
</style>

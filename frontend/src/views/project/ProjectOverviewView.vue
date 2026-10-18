<template>
  <div class="pms-page">
    <div class="page-header">
      <h1>전체 대시보드</h1>
      <p>내 프로젝트 {{ projects.length }}개 · 업무, 이슈, 팀 현황</p>
    </div>

    <div class="stack">
      <!-- KPI -->
      <section class="kpi-row">
        <div v-for="kpi in kpis" :key="kpi.label" class="kpi-card">
          <div class="kpi-label">{{ kpi.label }}</div>
          <div class="kpi-val">{{ kpi.value }}</div>
          <div class="kpi-sub" :class="kpi.tone">{{ kpi.sub }}</div>
        </div>
      </section>

      <div class="two-col">
        <!-- 최근 업무 -->
        <section class="card">
          <div class="card-head">
            <span class="card-title">최근 업무</span>
            <router-link to="/tasks" class="btn btn-ghost btn-sm">전체 보기</router-link>
          </div>
          <ul v-if="recentTasks.length" class="task-list">
            <TaskRow v-for="task in recentTasks" :key="task.id" :task="task" @toggle="toggleTask" />
          </ul>
          <div v-else class="empty-state">등록된 업무가 없습니다.</div>
        </section>

        <div class="stack">
          <!-- 단계별 진행률 -->
          <section class="card">
            <div class="card-head"><span class="card-title">단계별 진행률</span></div>
            <div class="progress-box">
              <div v-for="stage in stages" :key="stage.id" class="progress-row">
                <div class="progress-name">{{ stage.name }}</div>
                <div class="progress-track" role="progressbar" :aria-valuenow="stage.percent" aria-valuemin="0" aria-valuemax="100" :aria-label="`${stage.name} 진행률`">
                  <div class="progress-fill" :class="stage.tone" :style="{ width: stage.percent + '%' }"></div>
                </div>
                <div class="progress-pct">{{ stage.percent }}%</div>
              </div>
            </div>
          </section>

          <!-- 팀 멤버 -->
          <section class="card">
            <div class="card-head">
              <span class="card-title">팀 멤버</span>
              <router-link to="/members" class="btn btn-ghost btn-sm">관리</router-link>
            </div>
            <ul class="member-list">
              <MemberRow v-for="member in members" :key="member.id" :member="member" />
            </ul>
          </section>
        </div>
      </div>

      <!-- 리스크 · 이슈 -->
      <section class="card">
        <div class="card-head">
          <span class="card-title">리스크 · 이슈</span>
          <router-link to="/issues/new" class="btn btn-ghost btn-sm">이슈 등록</router-link>
        </div>
        <IssueTable :issues="issues" />
      </section>
    </div>
  </div>
</template>

<script>
import TaskRow from '@/components/work/TaskRow.vue'
import MemberRow from '@/components/work/MemberRow.vue'
import IssueTable from '@/components/work/IssueTable.vue'
import sampleStore from '@/store/sampleStore'

export default {
  name: 'ProjectOverviewView',
  components: { TaskRow, MemberRow, IssueTable },
  computed: {
    projects() {
      return sampleStore.state.myProjects
    },
    tasks() {
      return sampleStore.state.tasks
    },
    members() {
      return sampleStore.state.members
    },
    issues() {
      return sampleStore.state.issues
    },
    stages() {
      return sampleStore.state.stages
    },
    // 목록 맨 앞이 가장 최근 등록이므로 앞에서 6건만 보여준다
    recentTasks() {
      return this.tasks.slice(0, 6)
    },
    kpis() {
      const total = this.tasks.length
      const done = this.tasks.filter((t) => t.status === 'DONE').length
      const delayed = this.tasks.filter((t) => t.status === 'DELAYED').length
      const openIssues = this.issues.filter((i) => i.status !== 'DONE')
      const high = openIssues.filter((i) => i.severity === 'HIGH').length
      // 0 으로 나누는 경우(업무 없음)를 방지
      const rate = total ? Math.round((done / total) * 1000) / 10 : 0
      return [
        { label: '전체 업무', value: total, sub: '등록된 업무', tone: 'is-primary' },
        { label: '완료', value: done, sub: `${rate}% 달성`, tone: 'is-ok' },
        { label: '미완료', value: total - done, sub: `${delayed}건 지연`, tone: delayed ? 'is-warn' : '' },
        { label: '미해결 이슈', value: openIssues.length, sub: `HIGH ${high}건`, tone: high ? 'is-danger' : '' },
      ]
    },
  },
  methods: {
    toggleTask(id) {
      sampleStore.toggleTask(id)
    },
  },
}
</script>

<style scoped>
.progress-box {
  padding: var(--space-4);
}
</style>

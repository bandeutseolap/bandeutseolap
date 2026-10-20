<template>
  <div class="pms-page">
    <div class="page-header">
      <h1>전체 대시보드</h1>
      <p>내 프로젝트 {{ projects.length }}개 · 업무, 이슈, 게시판 현황</p>
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

          <!-- 게시판: /boards 와 같은 목록 API 의 최신 게시글 -->
          <section class="card">
            <div class="card-head">
              <span class="card-title">전체 공지</span>
              <router-link to="/boards" class="btn btn-ghost btn-sm">전체 보기</router-link>
            </div>

            <div v-if="boardLoading" class="empty-state">게시글을 불러오는 중입니다.</div>

            <div v-else-if="boardError" class="empty-state">
              {{ boardError }}
              <div class="retry-row">
                <button type="button" class="btn btn-outline btn-sm" @click="loadBoards">다시 시도</button>
              </div>
            </div>

            <ul v-else-if="boards.length" class="task-list">
              <li v-for="board in boards" :key="board.boardId" class="board-item">
                <svg class="task-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                  <path d="M4 4h16v12H8l-4 4z" />
                </svg>
                <div class="task-body">
                  <div class="task-name">
                    <router-link :to="`/boards/${board.boardId}`" class="board-link">{{ board.title }}</router-link>
                  </div>
                  <div class="task-meta">
                    <span>{{ board.writtenBy }}</span>
                    <span>{{ formatDate(board.writtenAt) }}</span>
                  </div>
                </div>
              </li>
            </ul>

            <div v-else class="empty-state">등록된 게시글이 없습니다.</div>
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
import IssueTable from '@/components/work/IssueTable.vue'
import sampleStore from '@/store/sampleStore'
import { fetchBoardList } from '@/services/boardService'
import dayjs from 'dayjs'

// 대시보드에 보여줄 최신 게시글 수 (목록 API 는 최신순으로 내려준다)
const BOARD_LIMIT = 3

export default {
  name: 'ProjectOverviewView',
  components: { TaskRow, IssueTable },
  data() {
    return {
      boards: [],
      boardLoading: false,
      boardError: '',
    }
  },
  computed: {
    projects() {
      return sampleStore.state.myProjects
    },
    tasks() {
      return sampleStore.state.tasks
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
  created() {
    this.loadBoards()
  },
  methods: {
    toggleTask(id) {
      sampleStore.toggleTask(id)
    },
    async loadBoards() {
      this.boardLoading = true
      this.boardError = ''
      try {
        const response = await fetchBoardList()
        this.boards = (response.content || []).slice(0, BOARD_LIMIT)
      } catch (err) {
        // 게시판 목록 실패가 대시보드 나머지(업무/이슈/진행률)를 막지 않도록 이 카드 안에서만 처리
        console.error('게시글 목록 조회 실패', err)
        this.boardError = '게시글을 불러오지 못했습니다.'
      } finally {
        this.boardLoading = false
      }
    },
    formatDate(value) {
      return value ? dayjs(value).format('YYYY-MM-DD') : '-'
    },
  },
}
</script>

<style scoped>
.progress-box {
  padding: var(--space-4);
}

.board-link {
  color: inherit;
}

.board-link:hover {
  color: var(--color-primary-700);
  text-decoration: underline;
}

.retry-row {
  margin-top: var(--space-3);
}
</style>
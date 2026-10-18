<template>
  <div class="pms-page">
    <div class="page-header">
      <h1>업무 목록</h1>
      <p>{{ currentProject.name }} · 전체 {{ tasks.length }}건</p>
    </div>

    <section class="card">
      <div class="card-head">
        <span class="card-title">업무 목록</span>
        <div class="card-actions">
          <select v-model="statusFilter" class="select-inline" aria-label="상태 필터">
            <option value="ALL">전체 상태</option>
            <option v-for="(meta, code) in statusOptions" :key="code" :value="code">{{ meta.label }}</option>
          </select>
          <router-link to="/tasks/new" class="btn btn-primary btn-sm">업무 등록</router-link>
        </div>
      </div>

      <ul v-if="filteredTasks.length" class="task-list">
        <TaskRow v-for="task in filteredTasks" :key="task.id" :task="task" @toggle="toggleTask" />
      </ul>
      <div v-else class="empty-state">조건에 맞는 업무가 없습니다.</div>
    </section>

    <AppToast :message="toastMessage" />
  </div>
</template>

<script>
import TaskRow from '@/components/work/TaskRow.vue'
import AppToast from '@/components/common/AppToast.vue'
import sampleStore from '@/store/sampleStore'
import sampleViewMixin from '@/mixins/sampleViewMixin'
import { TASK_STATUS } from '@/utils/workMeta'

export default {
  name: 'TaskListView',
  components: { TaskRow, AppToast },
  mixins: [sampleViewMixin],
  data() {
    return {
      statusFilter: 'ALL',
      statusOptions: TASK_STATUS,
    }
  },
  computed: {
    tasks() {
      return sampleStore.state.tasks
    },
    filteredTasks() {
      return this.statusFilter === 'ALL'
        ? this.tasks
        : this.tasks.filter((t) => t.status === this.statusFilter)
    },
  },
  created() {
    this.showFlash()
  },
  methods: {
    toggleTask(id) {
      sampleStore.toggleTask(id)
    },
  },
}
</script>

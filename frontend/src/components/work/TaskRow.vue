<template>
  <li class="task-item" :class="{ done: isDone }">
    <!-- 체크박스: 완료 ↔ 진행중 토글 (부모가 toggle 이벤트로 store 갱신) -->
    <button
      type="button"
      class="task-cb"
      role="checkbox"
      :aria-checked="isDone"
      :aria-label="`${task.title} 완료 처리`"
      @click="$emit('toggle', task.id)"
    >
      <svg v-if="isDone" width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3.5" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
        <path d="M20 6L9 17l-5-5" />
      </svg>
    </button>

    <div class="task-body">
      <div class="task-name">{{ task.title }}</div>
      <div class="task-meta">
        <span class="chip" :class="meta.chip">{{ meta.label }}</span>
        <span>{{ task.assignee }}</span>
        <span v-if="dateText">{{ dateText }}</span>
      </div>
    </div>
  </li>
</template>

<script>
import { TASK_STATUS, formatMonthDay } from '@/utils/workMeta'

export default {
  name: 'TaskRow',
  props: {
    task: { type: Object, required: true },
  },
  emits: ['toggle'],
  computed: {
    isDone() {
      return this.task.status === 'DONE'
    },
    meta() {
      return TASK_STATUS[this.task.status] || TASK_STATUS.WAIT
    },
    // 완료된 업무는 완료일, 그 외에는 '마감 M월 D일' (시안과 동일)
    dateText() {
      if (this.isDone) return formatMonthDay(this.task.completedAt || this.task.dueDate)
      return this.task.dueDate ? `마감 ${formatMonthDay(this.task.dueDate)}` : ''
    },
  },
}
</script>

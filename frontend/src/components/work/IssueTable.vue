<template>
  <div class="table-wrap">
    <table>
      <thead>
        <tr>
          <th>심각도</th>
          <th>제목</th>
          <th>유형</th>
          <th>담당자</th>
          <th>상태</th>
          <th>등록일</th>
        </tr>
      </thead>
      <tbody>
        <tr v-if="issues.length === 0" class="empty-row">
          <td colspan="6">등록된 이슈가 없습니다.</td>
        </tr>
        <tr v-for="issue in issues" :key="issue.id">
          <td>
            <span class="sev-dot" :style="{ background: severity(issue).color }"></span>{{ severity(issue).label }}
          </td>
          <td>{{ issue.title }}</td>
          <td><span class="chip" :class="type(issue).chip">{{ type(issue).label }}</span></td>
          <td>{{ issue.assignee }}</td>
          <td><span class="chip" :class="status(issue).chip">{{ status(issue).label }}</span></td>
          <td>{{ issue.createdAt }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
import { ISSUE_SEVERITY, ISSUE_TYPE, ISSUE_STATUS } from '@/utils/workMeta'

export default {
  name: 'IssueTable',
  props: {
    issues: { type: Array, required: true },
  },
  methods: {
    severity(issue) {
      return ISSUE_SEVERITY[issue.severity] || ISSUE_SEVERITY.LOW
    },
    type(issue) {
      return ISSUE_TYPE[issue.type] || ISSUE_TYPE.ETC
    },
    status(issue) {
      return ISSUE_STATUS[issue.status] || ISSUE_STATUS.WAIT
    },
  },
}
</script>

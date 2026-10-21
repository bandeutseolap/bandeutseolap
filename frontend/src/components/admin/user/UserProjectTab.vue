<template>
  <section class="card">
    <div class="card-head"><span class="card-title">참여 프로젝트 <span class="text-muted">조회 전용</span></span></div>
    <div class="table-wrap">
      <table>
        <thead><tr><th>프로젝트명</th><th>구성원 상태</th><th>참여일시</th><th>프로젝트 역할</th></tr></thead>
        <tbody>
          <tr v-if="rows.length === 0" class="empty-row"><td colspan="4">참여 중인 프로젝트가 없습니다.</td></tr>
          <tr v-for="p in rows" :key="p.projectName">
            <td><strong>{{ p.projectName }}</strong></td>
            <td><span class="chip" :class="p.memberStatus === '활동' ? 'chip-success' : 'badge-muted'">{{ p.memberStatus }}</span></td>
            <td>{{ p.joinedAt }}</td>
            <td>{{ p.projectRole }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>

<script>
import adminStore from '@/store/adminStore'

export default {
  name: 'UserProjectTab',
  props: { user: { type: Object, required: true } },
  computed: { rows() { return adminStore.participationsOf(this.user.userId) } },
}
</script>

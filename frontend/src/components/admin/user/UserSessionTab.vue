<template>
  <section class="card">
    <div class="card-head">
      <span class="card-title">Redis 세션 <span class="text-muted">{{ sessions.length }}건</span></span>
      <button type="button" class="btn btn-outline-error btn-sm" :disabled="!sessions.length" @click="confirmAll = true">전체 강제 로그아웃</button>
    </div>
    <div class="table-wrap">
      <table class="table-nowrap">
        <thead><tr><th>로그인 유형</th><th>접속 IP</th><th>발급일시</th><th>마지막 접근</th><th>만료일시</th><th>세션 상태</th><th class="col-actions">관리</th></tr></thead>
        <tbody>
          <tr v-if="sessions.length === 0" class="empty-row"><td colspan="7">로그인된 세션이 없습니다.</td></tr>
          <tr v-for="s in sessions" :key="s.id">
            <td>{{ s.loginType }}</td><td>{{ s.ip }}</td><td>{{ s.issuedAt }}</td><td>{{ s.lastAccessAt }}</td><td>{{ s.expiresAt }}</td>
            <td><span class="chip" :class="s.status === 'ACTIVE' ? 'chip-success' : 'badge-muted'">{{ s.status === 'ACTIVE' ? '활성' : '만료' }}</span></td>
            <td class="col-actions"><button type="button" class="btn btn-danger-text" @click="onRevoke(s)">강제 로그아웃</button></td>
          </tr>
        </tbody>
      </table>
    </div>
    <YesNoModal v-model="confirmAll" title="전체 강제 로그아웃" message="이 사용자의 모든 세션을 종료할까요?" sub-message="사용자는 모든 기기에서 다시 로그인해야 합니다." @confirm="onRevokeAll" />
  </section>
</template>

<script>
import YesNoModal from '@/components/common/YesNoModal.vue'
import adminStore from '@/store/adminStore'

export default {
  name: 'UserSessionTab',
  components: { YesNoModal },
  props: { user: { type: Object, required: true } },
  emits: ['toast', 'error'],
  data() { return { confirmAll: false } },
  computed: { sessions() { return adminStore.sessionsOf(this.user.userId) } },
  methods: {
    async onRevoke(s) { await adminStore.revokeUserSession(this.user.userId, s.id); this.$emit('toast', '세션을 종료했습니다.') },
    async onRevokeAll() { await adminStore.revokeAllUserSessions(this.user.userId); this.confirmAll = false; this.$emit('toast', '모든 세션을 종료했습니다.') },
  },
}
</script>

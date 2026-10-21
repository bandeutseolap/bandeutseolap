<template>
  <div class="stack">
    <section class="card">
      <div class="card-head"><span class="card-title">역할 부여</span></div>
      <div class="grant-row">
        <div class="form-row">
          <label for="r-role">역할</label>
          <select id="r-role" v-model="grant.roleId" class="form-control">
            <option v-for="r in activeRoles" :key="r.roleId" :value="r.roleId">{{ r.roleName }} ({{ r.roleCd }})</option>
          </select>
        </div>
        <div class="form-row">
          <label for="r-exp">만료일 (선택)</label>
          <input id="r-exp" v-model="grant.expiredAt" type="date" class="form-control" :min="today" />
        </div>
        <button type="button" class="btn btn-primary" @click="onGrant">부여</button>
      </div>
    </section>

    <section class="card">
      <div class="card-head"><span class="card-title">부여된 역할</span></div>
      <div class="table-wrap">
        <table class="table-nowrap">
          <thead><tr><th>역할명</th><th>역할코드</th><th>부여자</th><th>부여일시</th><th>만료일시</th><th>사용 여부</th><th>실효 여부</th><th class="col-actions">관리</th></tr></thead>
          <tbody>
            <tr v-if="maps.length === 0" class="empty-row"><td colspan="8">부여된 역할이 없습니다.</td></tr>
            <tr v-for="m in maps" :key="m.mapId">
              <td>{{ m.role.roleName }}</td>
              <td class="mono">{{ m.role.roleCd }}</td>
              <td>{{ m.grantedByName }}</td>
              <td>{{ m.grantedAt }}</td>
              <td>
                <template v-if="editing === m.mapId">
                  <input v-model="editDate" type="date" class="form-control date-edit" :min="today" aria-label="만료일" />
                </template>
                <template v-else>{{ m.expiredAt || '무기한' }}</template>
              </td>
              <td><span class="chip" :class="m.useYn ? 'chip-success' : 'badge-muted'">{{ m.useYn ? '사용' : '회수됨' }}</span></td>
              <td><span class="chip" :class="m.effective ? 'chip-success' : 'chip-warn'">{{ m.effective ? '실효' : (m.useYn ? '만료' : '-') }}</span></td>
              <td class="col-actions">
                <template v-if="editing === m.mapId">
                  <button type="button" class="btn btn-text" @click="saveExtend(m)">저장</button>
                  <button type="button" class="btn btn-text" @click="editing = null">취소</button>
                </template>
                <template v-else-if="m.useYn">
                  <button type="button" class="btn btn-text" @click="startExtend(m)">만료일 연장</button>
                  <button type="button" class="btn btn-danger-text" @click="onRevoke(m)">회수</button>
                </template>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </div>
</template>

<script>
import dayjs from 'dayjs'
import adminStore from '@/store/adminStore'

export default {
  name: 'UserRoleTab',
  props: { user: { type: Object, required: true } },
  emits: ['toast', 'error'],
  data() {
    return { grant: { roleId: null, expiredAt: '' }, editing: null, editDate: '', today: dayjs().format('YYYY-MM-DD') }
  },
  computed: {
    activeRoles() { return adminStore.state.roles.filter((r) => r.useYn) },
    maps() { return adminStore.roleMapsOf(this.user.userId) },
  },
  created() { this.grant.roleId = this.activeRoles[0] ? this.activeRoles[0].roleId : null },
  methods: {
    async onGrant() {
      try {
        await adminStore.grantRole(this.user.userId, this.grant.roleId, this.grant.expiredAt ? `${this.grant.expiredAt} 23:59` : '')
        this.grant.expiredAt = ''
        this.$emit('toast', '역할을 부여했습니다.')
      } catch (err) { this.$emit('error', err) }
    },
    async onRevoke(m) {
      try {
        await adminStore.revokeRole(this.user.userId, m.roleId)
        this.$emit('toast', '역할을 회수했습니다.')
      } catch (err) { this.$emit('error', err) } // 마지막 ADMIN 이면 LAST_ADMIN_CANNOT_BE_REVOKED
    },
    startExtend(m) { this.editing = m.mapId; this.editDate = m.expiredAt ? m.expiredAt.slice(0, 10) : '' },
    async saveExtend(m) {
      try {
        await adminStore.extendRole(this.user.userId, m.roleId, this.editDate ? `${this.editDate} 23:59` : '')
        this.editing = null
        this.$emit('toast', '만료일을 변경했습니다.')
      } catch (err) { this.$emit('error', err) }
    },
  },
}
</script>

<style scoped>
.grant-row { display: grid; grid-template-columns: minmax(0, 1fr) 200px auto; gap: var(--space-3); align-items: end; padding: var(--space-4); }
.grant-row .form-row { margin-bottom: 0; }
.date-edit { height: 30px; min-width: 150px; }
@media (max-width: 700px) { .grant-row { grid-template-columns: minmax(0, 1fr); } }
</style>

<template>
  <div class="pms-page">
    <div class="page-header">
      <h1>역할별 사용자</h1>
      <p>관리자 › 사용자/권한 관리 · 역할을 일괄로 부여하거나 회수합니다.</p>
    </div>

    <div class="role-users">
      <!-- 좌측: 역할 목록 -->
      <section class="card">
        <div class="card-head"><span class="card-title">역할</span></div>
        <ul class="role-list">
          <li v-for="r in roles" :key="r.roleId">
            <button type="button" class="role-btn" :class="{ active: r.roleId === roleId }" @click="selectRole(r.roleId)">
              <span>{{ r.roleName }} <span class="text-muted mono">{{ r.roleCd }}</span></span>
              <span class="chip badge-muted">{{ count(r) }}명</span>
            </button>
          </li>
        </ul>
      </section>

      <!-- 우측: 선택한 역할의 사용자 -->
      <section class="card">
        <div class="card-head">
          <span class="card-title">{{ role ? role.roleName : '' }} 보유 사용자 <span class="text-muted">{{ rows.length }}명</span></span>
          <div class="card-actions">
            <button type="button" class="btn btn-outline-error btn-sm" :disabled="!selected.length" @click="revokeOpen = true">선택 회수 ({{ selected.length }})</button>
            <button type="button" class="btn btn-primary btn-sm" @click="openGrant">사용자 검색 후 부여</button>
          </div>
        </div>

        <div class="table-wrap">
          <table class="table-nowrap">
            <thead><tr><th class="col-check"><input type="checkbox" :checked="allChecked" aria-label="전체 선택" @change="toggleAll($event.target.checked)" /></th><th>아이디</th><th>이름</th><th>부여자</th><th>부여일시</th><th>만료일시</th><th>실효 여부</th></tr></thead>
            <tbody>
              <tr v-if="rows.length === 0" class="empty-row"><td colspan="7">이 역할을 가진 사용자가 없습니다.</td></tr>
              <tr v-for="m in rows" :key="m.mapId">
                <td class="col-check"><input v-model="selected" type="checkbox" :value="m.userId" :aria-label="`${m.user.loginId} 선택`" /></td>
                <td><router-link :to="{ name: 'AdminUserDetail', params: { userId: m.userId } }" class="link-btn">{{ m.user.loginId }}</router-link></td>
                <td>{{ m.user.userName }}</td><td>{{ m.grantedByName }}</td><td>{{ m.grantedAt }}</td><td>{{ m.expiredAt || '무기한' }}</td>
                <td><span class="chip" :class="m.effective ? 'chip-success' : 'chip-warn'">{{ m.effective ? '실효' : '만료' }}</span></td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>
    </div>

    <!-- 부여: 사용자 검색 + 공통 만료일 -->
    <div v-if="grantOpen" class="modal-overlay" @click.self="grantOpen = false">
      <div class="modal wide-modal" role="dialog" aria-modal="true" aria-label="사용자 검색 후 부여">
        <div class="modal-header"><h3 class="modal-title">{{ role.roleName }} 일괄 부여</h3></div>
        <div class="modal-body">
          <div class="form-row">
            <label for="rg-search">사용자 검색</label>
            <input id="rg-search" v-model="keyword" class="form-control" placeholder="아이디 또는 이름" />
          </div>
          <ul class="candidate-list">
            <li v-for="u in candidates" :key="u.userId">
              <label><input v-model="picked" type="checkbox" :value="u.userId" /> <strong>{{ u.loginId }}</strong> <span class="text-muted">{{ u.userName }}</span></label>
            </li>
            <li v-if="!candidates.length" class="text-muted">부여할 수 있는 사용자가 없습니다.</li>
          </ul>
          <div class="form-row">
            <label for="rg-exp">만료일 (공통, 선택)</label>
            <input id="rg-exp" v-model="expiredAt" type="date" class="form-control" :min="today" />
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" @click="grantOpen = false">취소</button>
          <button type="button" class="btn btn-primary" :disabled="!picked.length" @click="onGrant">{{ picked.length }}명 부여</button>
        </div>
      </div>
    </div>

    <YesNoModal
      v-model="revokeOpen" title="역할 일괄 회수" :message="`선택한 ${selected.length}명의 '${role ? role.roleName : ''}' 역할을 회수할까요?`"
      sub-message="회수한 내역은 변경이력에 남습니다." @confirm="onRevoke"
    />
    <AppToast :message="toastMessage" />
  </div>
</template>

<script>
import dayjs from 'dayjs'
import AppToast from '@/components/common/AppToast.vue'
import YesNoModal from '@/components/common/YesNoModal.vue'
import adminStore from '@/store/adminStore'
import adminViewMixin from '@/mixins/adminViewMixin'

export default {
  name: 'RoleUsersView',
  components: { AppToast, YesNoModal },
  mixins: [adminViewMixin],
  data() {
    const first = adminStore.state.roles[0]
    return { roleId: first ? first.roleId : null, selected: [], grantOpen: false, keyword: '', picked: [], expiredAt: '', revokeOpen: false, today: dayjs().format('YYYY-MM-DD') }
  },
  computed: {
    roles() { return adminStore.state.roles },
    role() { return this.roles.find((r) => r.roleId === this.roleId) },
    rows() { return adminStore.usersOfRole(this.roleId) },
    allChecked() { return this.rows.length > 0 && this.rows.every((m) => this.selected.includes(m.userId)) },
    // 이미 실효 역할을 가진 사용자와 탈퇴 계정은 후보에서 뺀다
    candidates() {
      const holding = new Set(this.rows.filter((m) => m.effective).map((m) => m.userId))
      const kw = this.keyword.trim()
      return adminStore.state.users.filter((u) => u.accStatusCd !== 'WITHDRAWN' && !holding.has(u.userId) && (!kw || u.loginId.includes(kw) || u.userName.includes(kw)))
    },
  },
  methods: {
    count(role) { return adminStore.roleHolderCount(role.roleId) },
    selectRole(id) { this.roleId = id; this.selected = [] },
    toggleAll(checked) { this.selected = checked ? this.rows.map((m) => m.userId) : [] },
    openGrant() { this.keyword = ''; this.picked = []; this.expiredAt = ''; this.grantOpen = true },
    async onGrant() {
      try {
        await adminStore.bulkGrant(this.picked, this.roleId, this.expiredAt ? `${this.expiredAt} 23:59` : '')
        this.grantOpen = false
        this.showToast(`${this.picked.length}명에게 역할을 부여했습니다.`)
      } catch (err) { this.fail(err) }
    },
    async onRevoke() {
      try {
        await adminStore.revokeRoles(this.selected, this.roleId) // 마지막 ADMIN 보호 검증 포함
        this.showToast(`${this.selected.length}명의 역할을 회수했습니다.`)
        this.selected = []
      } catch (err) { this.fail(err) } finally { this.revokeOpen = false }
    },
  },
}
</script>

<style scoped>
.role-users { display: grid; grid-template-columns: 280px minmax(0, 1fr); gap: var(--space-4); align-items: start; }
.role-list { margin: 0; padding: var(--space-2); list-style: none; }
.role-btn {
  display: flex; align-items: center; justify-content: space-between; gap: var(--space-2); width: 100%; padding: 10px var(--space-3);
  border: none; border-radius: var(--radius-md); background: none; font-size: var(--font-size-sm); text-align: left; cursor: pointer;
}
.role-btn:hover { background: var(--color-surface-muted); }
.role-btn.active { background: var(--color-primary-50); font-weight: 700; color: var(--color-primary-700); }
.candidate-list { max-height: 220px; margin: 0 0 var(--space-4); padding: var(--space-2); overflow-y: auto; list-style: none; border: 1px solid var(--color-border); border-radius: var(--radius-md); }
.candidate-list li { padding: 5px var(--space-2); font-size: var(--font-size-sm); }
.candidate-list label { display: flex; align-items: center; gap: var(--space-2); cursor: pointer; }
.wide-modal { width: 520px; }
@media (max-width: 900px) { .role-users { grid-template-columns: minmax(0, 1fr); } }
</style>

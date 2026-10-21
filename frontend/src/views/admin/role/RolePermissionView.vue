<template>
  <div class="pms-page">
    <div class="page-header">
      <h1>역할-권한 매핑</h1>
      <p>관리자 › 사용자/권한 관리 · 역할이 할 수 있는 일을 정의합니다 (R: 조회, W: 변경)</p>
    </div>

    <section class="card">
      <div class="card-head">
        <span class="card-title">권한 매트릭스</span>
        <div class="card-actions">
          <button type="button" class="btn btn-outline btn-sm" :disabled="!dirty" @click="resetDraft">되돌리기</button>
          <button type="button" class="btn btn-primary btn-sm" :disabled="!dirty" @click="onSave">저장</button>
        </div>
      </div>

      <div class="table-wrap">
        <table class="matrix">
          <thead>
            <tr>
              <th rowspan="2" class="role-col">역할</th>
              <th v-for="res in resources" :key="res.cd" :colspan="res.actions.length" class="group-head">{{ res.name }}</th>
            </tr>
            <tr>
              <template v-for="res in resources" :key="res.cd">
                <th v-for="(a, i) in res.actions" :key="res.cd + a" class="action-head" :class="{ 'group-start': i === 0 }">{{ a === 'READ' ? 'R' : 'W' }}</th>
              </template>
            </tr>
          </thead>
          <tbody>
            <tr v-for="role in activeRoles" :key="role.roleId">
              <td class="role-col"><strong>{{ role.roleCd }}</strong><span class="cell-sub">{{ role.roleName }}</span></td>
              <template v-for="res in resources" :key="res.cd">
                <td v-for="(a, i) in res.actions" :key="res.cd + a" class="check-cell" :class="{ 'group-start': i === 0 }">
                  <input
                    type="checkbox" :checked="has(role.roleId, res.cd, a)"
                    :aria-label="`${role.roleCd} ${res.name} ${a === 'READ' ? '조회' : '변경'}`" @change="toggle(role.roleId, res.cd, a, $event.target.checked)"
                  />
                </td>
              </template>
            </tr>
          </tbody>
        </table>
      </div>
    </section>

    <p class="notice-box mt-4">
      권한 판정은 사용자의 <strong>실효 역할</strong>(use_yn = 1 이고 만료되지 않은 역할)에 매핑된 권한을 합쳐서 계산합니다.
      <span v-if="dirty"> 변경 사항이 아직 저장되지 않았습니다.</span>
    </p>

    <section class="card mt-4">
      <div class="card-head"><span class="card-title">권한 목록 (app_auth_permission)</span></div>
      <div class="table-wrap">
        <table>
          <thead><tr><th>권한코드</th><th>권한명</th><th>리소스</th><th>동작</th><th>사용 여부</th></tr></thead>
          <tbody>
            <tr v-for="p in permissions" :key="p.permissionId">
              <td class="mono">{{ p.permissionCd }}</td><td>{{ p.permissionName }}</td><td>{{ p.resourceCd }}</td><td>{{ p.actionCd }}</td>
              <td><span class="chip" :class="p.useYn ? 'chip-success' : 'badge-muted'">{{ p.useYn ? '사용' : '중지' }}</span></td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
    <AppToast :message="toastMessage" />
  </div>
</template>

<script>
import AppToast from '@/components/common/AppToast.vue'
import adminStore from '@/store/adminStore'
import adminViewMixin from '@/mixins/adminViewMixin'

export default {
  name: 'RolePermissionView',
  components: { AppToast },
  mixins: [adminViewMixin],
  data() { return { draft: [...adminStore.state.rolePermissions] } },
  computed: {
    resources() { return adminStore.state.resources },
    permissions() { return adminStore.state.permissions },
    activeRoles() { return adminStore.state.roles.filter((r) => r.useYn) },
    dirty() {
      const saved = adminStore.state.rolePermissions
      return this.draft.length !== saved.length || this.draft.some((k) => !saved.includes(k))
    },
  },
  methods: {
    permissionId(resCd, action) {
      return this.permissions.find((p) => p.resourceCd === resCd && p.actionCd === action).permissionId
    },
    has(roleId, resCd, action) { return this.draft.includes(`${roleId}:${this.permissionId(resCd, action)}`) },
    toggle(roleId, resCd, action, checked) {
      const key = `${roleId}:${this.permissionId(resCd, action)}`
      this.draft = checked ? [...new Set([...this.draft, key])] : this.draft.filter((k) => k !== key)
    },
    resetDraft() { this.draft = [...adminStore.state.rolePermissions] },
    async onSave() {
      // 변경된 역할만 저장한다
      const roleIds = [...new Set([...this.draft, ...adminStore.state.rolePermissions].map((k) => Number(k.split(':')[0])))]
      for (const roleId of roleIds) {
        const next = this.draft.filter((k) => k.startsWith(`${roleId}:`)).map((k) => Number(k.split(':')[1])).sort()
        const prev = adminStore.state.rolePermissions.filter((k) => k.startsWith(`${roleId}:`)).map((k) => Number(k.split(':')[1])).sort()
        if (next.join() !== prev.join()) await adminStore.setRolePermissions(roleId, next)
      }
      this.showToast('권한 매핑을 저장했습니다.')
    },
  },
}
</script>

<style scoped>
.matrix th, .matrix td { text-align: center; }
.matrix .role-col { min-width: 150px; text-align: left; }
.group-head { border-left: 1px solid var(--color-border); }
.action-head { width: 54px; }
.group-start { border-left: 1px solid var(--color-border); }
.check-cell input { width: 16px; height: 16px; accent-color: var(--color-primary-600); cursor: pointer; }
</style>

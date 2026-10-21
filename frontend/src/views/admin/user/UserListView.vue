<template>
  <div class="pms-page">
    <div class="page-header">
      <h1>사용자 목록</h1>
      <p>관리자 › 사용자/권한 관리 · 전체 {{ rows.length }}명</p>
    </div>

    <section class="card">
      <!-- 검색 -->
      <form class="search-panel" @submit.prevent="search">
        <div class="search-grid">
          <div class="form-row"><label for="f-login">아이디</label><input id="f-login" v-model="form.loginId" class="form-control" /></div>
          <div class="form-row"><label for="f-name">이름</label><input id="f-name" v-model="form.userName" class="form-control" /></div>
          <div class="form-row"><label for="f-email">이메일</label><input id="f-email" v-model="form.email" class="form-control" /></div>
          <div class="form-row">
            <label for="f-status">계정 상태</label>
            <select id="f-status" v-model="form.accStatusCd" class="form-control">
              <option value="">전체</option>
              <option v-for="(m, code) in accStatus" :key="code" :value="code">{{ m.label }}</option>
            </select>
          </div>
          <div class="form-row">
            <label for="f-type">계정 유형</label>
            <select id="f-type" v-model="form.accTypeCd" class="form-control">
              <option value="">전체</option>
              <option v-for="(m, code) in accType" :key="code" :value="code">{{ m.label }}</option>
            </select>
          </div>
          <div class="form-row">
            <label for="f-lock">잠금 여부</label>
            <select id="f-lock" v-model="form.lockYn" class="form-control">
              <option value="">전체</option><option value="1">잠금</option><option value="0">잠금 아님</option>
            </select>
          </div>
          <div class="form-row">
            <label for="f-role">시스템 역할</label>
            <select id="f-role" v-model="form.roleId" class="form-control">
              <option value="">전체</option>
              <option v-for="r in roles" :key="r.roleId" :value="r.roleId">{{ r.roleName }} ({{ r.roleCd }})</option>
            </select>
          </div>
          <div class="form-row span-2">
            <label>마지막 로그인 기간</label>
            <div class="range-inputs">
              <input v-model="form.lastLoginFrom" type="date" class="form-control" aria-label="마지막 로그인 시작일" />
              <span>~</span>
              <input v-model="form.lastLoginTo" type="date" class="form-control" aria-label="마지막 로그인 종료일" />
            </div>
          </div>
          <div class="form-row span-2">
            <label>가입일 기간</label>
            <div class="range-inputs">
              <input v-model="form.createdFrom" type="date" class="form-control" aria-label="가입일 시작일" />
              <span>~</span>
              <input v-model="form.createdTo" type="date" class="form-control" aria-label="가입일 종료일" />
            </div>
          </div>
        </div>
        <div class="search-actions">
          <button type="button" class="btn btn-outline" @click="reset">초기화</button>
          <button type="submit" class="btn btn-primary">검색</button>
        </div>
      </form>

      <!-- 일괄 동작 -->
      <div class="bulk-bar">
        <span class="bulk-count">{{ selected.length }}명 선택</span>
        <button type="button" class="btn btn-outline btn-sm" :disabled="!selected.length" @click="onUnlock">잠금 해제</button>
        <button type="button" class="btn btn-outline btn-sm" :disabled="!selected.length" @click="suspendOpen = true">계정 정지</button>
        <button type="button" class="btn btn-outline btn-sm" :disabled="!selected.length" @click="openGrant">역할 일괄 부여</button>
        <span class="grow"></span>
        <button type="button" class="btn btn-outline btn-sm" :disabled="!rows.length" @click="exportOpen = true">엑셀 다운로드</button>
        <router-link :to="{ name: 'AdminUserCreate' }" class="btn btn-primary btn-sm">사용자 등록</router-link>
      </div>

      <!-- 그리드 -->
      <div class="table-wrap">
        <table class="table-nowrap">
          <thead>
            <tr>
              <th class="col-check">
                <input type="checkbox" :checked="allOnPageChecked" aria-label="현재 페이지 전체 선택" @change="togglePage($event.target.checked)" />
              </th>
              <th>아이디</th><th>이름</th><th>이메일</th><th>휴대폰</th><th>계정 유형</th><th>실효 상태</th>
              <th>시스템 역할</th><th>MFA</th><th>로그인 실패</th><th>마지막 로그인</th><th>가입일</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="pageRows.length === 0" class="empty-row"><td colspan="12">조건에 맞는 사용자가 없습니다.</td></tr>
            <tr v-for="u in pageRows" :key="u.userId">
              <td class="col-check"><input v-model="selected" type="checkbox" :value="u.userId" :aria-label="`${u.loginId} 선택`" /></td>
              <td>
                <router-link :to="{ name: 'AdminUserDetail', params: { userId: u.userId } }" class="link-btn">{{ u.loginId }}</router-link>
              </td>
              <td>{{ u.userName }}</td>
              <td>{{ maskEmail(u.email) }}</td>
              <td>{{ maskPhone(u.phone) }}</td>
              <td>{{ accType[u.accTypeCd].label }}</td>
              <td><span class="chip" :class="effective[u.effective].chip">{{ effective[u.effective].label }}</span></td>
              <td>
                <span class="chip-row">
                  <span v-for="r in u.roles" :key="r.roleId" class="chip chip-common">{{ r.roleCd }}</span>
                  <span v-if="!u.roles.length" class="text-muted">-</span>
                </span>
              </td>
              <td>{{ u.security.mfaYn ? '사용' : '미사용' }}</td>
              <td>{{ u.security.failCount }}</td>
              <td>{{ u.lastLoginAt || '-' }}</td>
              <td>{{ u.createdAt.slice(0, 10) }}</td>
            </tr>
          </tbody>
        </table>
      </div>
      <PaginationBar v-model:page="page" :total="rows.length" :size="size" />
    </section>

    <!-- 역할 일괄 부여 -->
    <div v-if="grantOpen" class="modal-overlay" @click.self="grantOpen = false">
      <div class="modal" role="dialog" aria-modal="true" aria-label="역할 일괄 부여">
        <div class="modal-header"><h3 class="modal-title">역할 일괄 부여</h3></div>
        <div class="modal-body">
          <p class="modal-message">선택한 {{ selected.length }}명에게 역할을 부여합니다.</p>
          <div class="form-row">
            <label for="g-role">역할</label>
            <select id="g-role" v-model="grant.roleId" class="form-control">
              <option v-for="r in activeRoles" :key="r.roleId" :value="r.roleId">{{ r.roleName }} ({{ r.roleCd }})</option>
            </select>
          </div>
          <div class="form-row">
            <label for="g-exp">만료일 (선택)</label>
            <input id="g-exp" v-model="grant.expiredAt" type="date" class="form-control" :min="today" />
            <p class="field-hint">비워두면 만료 없이 부여됩니다.</p>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" @click="grantOpen = false">취소</button>
          <button type="button" class="btn btn-primary" @click="onGrant">부여</button>
        </div>
      </div>
    </div>

    <ReasonModal
      v-model="suspendOpen" title="계정 정지" :message="`선택한 ${selected.length}명의 계정을 정지합니다.`"
      sub-message="정지된 계정은 로그인할 수 없습니다." confirm-text="정지" danger @confirm="onSuspend"
    />
    <ReasonModal
      v-model="exportOpen" title="엑셀 다운로드" :message="`검색 결과 ${rows.length}건을 내려받습니다.`"
      sub-message="이메일·휴대폰은 마스킹된 값으로 저장되며, 다운로드 사실은 이력으로 남습니다." confirm-text="다운로드" @confirm="onExport"
    />
    <AppToast :message="toastMessage" />
  </div>
</template>

<script>
import dayjs from 'dayjs'
import AppToast from '@/components/common/AppToast.vue'
import PaginationBar from '@/components/common/PaginationBar.vue'
import ReasonModal from '@/components/common/ReasonModal.vue'
import adminStore from '@/store/adminStore'
import adminViewMixin from '@/mixins/adminViewMixin'
import { ACC_STATUS, ACC_TYPE, EFFECTIVE_STATUS, maskEmail, maskPhone } from '@/utils/adminMeta'

const blank = () => ({
  loginId: '', userName: '', email: '', accStatusCd: '', accTypeCd: '', lockYn: '', roleId: '',
  lastLoginFrom: '', lastLoginTo: '', createdFrom: '', createdTo: '',
})

export default {
  name: 'UserListView',
  components: { AppToast, PaginationBar, ReasonModal },
  mixins: [adminViewMixin],
  data() {
    return {
      accStatus: ACC_STATUS, accType: ACC_TYPE, effective: EFFECTIVE_STATUS,
      form: blank(), applied: blank(),
      page: 1, size: 10,
      selected: [],
      grantOpen: false, grant: { roleId: null, expiredAt: '' },
      suspendOpen: false, exportOpen: false,
      today: dayjs().format('YYYY-MM-DD'),
    }
  },
  computed: {
    roles() { return adminStore.state.roles },
    activeRoles() { return this.roles.filter((r) => r.useYn) },
    rows() { return adminStore.searchUsers(this.applied) },
    pageRows() { return this.rows.slice((this.page - 1) * this.size, this.page * this.size) },
    allOnPageChecked() {
      return this.pageRows.length > 0 && this.pageRows.every((u) => this.selected.includes(u.userId))
    },
  },
  methods: {
    maskEmail, maskPhone,
    search() { this.applied = { ...this.form }; this.page = 1; this.selected = [] },
    reset() { this.form = blank(); this.search() },
    togglePage(checked) {
      const ids = this.pageRows.map((u) => u.userId)
      this.selected = checked ? [...new Set([...this.selected, ...ids])] : this.selected.filter((id) => !ids.includes(id))
    },
    async onUnlock() {
      await adminStore.unlockUsers(this.selected)
      this.showToast('잠금을 해제했습니다. (로그인 실패 횟수 초기화)')
    },
    async onSuspend(reason) {
      await adminStore.suspendUsers(this.selected, reason)
      this.suspendOpen = false
      this.selected = []
      this.showToast('계정을 정지했습니다.')
    },
    openGrant() {
      this.grant = { roleId: this.activeRoles[0] ? this.activeRoles[0].roleId : null, expiredAt: '' }
      this.grantOpen = true
    },
    async onGrant() {
      if (!this.grant.roleId) return
      try {
        const n = await adminStore.bulkGrant(this.selected, this.grant.roleId, this.grant.expiredAt ? `${this.grant.expiredAt} 23:59` : '')
        this.grantOpen = false
        this.showToast(`${n}명에게 역할을 부여했습니다.`)
      } catch (err) { this.fail(err) }
    },
    async onExport(reason) {
      // TODO(API 연동): 서버에서 파일을 만들어 내려받는 구조로 교체 (여기서는 화면에 보이는 마스킹 값으로 CSV 를 만든다)
      const head = ['아이디', '이름', '이메일', '휴대폰', '계정 유형', '실효 상태', '시스템 역할', 'MFA', '로그인 실패', '마지막 로그인', '가입일']
      const lines = this.rows.map((u) => [
        u.loginId, u.userName, maskEmail(u.email), maskPhone(u.phone), ACC_TYPE[u.accTypeCd].label, EFFECTIVE_STATUS[u.effective].label,
        u.roles.map((r) => r.roleCd).join('/'), u.security.mfaYn ? '사용' : '미사용', u.security.failCount, u.lastLoginAt || '-', u.createdAt.slice(0, 10),
      ])
      const csv = [head, ...lines].map((row) => row.map((v) => `"${String(v).replace(/"/g, '""')}"`).join(',')).join('\r\n')
      const url = URL.createObjectURL(new Blob(['\ufeff' + csv], { type: 'text/csv;charset=utf-8' }))
      const a = document.createElement('a')
      a.href = url
      a.download = `users_${dayjs().format('YYYYMMDD_HHmm')}.csv`
      a.click()
      URL.revokeObjectURL(url)
      await adminStore.logExport(this.rows.length, reason) // 다운로드 자체를 이력(cfg_system_hist)으로 남긴다
      this.exportOpen = false
      this.showToast('다운로드를 시작했습니다. 이력에 기록되었습니다.')
    },
  },
}
</script>

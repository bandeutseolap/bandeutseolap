<template>
  <div class="pms-page">
    <div class="page-header">
      <h1>역할 관리</h1>
      <p>관리자 › 사용자/권한 관리 · 시스템 역할(app_auth_role)</p>
    </div>

    <section class="card">
      <div class="card-head">
        <span class="card-title">역할 목록 <span class="text-muted">{{ roles.length }}건</span></span>
        <button type="button" class="btn btn-primary btn-sm" @click="openCreate">역할 등록</button>
      </div>

      <div class="table-wrap">
        <table class="table-nowrap">
          <thead>
            <tr><th>역할코드</th><th>역할명</th><th>설명</th><th>사용 여부</th><th>보유 사용자</th><th>생성자</th><th>생성일시</th><th>수정자</th><th>수정일시</th><th class="col-actions">관리</th></tr>
          </thead>
          <tbody>
            <tr v-for="r in roles" :key="r.roleId">
              <td class="mono"><strong>{{ r.roleCd }}</strong> <span v-if="r.system" class="chip chip-common">시스템</span></td>
              <td>{{ r.roleName }}</td>
              <td class="text-soft">{{ r.roleDesc || '-' }}</td>
              <td><span class="chip" :class="r.useYn ? 'chip-success' : 'badge-muted'">{{ r.useYn ? '사용' : '중지' }}</span></td>
              <td>{{ holderCount(r) }}명</td>
              <td>{{ r.createdBy }}</td><td>{{ r.createdAt }}</td><td>{{ r.updatedBy }}</td><td>{{ r.updatedAt }}</td>
              <td class="col-actions">
                <button type="button" class="btn btn-text" @click="openEdit(r)">수정</button>
                <button
                  type="button" class="btn btn-text" :disabled="r.system && r.useYn"
                  :title="r.system ? '시스템 기본 역할은 사용 중지할 수 없습니다.' : ''" @click="onToggle(r)"
                >{{ r.useYn ? '사용 중지' : '사용' }}</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>

    <!-- 등록/수정 -->
    <div v-if="formOpen" class="modal-overlay" @click.self="formOpen = false">
      <div class="modal" role="dialog" aria-modal="true" :aria-label="editing ? '역할 수정' : '역할 등록'">
        <div class="modal-header"><h3 class="modal-title">{{ editing ? '역할 수정' : '역할 등록' }}</h3></div>
        <div class="modal-body">
          <div class="form-row">
            <label for="rl-cd">역할코드<span class="req">*</span></label>
            <input id="rl-cd" v-model="form.roleCd" class="form-control" :disabled="!!editing" maxlength="30" placeholder="영문 대문자 · 예) AUDITOR" @input="form.roleCd = form.roleCd.toUpperCase()" />
            <p v-if="errors.roleCd" class="field-error">{{ errors.roleCd }}</p>
            <p v-else-if="editing" class="field-hint">역할코드는 수정할 수 없습니다.</p>
          </div>
          <div class="form-row">
            <label for="rl-name">역할명<span class="req">*</span></label>
            <input id="rl-name" v-model="form.roleName" class="form-control" maxlength="50" />
            <p v-if="errors.roleName" class="field-error">{{ errors.roleName }}</p>
          </div>
          <div class="form-row">
            <label for="rl-desc">설명</label>
            <textarea id="rl-desc" v-model="form.roleDesc" class="form-control" rows="3" maxlength="500"></textarea>
            <p class="field-hint">{{ form.roleDesc.length }} / 500</p>
          </div>
          <div class="form-row">
            <label>사용 여부</label>
            <div class="radio-group">
              <label><input v-model="form.useYn" type="radio" :value="true" /> 사용</label>
              <label><input v-model="form.useYn" type="radio" :value="false" :disabled="editing && editing.system" /> 중지</label>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" @click="formOpen = false">취소</button>
          <button type="button" class="btn btn-primary" @click="onSubmit">{{ editing ? '저장' : '등록' }}</button>
        </div>
      </div>
    </div>

    <!-- 보유 사용자가 있는 역할을 중지할 때 경고 -->
    <YesNoModal
      v-model="warnOpen" title="역할 사용 중지"
      :message="warnTarget ? `'${warnTarget.roleName}' 역할을 사용 중지할까요?` : ''"
      :sub-message="warnTarget ? `현재 ${holderCount(warnTarget)}명이 이 역할을 보유하고 있습니다. 중지하면 해당 사용자의 권한에 영향이 있을 수 있습니다.` : ''"
      @confirm="applyToggle"
    />
    <AppToast :message="toastMessage" />
  </div>
</template>

<script>
import AppToast from '@/components/common/AppToast.vue'
import YesNoModal from '@/components/common/YesNoModal.vue'
import adminStore from '@/store/adminStore'
import adminViewMixin from '@/mixins/adminViewMixin'

export default {
  name: 'RoleListView',
  components: { AppToast, YesNoModal },
  mixins: [adminViewMixin],
  data() {
    return { formOpen: false, editing: null, form: { roleCd: '', roleName: '', roleDesc: '', useYn: true }, errors: {}, warnOpen: false, warnTarget: null }
  },
  computed: { roles() { return adminStore.state.roles } },
  methods: {
    holderCount(role) { return adminStore.roleHolderCount(role.roleId) },
    openCreate() { this.editing = null; this.form = { roleCd: '', roleName: '', roleDesc: '', useYn: true }; this.errors = {}; this.formOpen = true },
    openEdit(r) { this.editing = r; this.form = { roleCd: r.roleCd, roleName: r.roleName, roleDesc: r.roleDesc, useYn: r.useYn }; this.errors = {}; this.formOpen = true },
    async onSubmit() {
      const e = {}
      if (!this.editing && !this.form.roleCd.trim()) e.roleCd = '역할코드를 입력해주세요.'
      if (!this.form.roleName.trim()) e.roleName = '역할명을 입력해주세요.'
      this.errors = e
      if (Object.keys(e).length) return
      try {
        if (this.editing) await adminStore.updateRole(this.editing.roleId, { ...this.form, roleName: this.form.roleName.trim() })
        else await adminStore.createRole({ ...this.form, roleCd: this.form.roleCd.trim(), roleName: this.form.roleName.trim() })
        this.formOpen = false
        this.showToast(this.editing ? '역할을 수정했습니다.' : '역할을 등록했습니다.')
      } catch (err) {
        if (err.code === 'ROLE_CD_DUPLICATE' || err.code === 'ROLE_CD_INVALID') this.errors = { roleCd: adminStore.ERROR_MESSAGES[err.code] }
        else this.fail(err)
      }
    },
    onToggle(role) {
      if (role.useYn && this.holderCount(role) > 0) { this.warnTarget = role; this.warnOpen = true; return }
      this.apply(role)
    },
    applyToggle() { this.warnOpen = false; this.apply(this.warnTarget) },
    async apply(role) {
      try {
        await adminStore.updateRole(role.roleId, { roleName: role.roleName, roleDesc: role.roleDesc, useYn: !role.useYn })
        this.showToast(role.useYn ? '역할을 사용 중지했습니다.' : '역할을 사용하도록 변경했습니다.')
      } catch (err) { this.fail(err) }
    },
  },
}
</script>

<template>
  <div class="pms-page is-narrow">
    <div class="page-header head-row">
      <div>
        <h1>사용자 등록</h1>
        <p>관리자 › 사용자/권한 관리 · 관리자 생성 계정</p>
      </div>
      <router-link :to="{ name: 'AdminUserList' }" class="btn btn-outline">← 목록으로</router-link>
    </div>

    <div class="card">
      <div class="fcard-body">
        <div class="form-row">
          <label for="c-login">아이디<span class="req">*</span></label>
          <div class="inline-check">
            <input id="c-login" v-model="form.loginId" class="form-control" maxlength="20" placeholder="영문 소문자와 숫자 4~20자" @input="loginChecked = false" />
            <button type="button" class="btn btn-outline" @click="checkLogin">중복 확인</button>
          </div>
          <p v-if="errors.loginId" class="field-error">{{ errors.loginId }}</p>
          <p v-else-if="loginChecked" class="field-hint ok">사용할 수 있는 아이디입니다.</p>
        </div>

        <div class="form-grid-2">
          <div class="form-row">
            <label for="c-name">이름<span class="req">*</span></label>
            <input id="c-name" v-model="form.userName" class="form-control" maxlength="50" />
            <p v-if="errors.userName" class="field-error">{{ errors.userName }}</p>
          </div>
          <div class="form-row">
            <label for="c-phone">휴대폰</label>
            <input id="c-phone" v-model="form.phone" class="form-control" placeholder="010-0000-0000" maxlength="13" />
            <p v-if="errors.phone" class="field-error">{{ errors.phone }}</p>
          </div>
        </div>

        <div class="form-row">
          <label for="c-email">이메일<span class="req">*</span></label>
          <input id="c-email" v-model="form.email" type="email" class="form-control" maxlength="100" />
          <p v-if="errors.email" class="field-error">{{ errors.email }}</p>
        </div>

        <div class="form-grid-2">
          <div class="form-row">
            <label for="c-type">계정 유형<span class="req">*</span></label>
            <select id="c-type" v-model="form.accTypeCd" class="form-control">
              <option v-for="(m, code) in accType" :key="code" :value="code">{{ m.label }}</option>
            </select>
          </div>
          <div class="form-row">
            <label for="c-role">초기 시스템 역할<span class="req">*</span></label>
            <select id="c-role" v-model="form.roleId" class="form-control">
              <option v-for="r in activeRoles" :key="r.roleId" :value="r.roleId">{{ r.roleName }} ({{ r.roleCd }})</option>
            </select>
          </div>
        </div>

        <p class="notice-box">
          비밀번호는 관리자가 입력하지 않습니다. 등록하면 <strong>초기 비밀번호 설정 링크가 이메일로 발송</strong>됩니다.<br />
          개인정보 수집 동의는 관리자가 대신할 수 없으며, 사용자가 최초 로그인할 때 본인 동의를 받습니다.
        </p>
      </div>

      <div class="fcard-footer">
        <router-link :to="{ name: 'AdminUserList' }" class="btn btn-outline">취소</router-link>
        <button type="button" class="btn btn-primary" :disabled="submitting" @click="onSubmit">{{ submitting ? '등록 중...' : '등록' }}</button>
      </div>
    </div>
    <AppToast :message="toastMessage" />
  </div>
</template>

<script>
import AppToast from '@/components/common/AppToast.vue'
import adminStore from '@/store/adminStore'
import adminViewMixin from '@/mixins/adminViewMixin'
import { ACC_TYPE } from '@/utils/adminMeta'

const EMAIL = /^\S+@\S+\.\S+$/
const PHONE = /^01[016789]-?\d{3,4}-?\d{4}$/
const LOGIN = /^[a-z0-9]{4,20}$/

export default {
  name: 'UserCreateView',
  components: { AppToast },
  mixins: [adminViewMixin],
  data() {
    const user = adminStore.state.roles.find((r) => r.roleCd === 'USER')
    return {
      accType: ACC_TYPE,
      form: { loginId: '', userName: '', email: '', phone: '', accTypeCd: 'GENERAL', roleId: user ? user.roleId : null }, // 기본 역할 USER
      errors: {}, loginChecked: false, submitting: false,
    }
  },
  computed: {
    activeRoles() { return adminStore.state.roles.filter((r) => r.useYn) },
  },
  methods: {
    checkLogin() {
      const id = this.form.loginId.trim()
      if (!LOGIN.test(id)) { this.errors = { ...this.errors, loginId: '아이디는 영문 소문자와 숫자 4~20자여야 합니다.' }; return }
      if (adminStore.state.users.some((u) => u.loginId === id)) { this.errors = { ...this.errors, loginId: '이미 사용 중인 아이디입니다.' }; this.loginChecked = false; return }
      this.errors = { ...this.errors, loginId: '' }
      this.loginChecked = true
    },
    validate() {
      const e = {}
      if (!LOGIN.test(this.form.loginId.trim())) e.loginId = '아이디는 영문 소문자와 숫자 4~20자여야 합니다.'
      if (!this.form.userName.trim()) e.userName = '이름을 입력해주세요.'
      if (!EMAIL.test(this.form.email.trim())) e.email = '올바른 이메일 형식이 아닙니다.'
      if (this.form.phone && !PHONE.test(this.form.phone.trim())) e.phone = '올바른 휴대폰 번호 형식이 아닙니다.'
      this.errors = e
      return Object.keys(e).length === 0
    },
    async onSubmit() {
      if (!this.validate() || this.submitting) return
      this.submitting = true
      try {
        const created = await adminStore.createUser({ ...this.form, loginId: this.form.loginId.trim(), userName: this.form.userName.trim(), email: this.form.email.trim(), phone: this.form.phone.trim() })
        this.$router.push({ name: 'AdminUserDetail', params: { userId: created.userId }, query: { created: '1' } })
      } catch (err) {
        // 중복 오류는 해당 입력 아래에도 표시한다
        if (err.code === 'LOGIN_ID_DUPLICATE') this.errors = { ...this.errors, loginId: adminStore.ERROR_MESSAGES[err.code] }
        else if (err.code === 'EMAIL_DUPLICATE') this.errors = { ...this.errors, email: adminStore.ERROR_MESSAGES[err.code] }
        else this.fail(err)
      } finally { this.submitting = false }
    },
  },
}
</script>

<style scoped>
.inline-check { display: flex; gap: var(--space-2); }
.field-hint.ok { color: var(--color-success-700); }
</style>

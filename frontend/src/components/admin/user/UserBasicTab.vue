<template>
  <div class="card">
    <div class="fcard-body">
      <div class="form-grid-2">
        <div class="form-row"><label for="b-login">아이디</label><input id="b-login" class="form-control" :value="user.loginId" disabled /></div>
        <div class="form-row">
          <label for="b-name">이름<span class="req">*</span></label>
          <input id="b-name" v-model="form.userName" class="form-control" maxlength="50" />
          <p v-if="errors.userName" class="field-error">{{ errors.userName }}</p>
        </div>
      </div>
      <div class="form-grid-2">
        <div class="form-row">
          <label for="b-email">이메일<span class="req">*</span></label>
          <input id="b-email" v-model="form.email" type="email" class="form-control" maxlength="100" />
          <p v-if="errors.email" class="field-error">{{ errors.email }}</p>
        </div>
        <div class="form-row">
          <label for="b-phone">휴대폰</label>
          <input id="b-phone" v-model="form.phone" class="form-control" maxlength="13" />
          <p v-if="errors.phone" class="field-error">{{ errors.phone }}</p>
        </div>
      </div>
      <div class="form-grid-2">
        <div class="form-row">
          <label for="b-type">계정 유형<span class="req">*</span></label>
          <select id="b-type" v-model="form.accTypeCd" class="form-control">
            <option v-for="(m, code) in accType" :key="code" :value="code">{{ m.label }}</option>
          </select>
        </div>
        <div class="form-row">
          <label for="b-status">계정 상태<span class="req">*</span></label>
          <select id="b-status" v-model="form.accStatusCd" class="form-control">
            <option v-for="(m, code) in accStatus" :key="code" :value="code">{{ m.label }}</option>
          </select>
          <p class="field-hint">잠금은 로그인 실패 시 자동으로 걸리며, '보안' 탭에서 해제합니다.</p>
        </div>
      </div>
      <div class="form-grid-2">
        <div class="form-row"><label for="b-created">가입일시</label><input id="b-created" class="form-control" :value="user.createdAt" disabled /></div>
        <div class="form-row"><label for="b-updated">수정일시</label><input id="b-updated" class="form-control" :value="user.updatedAt" disabled /></div>
      </div>
    </div>
    <div class="fcard-footer">
      <button type="button" class="btn btn-primary" :disabled="saving" @click="onSave">{{ saving ? '저장 중...' : '저장' }}</button>
    </div>

    <!-- 상태를 바꿀 때는 사유를 남긴다 -->
    <ReasonModal v-model="reasonOpen" title="계정 상태 변경" :message="`계정 상태를 '${accStatus[form.accStatusCd].label}'(으)로 변경합니다.`" confirm-text="변경" danger @confirm="save" />
  </div>
</template>

<script>
import ReasonModal from '@/components/common/ReasonModal.vue'
import adminStore from '@/store/adminStore'
import { ACC_STATUS, ACC_TYPE } from '@/utils/adminMeta'

const EMAIL = /^\S+@\S+\.\S+$/
const PHONE = /^01[016789]-?\d{3,4}-?\d{4}$/

export default {
  name: 'UserBasicTab',
  components: { ReasonModal },
  props: { user: { type: Object, required: true } },
  emits: ['toast', 'error'],
  data() {
    const { userName, email, phone, accTypeCd, accStatusCd } = this.user
    return { accStatus: ACC_STATUS, accType: ACC_TYPE, form: { userName, email, phone, accTypeCd, accStatusCd }, errors: {}, saving: false, reasonOpen: false }
  },
  methods: {
    onSave() {
      const e = {}
      if (!this.form.userName.trim()) e.userName = '이름을 입력해주세요.'
      if (!EMAIL.test(this.form.email.trim())) e.email = '올바른 이메일 형식이 아닙니다.'
      if (this.form.phone && !PHONE.test(this.form.phone.trim())) e.phone = '올바른 휴대폰 번호 형식이 아닙니다.'
      this.errors = e
      if (Object.keys(e).length) return
      if (this.form.accStatusCd !== this.user.accStatusCd) this.reasonOpen = true
      else this.save('')
    },
    async save(reason) {
      this.saving = true
      try {
        await adminStore.updateUserBasic(this.user.userId, { ...this.form, userName: this.form.userName.trim(), email: this.form.email.trim(), phone: this.form.phone.trim() }, reason)
        this.reasonOpen = false
        this.$emit('toast', '기본정보를 저장했습니다.')
      } catch (err) {
        this.reasonOpen = false
        if (err.code === 'EMAIL_DUPLICATE') this.errors = { ...this.errors, email: adminStore.ERROR_MESSAGES.EMAIL_DUPLICATE } // 이메일 변경 시 중복 검사
        else this.$emit('error', err)
      } finally { this.saving = false }
    },
  },
}
</script>

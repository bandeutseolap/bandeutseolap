<template>
  <div class="pms-page is-narrow">
    <div class="page-header">
      <h1>계정 관리</h1>
      <p>내 계정 정보와 비밀번호를 관리합니다.</p>
    </div>

    <div class="stack">
      <!-- 프로필 -->
      <section class="card">
        <div class="card-head"><span class="card-title">기본 정보</span></div>
        <div class="fcard-body">
          <div class="form-row">
            <label for="acc-id">아이디</label>
            <input id="acc-id" :value="user.loginId" class="form-control" disabled />
          </div>
          <div class="form-grid-2">
            <div class="form-row">
              <label for="acc-name">이름<span class="req">*</span></label>
              <input id="acc-name" v-model="profile.name" class="form-control" maxlength="50" />
              <p v-if="profileErrors.name" class="field-error">{{ profileErrors.name }}</p>
            </div>
            <div class="form-row">
              <label for="acc-email">이메일<span class="req">*</span></label>
              <input id="acc-email" v-model="profile.email" type="email" class="form-control" maxlength="100" />
              <p v-if="profileErrors.email" class="field-error">{{ profileErrors.email }}</p>
            </div>
          </div>
        </div>
        <div class="fcard-footer">
          <button type="button" class="btn btn-primary" @click="onSaveProfile">저장</button>
        </div>
      </section>

      <!-- 비밀번호 변경 -->
      <section class="card">
        <div class="card-head"><span class="card-title">비밀번호 변경</span></div>
        <div class="fcard-body">
          <div class="form-row">
            <label for="pw-current">현재 비밀번호</label>
            <input id="pw-current" v-model="password.current" type="password" class="form-control" autocomplete="current-password" />
            <p v-if="passwordErrors.current" class="field-error">{{ passwordErrors.current }}</p>
          </div>
          <div class="form-grid-2">
            <div class="form-row">
              <label for="pw-new">새 비밀번호</label>
              <input id="pw-new" v-model="password.next" type="password" class="form-control" autocomplete="new-password" />
              <p v-if="passwordErrors.next" class="field-error">{{ passwordErrors.next }}</p>
            </div>
            <div class="form-row">
              <label for="pw-confirm">새 비밀번호 확인</label>
              <input id="pw-confirm" v-model="password.confirm" type="password" class="form-control" autocomplete="new-password" />
              <p v-if="passwordErrors.confirm" class="field-error">{{ passwordErrors.confirm }}</p>
            </div>
          </div>
        </div>
        <div class="fcard-footer">
          <button type="button" class="btn btn-primary" @click="onChangePassword">비밀번호 변경</button>
        </div>
      </section>
    </div>

    <AppToast :message="toastMessage" />
  </div>
</template>

<script>
import AppToast from '@/components/common/AppToast.vue'
import sampleStore from '@/store/sampleStore'
import authStore from '@/store/authStore'
import { loadMyProfile } from '@/services/authService'
import sampleViewMixin from '@/mixins/sampleViewMixin'

const MIN_PASSWORD_LENGTH = 8

export default {
  name: 'AccountManageView',
  components: { AppToast },
  mixins: [sampleViewMixin],
  data() {
    // 내 정보 조회가 끝나기 전에도 화면이 비지 않도록 현재 값으로 먼저 채운다
    const { name, email } = authStore.state.profile || sampleStore.state.currentUser
    return {
      profile: { name, email },
      profileErrors: {},
      password: { current: '', next: '', confirm: '' },
      passwordErrors: {},
    }
  },
  computed: {
    user() {
      return authStore.state.profile || sampleStore.state.currentUser
    },
  },
  async created() {
    // 새로고침으로 바로 들어온 경우를 포함해, 로그인한 사용자의 내 정보로 폼을 채운다
    try {
      const { name, email } = await loadMyProfile()
      this.profile = { name, email }
    } catch (error) {
      console.error('내 정보 조회 실패', error)
    }
  },
  methods: {
    async onSaveProfile() {
      const e = {}
      if (!this.profile.name.trim()) e.name = '이름을 입력해주세요.'
      // 간단한 형식 검사만 한다 (정확한 검증은 서버에서)
      if (!/^\S+@\S+\.\S+$/.test(this.profile.email.trim())) e.email = '올바른 이메일 형식이 아닙니다.'
      this.profileErrors = e
      if (Object.keys(e).length) return

      await sampleStore.saveProfile(this.user.loginId, { name: this.profile.name.trim(), email: this.profile.email.trim() })
      // 헤더 프로필에도 바로 반영되도록 다시 조회한다
      await loadMyProfile({ force: true })
      this.showToast('계정 정보가 저장되었습니다.')
    },
    async onChangePassword() {
      const e = {}
      if (!this.password.current) e.current = '현재 비밀번호를 입력해주세요.'
      if (this.password.next.length < MIN_PASSWORD_LENGTH) e.next = `새 비밀번호는 ${MIN_PASSWORD_LENGTH}자 이상이어야 합니다.`
      else if (this.password.next === this.password.current) e.next = '현재 비밀번호와 다른 비밀번호를 입력해주세요.'
      if (this.password.confirm !== this.password.next) e.confirm = '새 비밀번호가 일치하지 않습니다.'
      this.passwordErrors = e
      if (Object.keys(e).length) return

      // TODO(API 연동): await axios.put('/api/account/password', { current, next })
      //                 비밀번호는 store/로그에 남기지 않는다.
      this.password = { current: '', next: '', confirm: '' }
      this.showToast('비밀번호가 변경되었습니다.')
    },
  },
}
</script>

<template>
  <div class="stack">
    <section class="card">
      <div class="card-head">
        <span class="card-title">보안 정보</span>
        <div class="card-actions">
          <button type="button" class="btn btn-outline btn-sm" :disabled="!sec.lockYn" @click="onUnlock">잠금 해제</button>
          <button type="button" class="btn btn-outline btn-sm" @click="resetPwOpen = true">비밀번호 초기화</button>
          <button type="button" class="btn btn-outline btn-sm" :disabled="!sec.mfaYn" @click="resetMfaOpen = true">MFA 초기화</button>
        </div>
      </div>
      <dl class="info-grid">
        <div><dt>비밀번호 변경일시</dt><dd>{{ sec.pwChangedAt || '-' }}</dd></div>
        <div>
          <dt>비밀번호 만료</dt>
          <dd><span class="chip" :class="expired ? 'chip-error' : 'chip-success'">{{ expired ? '만료' : '유효' }}</span>
            <span class="cell-sub">기준 {{ expireDays }}일 (PASSWORD_EXPIRE_DAYS)</span></dd>
        </div>
        <div><dt>로그인 실패 횟수</dt><dd>{{ sec.failCount }}회</dd></div>
        <div><dt>마지막 실패일시</dt><dd>{{ sec.lastFailAt || '-' }}</dd></div>
        <div><dt>잠금 여부</dt><dd><span class="chip" :class="sec.lockYn ? 'chip-error' : 'badge-muted'">{{ sec.lockYn ? '잠금' : '잠금 아님' }}</span></dd></div>
        <div><dt>잠금일시</dt><dd>{{ sec.lockedAt || '-' }}</dd></div>
        <div><dt>MFA 인증 여부</dt><dd>{{ sec.mfaYn ? '사용' : '미사용' }}</dd></div>
        <div><dt>MFA 수단</dt><dd>{{ mfaMethods[sec.mfaMethod].label }}</dd></div>
        <div><dt>최종 MFA 인증</dt><dd>{{ sec.mfaVerifiedAt || '-' }}</dd></div>
      </dl>
    </section>

    <p class="notice-box">
      비밀번호 초기화는 임시 비밀번호를 화면에 노출하지 않고, <strong>재설정 링크를 사용자 이메일로 발송</strong>합니다.
    </p>

    <YesNoModal v-model="resetPwOpen" title="비밀번호 초기화" message="재설정 링크를 사용자 이메일로 발송할까요?" :sub-message="`수신: ${user.email}`" @confirm="onResetPw" />
    <YesNoModal v-model="resetMfaOpen" title="MFA 초기화" message="MFA 설정을 초기화할까요?" sub-message="사용자는 다음 로그인 후 MFA 를 다시 설정해야 합니다." @confirm="onResetMfa" />
  </div>
</template>

<script>
import dayjs from 'dayjs'
import YesNoModal from '@/components/common/YesNoModal.vue'
import adminStore from '@/store/adminStore'
import { MFA_METHODS } from '@/utils/myPageMeta'

export default {
  name: 'UserSecurityTab',
  components: { YesNoModal },
  props: { user: { type: Object, required: true } },
  emits: ['toast', 'error'],
  data() { return { mfaMethods: MFA_METHODS, resetPwOpen: false, resetMfaOpen: false } },
  computed: {
    sec() { return this.user.security },
    expireDays() { return adminStore.state.systemVariables.PASSWORD_EXPIRE_DAYS },
    // 비밀번호 만료 여부 = 변경일 + PASSWORD_EXPIRE_DAYS 가 지났는가
    expired() { return !!this.sec.pwChangedAt && dayjs(this.sec.pwChangedAt).add(this.expireDays, 'day').isBefore(dayjs()) },
  },
  methods: {
    async onUnlock() {
      await adminStore.unlockUsers([this.user.userId])
      this.$emit('toast', '잠금을 해제했습니다. 로그인 실패 횟수가 초기화되었습니다.')
    },
    async onResetPw() {
      await adminStore.resetPassword(this.user.userId)
      this.resetPwOpen = false
      this.$emit('toast', '비밀번호 재설정 링크를 이메일로 발송했습니다.')
    },
    async onResetMfa() {
      await adminStore.resetMfa(this.user.userId)
      this.resetMfaOpen = false
      this.$emit('toast', 'MFA 를 초기화했습니다.')
    },
  },
}
</script>

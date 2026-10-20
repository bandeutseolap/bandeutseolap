<template>
  <div class="pms-page is-narrow">
    <div class="page-header head-row">
      <div>
        <h1>보안 설정</h1>
        <p>비밀번호, 추가 인증(MFA), 마지막 로그인 정보를 관리합니다.</p>
      </div>
      <router-link :to="{ name: 'MyPage' }" class="btn btn-outline">← 마이페이지</router-link>
    </div>

    <div v-if="loading" class="card"><div class="empty-state">보안 설정을 불러오는 중입니다.</div></div>

    <div v-else class="stack">
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
          <button type="button" class="btn btn-primary" :disabled="passwordSubmitting" @click="onChangePassword">
            {{ passwordSubmitting ? '변경 중...' : '비밀번호 변경' }}
          </button>
        </div>
      </section>

      <!-- MFA 설정 -->
      <section class="card">
        <div class="card-head"><span class="card-title">MFA 설정</span></div>
        <div class="fcard-body">
          <div class="form-grid-2">
            <div class="form-row">
              <label for="mfa-method">수단</label>
              <select id="mfa-method" v-model="mfaMethod" class="form-control">
                <option v-for="(meta, code) in mfaMethods" :key="code" :value="code">{{ meta.label }}</option>
              </select>
            </div>
            <div class="form-row">
              <label for="mfa-verified">최종 인증일</label>
              <input id="mfa-verified" class="form-control" :value="security.mfaVerifiedAt || '-'" disabled />
            </div>
          </div>
        </div>
        <div class="fcard-footer">
          <button type="button" class="btn btn-primary" :disabled="mfaSubmitting || !mfaChanged" @click="onSaveMfa">
            {{ mfaSubmitting ? '저장 중...' : 'MFA 저장' }}
          </button>
        </div>
      </section>

      <!-- 마지막 로그인 -->
      <section class="card">
        <div class="card-head"><span class="card-title">마지막 로그인</span></div>
        <dl class="info-list">
          <div><dt>일시</dt><dd>{{ security.lastLoginAt || '-' }}</dd></div>
          <div><dt>접속 IP</dt><dd>{{ security.lastLoginIp || '-' }}</dd></div>
        </dl>
      </section>
    </div>

    <AppToast :message="toastMessage" />
  </div>
</template>

<script>
import AppToast from '@/components/common/AppToast.vue'
import sampleViewMixin from '@/mixins/sampleViewMixin'
import { fetchSecurity, changePassword, saveMfa } from '@/services/myPageService'
import { MFA_METHODS } from '@/utils/myPageMeta'

const MIN_PASSWORD_LENGTH = 8

export default {
  name: 'MySecurityView',
  components: { AppToast },
  mixins: [sampleViewMixin],
  data() {
    return {
      mfaMethods: MFA_METHODS,
      loading: true,
      security: { mfaMethod: 'NONE', mfaVerifiedAt: '', lastLoginAt: '', lastLoginIp: '' },
      mfaMethod: 'NONE',
      mfaSubmitting: false,
      password: { current: '', next: '', confirm: '' },
      passwordErrors: {},
      passwordSubmitting: false,
    }
  },
  computed: {
    mfaChanged() {
      return this.mfaMethod !== this.security.mfaMethod
    },
  },
  async created() {
    try {
      this.security = await fetchSecurity()
      this.mfaMethod = this.security.mfaMethod
    } catch (err) {
      // TODO: 공통 에러 처리(토스트/알림) 컨벤션에 맞춰 연결
      console.error('보안 설정 조회 실패', err)
    } finally {
      this.loading = false
    }
  },
  methods: {
    async onChangePassword() {
      const e = {}
      if (!this.password.current) e.current = '현재 비밀번호를 입력해주세요.'
      if (this.password.next.length < MIN_PASSWORD_LENGTH) e.next = `새 비밀번호는 ${MIN_PASSWORD_LENGTH}자 이상이어야 합니다.`
      else if (this.password.next === this.password.current) e.next = '현재 비밀번호와 다른 비밀번호를 입력해주세요.'
      if (this.password.confirm !== this.password.next) e.confirm = '새 비밀번호가 일치하지 않습니다.'
      this.passwordErrors = e
      if (Object.keys(e).length || this.passwordSubmitting) return

      this.passwordSubmitting = true
      try {
        await changePassword({ current: this.password.current, next: this.password.next })
        this.password = { current: '', next: '', confirm: '' }
        this.showToast('비밀번호가 변경되었습니다.')
      } catch (err) {
        // TODO: 공통 에러 처리(토스트/알림) 컨벤션에 맞춰 연결 (예: 현재 비밀번호 불일치)
        console.error('비밀번호 변경 실패', err)
      } finally {
        this.passwordSubmitting = false
      }
    },
    async onSaveMfa() {
      if (this.mfaSubmitting) return
      this.mfaSubmitting = true
      try {
        const saved = await saveMfa(this.mfaMethod)
        this.security = { ...this.security, mfaMethod: saved.mfaMethod, mfaVerifiedAt: saved.mfaVerifiedAt }
        this.showToast(this.mfaMethod === 'NONE' ? 'MFA를 사용하지 않도록 설정했습니다.' : 'MFA 설정이 저장되었습니다.')
      } catch (err) {
        // TODO: 공통 에러 처리(토스트/알림) 컨벤션에 맞춰 연결
        console.error('MFA 설정 실패', err)
      } finally {
        this.mfaSubmitting = false
      }
    },
  },
}
</script>

<style scoped>
.info-list {
  margin: 0;
  padding: var(--space-2) var(--space-5);
}

.info-list > div {
  display: flex;
  gap: var(--space-4);
  padding: 12px 0;
  border-bottom: 1px solid var(--color-border);
}
.info-list > div:last-child { border-bottom: none; }

.info-list dt {
  flex-shrink: 0;
  width: 90px;
  font-size: var(--font-size-sm);
  font-weight: 600;
  color: var(--color-text-muted);
}

.info-list dd {
  margin: 0;
  font-size: var(--font-size-sm);
  color: var(--color-text);
}
</style>

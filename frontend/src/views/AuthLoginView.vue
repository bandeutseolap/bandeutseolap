<template>
  <div class="login-page">
    <div class="login-shell">
      <section class="brand-panel">
        <div class="brand-wrap">
          <p class="eyebrow">WorkStation Platform</p>
          <h1>반듯서랍</h1>
          <p class="brand-desc">
            문서, 업무, 커뮤니케이션을 하나의 작업 흐름으로 연결하는
            협업 워크스테이션.
          </p>

          <div class="brand-card-list">
            <div class="brand-card">
              <span class="card-label">Document-Centered</span>
              <strong>문서와 작업의 연결</strong>
              <p>문서, 게시판, 프로젝트를 하나의 흐름으로 관리합니다.</p>
            </div>
            <div class="brand-card">
              <span class="card-label">Integrated Workspace</span>
              <strong>일관된 작업 경험</strong>
              <p>웹, 앱, 실행 환경 전반에 동일한 UX 구조를 지향합니다.</p>
            </div>
          </div>
        </div>
      </section>

      <section class="form-panel">
        <div class="form-wrap">
          <div class="form-header">
            <p class="form-kicker">Welcome back</p>
            <h2>로그인</h2>
            <p class="form-subtext">
              반듯서랍 계정으로 로그인하여 프로젝트와 문서를 이어서 작업하세요.
            </p>
          </div>

          <form class="login-form" @submit.prevent="handleSubmit">
            <label class="field-group" for="loginId">
              <span>아이디</span>
              <input
                  id="loginId"
                  v-model="form.loginId"
                  type="text"
                  placeholder="아이디를 입력하세요"
                  autocomplete="username"
              />
            </label>

            <label class="field-group" for="password">
              <span>비밀번호</span>
              <div class="password-wrap">
                <input
                    id="password"
                    v-model="form.password"
                    :type="showPassword ? 'text' : 'password'"
                    placeholder="비밀번호를 입력하세요"
                    autocomplete="current-password"
                />
                <button
                    type="button"
                    class="toggle-btn"
                    @click="showPassword = !showPassword"
                >
                  {{ showPassword ? '숨김' : '보기' }}
                </button>
              </div>
            </label>

            <div class="form-options">
              <label class="checkbox-wrap">
                <input v-model="form.rememberMe" type="checkbox" />
                <span>로그인 상태 유지</span>
              </label>

              <button type="button" class="text-btn">비밀번호 찾기</button>
            </div>

            <button type="submit" class="submit-btn" :disabled="loading">
              {{ loading ? '로그인 중...' : '로그인' }}
            </button>
          </form>

          <div v-if="errorMessage" class="error-box">
            {{ errorMessage }}
          </div>

          <div class="divider">
            <span>또는</span>
          </div>

          <div class="sub-actions">
            <button type="button" class="secondary-btn">사원번호 / SSO 로그인</button>
            <button type="button" class="ghost-btn">계정 생성 요청</button>
          </div>

          <footer class="form-footer">
            <span>Version 0.1</span>
            <span class="dot"></span>
            <span>On-Premise Ready</span>
          </footer>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { fetchUserLogin } from "@/services/authService.js";

const router = useRouter()

const loading = ref(false)
const showPassword = ref(false)
const errorMessage = ref('')

const form = reactive({
  loginId: '',
  password: '',
  rememberMe: false,
})

const handleSubmit = async () => {
  errorMessage.value = ''

  if (!form.loginId || !form.password) {
    errorMessage.value = '아이디와 비밀번호를 모두 입력해주세요.'
    return
  }

  try {
    loading.value = true

    // Login Service 연결
    await fetchUserLogin({
      loginId: form.loginId,
      password: form.password
    })

    router.push('/')

  } catch (error) {
    errorMessage.value = '로그인에 실패했습니다. 계정 정보를 다시 확인해주세요.'
    console.error(error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* 로그인 화면은 앱 셸(DefaultLayout) 밖에 있으므로 자체 레이아웃을 가진다.
   색상/반경/그림자는 모두 common.css 토큰을 사용한다.
   (이전의 :global(*) / :global(body) 덮어쓰기는 전역 base 와 충돌해서 제거) */
.login-page {
  min-height: 100vh;
  padding: var(--space-6);
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-bg);
}

.login-shell {
  width: 100%;
  max-width: 1080px;
  min-height: 600px;
  display: grid;
  grid-template-columns: 1.1fr 0.9fr;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-xl);
  overflow: hidden;
  background: var(--color-surface);
  box-shadow: var(--shadow-md);
}

/* ---- 좌측 브랜드 패널 (TopNav 와 같은 네이비 톤) ---- */
.brand-panel {
  display: flex;
  align-items: stretch;
  padding: 48px;
  background: var(--color-nav-bg);
  color: var(--color-nav-fg);
}

.brand-wrap {
  display: flex;
  flex-direction: column;
  justify-content: center;
  width: 100%;
}

.eyebrow {
  margin: 0 0 var(--space-3);
  font-size: var(--font-size-xs);
  font-weight: 600;
  letter-spacing: 0.8px;
  text-transform: uppercase;
  color: var(--color-primary-300);
}

.brand-wrap h1 {
  margin: 0 0 var(--space-4);
  font-size: 36px;
  font-weight: 700;
  letter-spacing: -1px;
  color: #fff;
}

.brand-desc {
  max-width: 420px;
  margin: 0 0 var(--space-8);
  font-size: var(--font-size-md);
  line-height: 1.7;
}

.brand-card-list {
  display: grid;
  gap: var(--space-3);
}

.brand-card {
  padding: var(--space-4);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: var(--radius-lg);
  background: rgba(255, 255, 255, 0.05);
}

.card-label {
  display: block;
  margin-bottom: 6px;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.6px;
  text-transform: uppercase;
  color: var(--color-primary-300);
}

.brand-card strong {
  display: block;
  margin-bottom: 4px;
  font-size: var(--font-size-md);
  color: #fff;
}

.brand-card p {
  margin: 0;
  font-size: var(--font-size-sm);
  line-height: 1.6;
}

/* ---- 우측 폼 패널 ---- */
.form-panel {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px 40px;
}

.form-wrap {
  width: 100%;
  max-width: 360px;
}

.form-header {
  margin-bottom: var(--space-6);
}

.form-kicker {
  margin: 0 0 var(--space-1);
  font-size: var(--font-size-xs);
  font-weight: 600;
  color: var(--color-primary-600);
}

.form-header h2 {
  margin: 0 0 var(--space-2);
  font-size: var(--font-size-2xl);
  font-weight: 700;
  letter-spacing: -0.6px;
  color: var(--color-text);
}

.form-subtext {
  margin: 0;
  font-size: var(--font-size-sm);
  line-height: 1.6;
  color: var(--color-text-soft);
}

.login-form {
  display: grid;
  gap: var(--space-4);
}

.field-group {
  display: grid;
  gap: 6px;
}

.field-group > span {
  font-size: var(--font-size-xs);
  font-weight: 600;
  color: var(--color-text-soft);
}

.field-group input,
.password-wrap input {
  width: 100%;
  height: 40px;
  padding: 0 var(--space-3);
  border: 1px solid var(--color-border-strong);
  border-radius: var(--radius-md);
  background: var(--color-surface);
  color: var(--color-text);
  font-size: var(--font-size-sm);
  transition: border-color 0.15s ease, box-shadow 0.15s ease;
}

.field-group input::placeholder {
  color: var(--color-text-faint);
}

.field-group input:focus {
  outline: none;
  border-color: var(--color-primary-500);
  box-shadow: 0 0 0 3px var(--color-focus-ring);
}

.password-wrap {
  position: relative;
}

.password-wrap input {
  padding-right: 64px;
}

.toggle-btn {
  position: absolute;
  top: 50%;
  right: 6px;
  transform: translateY(-50%);
  padding: 4px 8px;
  border: none;
  border-radius: var(--radius-sm);
  background: none;
  font-size: var(--font-size-xs);
  font-weight: 600;
  color: var(--color-text-muted);
  cursor: pointer;
}

.toggle-btn:hover {
  background: var(--color-surface-muted);
  color: var(--color-text);
}

.form-options {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.checkbox-wrap {
  display: inline-flex;
  align-items: center;
  gap: var(--space-2);
  font-size: var(--font-size-sm);
  color: var(--color-text-soft);
  cursor: pointer;
}

.checkbox-wrap input {
  width: 15px;
  height: 15px;
  accent-color: var(--color-primary-600);
}

.text-btn {
  padding: 0;
  border: none;
  background: none;
  font-size: var(--font-size-sm);
  font-weight: 500;
  color: var(--color-primary-600);
  cursor: pointer;
}

.text-btn:hover {
  color: var(--color-primary-700);
  text-decoration: underline;
}

.submit-btn,
.secondary-btn,
.ghost-btn {
  width: 100%;
  height: 40px;
  border-radius: var(--radius-md);
  font-size: var(--font-size-sm);
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.15s ease, border-color 0.15s ease, color 0.15s ease;
}

.submit-btn {
  border: 1px solid var(--color-primary-600);
  background: var(--color-primary-600);
  color: #fff;
}

.submit-btn:hover:not(:disabled) {
  border-color: var(--color-primary-700);
  background: var(--color-primary-700);
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.secondary-btn {
  border: 1px solid var(--color-border-strong);
  background: var(--color-surface);
  color: var(--color-text);
}

.secondary-btn:hover {
  background: var(--color-surface-muted);
}

.ghost-btn {
  border: 1px solid transparent;
  background: none;
  color: var(--color-text-soft);
}

.ghost-btn:hover {
  background: var(--color-surface-muted);
  color: var(--color-text);
}

.error-box {
  margin-top: var(--space-4);
  padding: var(--space-3);
  border: 1px solid var(--color-error-200);
  border-radius: var(--radius-md);
  background: var(--color-error-50);
  color: var(--color-error-700);
  font-size: var(--font-size-sm);
}

.divider {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  margin: var(--space-5) 0;
  font-size: var(--font-size-xs);
  color: var(--color-text-faint);
}

.divider::before,
.divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background: var(--color-border);
}

.sub-actions {
  display: grid;
  gap: var(--space-2);
}

.form-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-2);
  margin-top: var(--space-6);
  font-size: var(--font-size-xs);
  color: var(--color-text-muted);
}

.form-footer .dot {
  width: 3px;
  height: 3px;
  border-radius: 50%;
  background: var(--color-text-faint);
}

@media (max-width: 900px) {
  .login-page {
    padding: var(--space-4);
  }

  .login-shell {
    grid-template-columns: 1fr;
    min-height: 0;
  }

  .brand-panel {
    display: none;
  }
}
</style>

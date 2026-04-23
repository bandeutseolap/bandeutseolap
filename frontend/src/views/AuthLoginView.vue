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

    // TODO:
    // 실제 API 연동 시 아래 로직을 교체하세요.
    // await authApi.login({
    //   loginId: form.loginId,
    //   password: form.password,
    //   rememberMe: form.rememberMe,
    // })

    await new Promise((resolve) => setTimeout(resolve, 800))

    console.log('login payload', {
      loginId: form.loginId,
      password: form.password,
      rememberMe: form.rememberMe,
    })
  } catch (error) {
    errorMessage.value = '로그인에 실패했습니다. 계정 정보를 다시 확인해주세요.'
    console.error(error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
:global(*) {
  box-sizing: border-box;
}

:global(body) {
  margin: 0;
  font-family: Inter, Pretendard, 'Noto Sans KR', system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
  background: #0b1020;
}

button,
input {
  font: inherit;
}

button {
  cursor: pointer;
}

.login-page {
  min-height: 100vh;
  padding: 24px;
  background:
      radial-gradient(circle at top left, rgba(80, 110, 255, 0.22), transparent 28%),
      radial-gradient(circle at bottom right, rgba(32, 197, 158, 0.16), transparent 22%),
      linear-gradient(135deg, #0b1020 0%, #11182d 42%, #0d1324 100%);
  color: #edf2ff;
}

.login-shell {
  max-width: 1240px;
  min-height: calc(100vh - 48px);
  margin: 0 auto;
  display: grid;
  grid-template-columns: 1.15fr 0.85fr;
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 28px;
  overflow: hidden;
  background: rgba(10, 14, 27, 0.72);
  backdrop-filter: blur(24px);
  box-shadow: 0 30px 80px rgba(0, 0, 0, 0.35);
}

.brand-panel {
  padding: 56px;
  display: flex;
  align-items: stretch;
  background:
      linear-gradient(180deg, rgba(255, 255, 255, 0.03), rgba(255, 255, 255, 0.01)),
      linear-gradient(140deg, rgba(67, 97, 238, 0.18), rgba(42, 157, 143, 0.12));
}

.brand-wrap {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  gap: 40px;
}

.eyebrow,
.form-kicker,
.card-label {
  margin: 0;
  font-size: 12px;
  line-height: 1;
  letter-spacing: 0.18em;
  text-transform: uppercase;
  color: #8ea3ff;
}

.brand-wrap h1 {
  margin: 12px 0 20px;
  font-size: clamp(40px, 5vw, 64px);
  line-height: 1.05;
  letter-spacing: -0.04em;
}

.brand-desc {
  max-width: 540px;
  margin: 0;
  font-size: 18px;
  line-height: 1.7;
  color: rgba(237, 242, 255, 0.78);
}

.brand-card-list {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.brand-card {
  padding: 22px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.brand-card strong {
  display: block;
  margin: 14px 0 10px;
  font-size: 18px;
  font-weight: 700;
}

.brand-card p {
  margin: 0;
  color: rgba(237, 242, 255, 0.72);
  line-height: 1.6;
}

.form-panel {
  padding: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.02);
}

.form-wrap {
  width: 100%;
  max-width: 420px;
}

.form-header h2 {
  margin: 10px 0 12px;
  font-size: 34px;
  line-height: 1.15;
  letter-spacing: -0.04em;
}

.form-subtext {
  margin: 0;
  color: rgba(237, 242, 255, 0.7);
  line-height: 1.65;
}

.login-form {
  margin-top: 32px;
}

.field-group {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 18px;
}

.field-group span {
  font-size: 14px;
  font-weight: 600;
  color: #d9e2ff;
}

.field-group input {
  width: 100%;
  height: 52px;
  padding: 0 16px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 14px;
  background: rgba(8, 12, 24, 0.78);
  color: #f8faff;
  outline: none;
  transition: border-color 0.2s ease, box-shadow 0.2s ease, background 0.2s ease;
}

.field-group input::placeholder {
  color: rgba(237, 242, 255, 0.34);
}

.field-group input:focus {
  border-color: rgba(120, 143, 255, 0.9);
  box-shadow: 0 0 0 4px rgba(120, 143, 255, 0.14);
  background: rgba(10, 15, 29, 0.95);
}

.password-wrap {
  position: relative;
}

.password-wrap input {
  padding-right: 72px;
}

.toggle-btn {
  position: absolute;
  top: 50%;
  right: 12px;
  transform: translateY(-50%);
  border: 0;
  background: transparent;
  color: #9bb0ff;
  font-size: 13px;
  font-weight: 700;
}

.form-options {
  margin: 10px 0 22px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.checkbox-wrap {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: rgba(237, 242, 255, 0.72);
  font-size: 14px;
}

.checkbox-wrap input {
  width: 16px;
  height: 16px;
}

.text-btn,
.ghost-btn,
.secondary-btn {
  border: 0;
  background: transparent;
  color: #aebdff;
}

.text-btn {
  padding: 0;
  font-size: 14px;
  font-weight: 600;
}

.submit-btn {
  width: 100%;
  height: 54px;
  border: 0;
  border-radius: 16px;
  background: linear-gradient(135deg, #6e7fff 0%, #4d63ff 100%);
  color: #ffffff;
  font-size: 16px;
  font-weight: 700;
  box-shadow: 0 14px 30px rgba(77, 99, 255, 0.28);
  transition: transform 0.2s ease, box-shadow 0.2s ease, opacity 0.2s ease;
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 18px 36px rgba(77, 99, 255, 0.34);
}

.submit-btn:disabled {
  opacity: 0.72;
  cursor: wait;
}

.error-box {
  margin-top: 16px;
  padding: 14px 16px;
  border-radius: 14px;
  background: rgba(255, 84, 104, 0.12);
  border: 1px solid rgba(255, 84, 104, 0.22);
  color: #ffb7c0;
  font-size: 14px;
}

.divider {
  position: relative;
  margin: 28px 0 20px;
  text-align: center;
}

.divider::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 1px;
  background: rgba(255, 255, 255, 0.08);
}

.divider span {
  position: relative;
  display: inline-block;
  padding: 0 12px;
  background: rgba(15, 20, 37, 0.96);
  color: rgba(237, 242, 255, 0.48);
  font-size: 13px;
}

.sub-actions {
  display: grid;
  gap: 10px;
}

.secondary-btn,
.ghost-btn {
  height: 48px;
  border-radius: 14px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.03);
  color: #eef2ff;
  font-weight: 600;
}

.ghost-btn {
  background: transparent;
  color: #b6c4ff;
}

.form-footer {
  margin-top: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  color: rgba(237, 242, 255, 0.42);
  font-size: 12px;
}

.dot {
  width: 4px;
  height: 4px;
  border-radius: 999px;
  background: rgba(237, 242, 255, 0.3);
}

@media (max-width: 980px) {
  .login-shell {
    grid-template-columns: 1fr;
  }

  .brand-panel {
    padding: 40px;
  }

  .brand-card-list {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .login-page {
    padding: 16px;
  }

  .brand-panel,
  .form-panel {
    padding: 24px;
  }

  .form-options {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>

<template>
  <!-- 로그인 프로필: 시안의 .avatar(그라디언트 원형 + 이니셜)를 버튼으로 두고, 클릭하면 메뉴를 연다 -->
  <div ref="root" class="profile" @focusout="onFocusOut">
    <button
      type="button"
      class="avatar"
      :title="displayName"
      :aria-label="`${displayName} 프로필 메뉴`"
      aria-haspopup="true"
      :aria-expanded="open"
      @click="open = !open"
      @keydown.esc="close"
    >
      {{ initial }}
    </button>

    <div v-show="open" class="profile-menu">
      <div class="profile-head">
        <span class="avatar avatar-lg" aria-hidden="true">{{ initial }}</span>
        <div class="profile-info">
          <div class="profile-name">{{ displayName }}</div>
          <div class="profile-sub">{{ profile ? profile.email : '로그인됨' }}</div>
        </div>
      </div>

      <!-- 역할/담당: 내 정보 조회가 끝난 뒤에만 표시 -->
      <div v-if="profile" class="profile-role">
        <span class="chip" :class="roleChip">{{ profile.role }}</span>
        <span v-if="profile.description" class="profile-desc">{{ profile.description }}</span>
      </div>

      <router-link :to="{ name: 'MyPage' }" class="profile-item" @click="close" @keydown.esc="close">마이페이지</router-link>
      <button type="button" class="profile-item" @click="onLogout" @keydown.esc="close">로그아웃</button>
    </div>
  </div>
</template>

<script>
import authStore from '@/store/authStore'
import sampleStore from '@/store/sampleStore'
import { loadMyProfile, requestLogout } from '@/services/authService'
import { ROLE_CHIP } from '@/utils/workMeta'

export default {
  name: 'ProfileMenu',
  data() {
    return { open: false }
  },
  computed: {
    profile() {
      return authStore.state.profile
    },
    // 내 정보 조회 전에는 로그인 때 저장한 아이디, 그것도 없으면 샘플 사용자로 대체한다.
    displayName() {
      return (
        (this.profile && this.profile.name) ||
        authStore.state.loginId ||
        sampleStore.state.currentUser.loginId
      )
    },
    initial() {
      return this.displayName.charAt(0).toUpperCase()
    },
    roleChip() {
      return (this.profile && ROLE_CHIP[this.profile.role]) || 'badge-muted'
    },
  },
  watch: {
    '$route.fullPath'() {
      this.close()
    },
    // 다른 탭에서 계정이 바뀌어 프로필이 비워지면 다시 조회한다
    profile(value) {
      if (!value && authStore.state.loggedIn) this.loadProfile()
    },
  },
  created() {
    // 새로고침 직후처럼 토큰만 있고 프로필이 없는 경우를 채운다
    if (!this.profile) this.loadProfile()
  },
  mounted() {
    // 메뉴 바깥을 클릭하면 닫는다 (Safari 처럼 버튼이 포커스를 받지 않는 브라우저 대응)
    document.addEventListener('click', this.onDocumentClick)
  },
  beforeUnmount() {
    document.removeEventListener('click', this.onDocumentClick)
  },
  methods: {
    close() {
      this.open = false
    },
    onDocumentClick(event) {
      if (this.open && this.$refs.root && !this.$refs.root.contains(event.target)) this.close()
    },
    onFocusOut(event) {
      if (!event.currentTarget.contains(event.relatedTarget)) this.close()
    },
    async loadProfile() {
      try {
        await loadMyProfile()
      } catch (error) {
        // 프로필 조회 실패는 헤더 동작을 막지 않는다 (아이디/이니셜로 대체 표시)
        console.error('내 정보 조회 실패', error)
      }
    },
    async onLogout() {
      this.close()
      try {
        await requestLogout()
      } catch (error) {
        // 서버 요청이 실패해도 이 브라우저의 로그아웃은 반드시 진행한다
        console.error('서버 로그아웃 요청 실패', error)
      } finally {
        authStore.logout()
        this.$router.push({ name: 'AuthLogin' })
      }
    },
  },
}
</script>

<style scoped>
.profile {
  position: relative;
  display: flex;
  align-items: center;
}

/* 시안 .avatar */
.avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  width: 30px;
  height: 30px;
  padding: 0;
  border: none;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--color-primary-600) 0%, var(--color-accent-500) 100%);
  font-size: 11px;
  font-weight: 700;
  color: #fff;
  cursor: pointer;
}

button.avatar:hover {
  box-shadow: 0 0 0 3px rgba(255, 255, 255, 0.15);
}

.avatar-lg {
  width: 36px;
  height: 36px;
  font-size: 13px;
  cursor: default;
}

.profile-menu {
  position: absolute;
  top: calc(100% + 10px);
  right: 0;
  z-index: 200;
  min-width: 240px;
  padding: var(--space-1);
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-md);
}

.profile-head {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px 6px;
}

.profile-info { min-width: 0; }

.profile-name {
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  font-size: var(--font-size-sm);
  font-weight: 700;
  color: var(--color-text);
}

.profile-sub {
  font-size: 11.5px;
  color: var(--color-text-muted);
}

.profile-role {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: 6px 12px 10px;
  border-bottom: 1px solid var(--color-border);
  margin-bottom: var(--space-1);
}

.profile-desc {
  min-width: 0;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  font-size: 11.5px;
  color: var(--color-text-muted);
}

.profile-item {
  display: block;
  width: 100%;
  padding: 8px 12px;
  border: none;
  border-radius: var(--radius-sm);
  background: none;
  font-size: var(--font-size-sm);
  font-weight: 500;
  text-align: left;
  color: var(--color-text-soft);
  white-space: nowrap;
  cursor: pointer;
}

.profile-item:hover {
  background: var(--color-surface-muted);
  color: var(--color-text);
}
</style>

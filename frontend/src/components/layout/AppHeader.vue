<template>
  <header class="app-topnav">
    <!-- 브랜드 영역: 폭을 사이드바와 맞춰 상단/좌측 구분선이 이어지도록 함 -->
    <router-link to="/" class="brand">
      <span class="brand-mark" aria-hidden="true">반</span>
      <span class="brand-name">반듯서랍</span>
    </router-link>

    <!-- 상단 메뉴: hover / 키보드 포커스 / 클릭(터치) 으로 드롭다운을 연다 -->
    <nav class="nav-links" aria-label="상단 메뉴">
      <div
        v-for="(menu, index) in menus"
        :key="menu.label"
        class="nav-item"
        @mouseenter="openMenu(index)"
        @mouseleave="closeMenuLater"
        @focusout="onFocusOut"
      >
        <button
          type="button"
          class="nav-link"
          :class="{ active: isMenuActive(menu), open: openIndex === index }"
          :aria-expanded="openIndex === index"
          aria-haspopup="true"
          @click="toggleMenu(index)"
          @keydown.esc="closeMenu"
        >
          {{ menu.label }}
        </button>

        <ul v-show="openIndex === index" class="nav-dropdown">
          <li v-for="child in menu.children" :key="child.label">
            <router-link
              v-if="child.to"
              :to="child.to"
              class="nav-dropdown-item"
              :class="{ active: isChildActive(child) }"
              @click="closeMenu"
              @keydown.esc="closeMenu"
            >
              {{ child.label }}
            </router-link>
            <!-- 아직 화면이 없는 메뉴(to 가 비어 있음): 클릭해도 이동하지 않는다 -->
            <span v-else class="nav-dropdown-item is-disabled" aria-disabled="true">{{ child.label }}</span>
          </li>
        </ul>
      </div>
    </nav>

    <div class="nav-right">
      <!-- TODO(API 연동): 알림/검색은 시안의 아이콘만 먼저 배치. 동작은 API 확정 후 연결 -->
      <button type="button" class="nav-icon-btn" title="알림" aria-label="알림">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
          <path d="M18 8a6 6 0 0 0-12 0c0 7-3 9-3 9h18s-3-2-3-9 M13.73 21a2 2 0 0 1-3.46 0" />
        </svg>
        <span class="badge-dot" aria-hidden="true"></span>
      </button>
      <button type="button" class="nav-icon-btn" title="검색" aria-label="검색">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
          <path d="M11 19a8 8 0 1 0 0-16 8 8 0 0 0 0 16z M21 21l-4.3-4.3" />
        </svg>
      </button>

      <!-- 로그인 상태면 프로필(아바타), 아니면 로그인 버튼 -->
      <ProfileMenu v-if="isLoggedIn" />
      <router-link v-else to="/auth/login" class="nav-action nav-action-primary">로그인</router-link>
    </div>
  </header>
</template>

<script>
import { headerMenu } from '@/config/navigation'
import ProfileMenu from '@/components/layout/ProfileMenu.vue'
import authStore from '@/store/authStore'

export default {
  name: 'AppHeader',
  components: { ProfileMenu },
  data() {
    return {
      menus: headerMenu,
      openIndex: -1,
      closeTimer: null,
    }
  },
  computed: {
    isLoggedIn() {
      return authStore.state.loggedIn
    },
  },
  watch: {
    // 페이지가 바뀌면 열려 있던 드롭다운은 닫는다
    '$route.fullPath'() {
      this.closeMenu()
    },
  },
  beforeUnmount() {
    clearTimeout(this.closeTimer)
  },
  methods: {
    openMenu(index) {
      clearTimeout(this.closeTimer)
      this.openIndex = index
    },
    toggleMenu(index) {
      this.openIndex = this.openIndex === index ? -1 : index
    },
    closeMenu() {
      clearTimeout(this.closeTimer)
      this.openIndex = -1
    },
    // 마우스가 trigger → dropdown 으로 이동하는 짧은 순간에 깜빡이지 않도록 약간 지연
    closeMenuLater() {
      clearTimeout(this.closeTimer)
      this.closeTimer = setTimeout(() => {
        this.openIndex = -1
      }, 120)
    },
    // 포커스가 메뉴 영역 밖으로 나가면 닫는다 (키보드 접근성)
    onFocusOut(event) {
      if (!event.currentTarget.contains(event.relatedTarget)) {
        this.closeMenu()
      }
    },
    isChildActive(child) {
      // names 가 비어 있거나 배열이 아닌 메뉴(미구현 항목)는 활성으로 보지 않는다
      return Array.isArray(child.names) && child.names.includes(this.$route.name)
    },
    isMenuActive(menu) {
      return menu.children.some((child) => this.isChildActive(child))
    },
  },
}
</script>

<style scoped>
.brand {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  flex-shrink: 0;
  width: var(--side-nav-width);
  height: 100%;
  padding: 0 var(--space-4);
  border-right: 1px solid rgba(255, 255, 255, 0.07);
}

.brand-mark {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border-radius: 5px;
  background: var(--color-primary-600);
  color: #fff;
  font-size: 12px;
  font-weight: 700;
}

.brand-name {
  font-size: 15px;
  font-weight: 700;
  letter-spacing: -0.4px;
  color: var(--color-nav-active);
}

/* ---- 상단 메뉴 + 드롭다운 ---- */
.nav-links {
  display: flex;
  align-items: stretch;
  gap: 2px;
  height: 100%;
  padding: 0 12px;
  flex: 1;
}

.nav-item {
  position: relative;
  display: flex;
  align-items: center;
}

.nav-link {
  height: 30px;
  padding: 0 14px;
  border: none;
  border-radius: 5px;
  background: none;
  font-size: var(--font-size-sm);
  font-weight: 500;
  color: var(--color-nav-fg);
  white-space: nowrap;
  cursor: pointer;
  transition: color 0.15s ease, background-color 0.15s ease;
}

.nav-link:hover,
.nav-link.open {
  color: var(--color-nav-active);
  background: rgba(255, 255, 255, 0.06);
}

.nav-link.active {
  color: var(--color-nav-active);
  background: rgba(255, 255, 255, 0.1);
}

.nav-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  z-index: 200;
  min-width: 160px;
  margin: 0;
  padding: var(--space-1);
  list-style: none;
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-md);
}

.nav-dropdown-item {
  display: block;
  padding: 8px 12px;
  border-radius: var(--radius-sm);
  font-size: var(--font-size-sm);
  font-weight: 500;
  color: var(--color-text-soft);
  white-space: nowrap;
}

.nav-dropdown-item:hover {
  background: var(--color-surface-muted);
  color: var(--color-text);
}

.nav-dropdown-item.is-disabled {
  color: var(--color-text-faint);
  cursor: default;
}

.nav-dropdown-item.is-disabled:hover {
  background: none;
  color: var(--color-text-faint);
}

.nav-dropdown-item.active {
  background: var(--color-primary-50);
  color: var(--color-primary-600);
  font-weight: 600;
}

/* ---- 우측 영역 ---- */
.nav-right {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 0 14px;
}

.nav-icon-btn {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  padding: 0;
  border: none;
  border-radius: var(--radius-md);
  background: none;
  color: var(--color-nav-fg);
  cursor: pointer;
  transition: background-color 0.15s ease, color 0.15s ease;
}

.nav-icon-btn:hover {
  background: rgba(255, 255, 255, 0.08);
  color: var(--color-nav-active);
}

.badge-dot {
  position: absolute;
  top: 5px;
  right: 5px;
  width: 7px;
  height: 7px;
  border: 1.5px solid var(--color-nav-bg);
  border-radius: 50%;
  background: var(--color-warning-500);
}

.nav-action {
  display: inline-flex;
  align-items: center;
  height: 30px;
  padding: 0 14px;
  border-radius: var(--radius-md);
  font-size: var(--font-size-sm);
  font-weight: 500;
  color: var(--color-nav-fg);
  transition: background-color 0.15s ease, color 0.15s ease;
}

.nav-action:hover {
  background: rgba(255, 255, 255, 0.08);
  color: var(--color-nav-active);
}

.nav-action-primary {
  background: var(--color-primary-600);
  color: #fff;
  font-weight: 600;
}

.nav-action-primary:hover {
  background: var(--color-primary-500);
  color: #fff;
}

/* 좁은 화면: 브랜드 글자를 숨기고 여백을 줄여 한 줄에 맞춘다 */
@media (max-width: 700px) {
  .brand {
    width: auto;
    padding: 0 var(--space-3);
    border-right: none;
  }

  .brand-name {
    display: none;
  }

  .nav-links {
    padding: 0 var(--space-1);
  }

  .nav-link {
    padding: 0 10px;
  }

  .nav-right {
    padding: 0 var(--space-2);
    gap: 2px;
  }
}
</style>

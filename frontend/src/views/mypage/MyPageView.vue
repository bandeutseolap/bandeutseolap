<template>
  <div class="pms-page">
    <div class="page-header">
      <h1>마이페이지</h1>
      <p>내 정보, 보안, 로그인 세션, 동의 내역을 관리합니다.</p>
    </div>

    <!-- 4개 화면 목록: 항목을 누르면 해당 화면으로 이동 -->
    <ul class="card mypage-list">
      <li v-for="menu in menus" :key="menu.name">
        <router-link :to="{ name: menu.name }" class="mypage-item">
          <span class="mypage-icon" aria-hidden="true">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path :d="menu.icon" />
            </svg>
          </span>
          <span class="mypage-text">
            <span class="mypage-title">{{ menu.title }}</span>
            <span class="mypage-fields">{{ menu.fields }}</span>
          </span>
          <svg class="mypage-chevron" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
            <path d="M9 18l6-6-6-6" />
          </svg>
        </router-link>
      </li>
    </ul>
  </div>
</template>

<script>
import { MYPAGE_MENUS } from '@/utils/myPageMeta'

export default {
  name: 'MyPageView',
  data() {
    return { menus: MYPAGE_MENUS }
  },
}
</script>

<style scoped>
.mypage-list {
  margin: 0;
  padding: 0;
  list-style: none;
}

.mypage-list li + li {
  border-top: 1px solid var(--color-border);
}

.mypage-item {
  display: flex;
  align-items: center;
  gap: var(--space-4);
  padding: var(--space-4) var(--space-5);
  color: var(--color-text);
  transition: background-color 0.12s ease;
}

.mypage-item:hover {
  background: var(--color-surface-muted);
  color: var(--color-text);
}

.mypage-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  width: 38px;
  height: 38px;
  border-radius: var(--radius-md);
  background: var(--color-primary-50);
  color: var(--color-primary-600);
}

.mypage-text {
  display: flex;
  flex: 1;
  flex-direction: column;
  gap: 3px;
  min-width: 0;
}

.mypage-title {
  font-size: var(--font-size-md);
  font-weight: 700;
}

.mypage-fields {
  font-size: var(--font-size-sm);
  line-height: 1.5;
  color: var(--color-text-muted);
}

.mypage-chevron {
  flex-shrink: 0;
  color: var(--color-text-faint);
}
</style>

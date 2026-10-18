<template>
  <!-- 앱 셸: TopNav(고정) + Sidebar + Main(내부 스크롤) — 레이아웃 CSS 는 common.css 의 .app 참고 -->
  <div class="app">
    <AppHeader />
    <AppSidebar />

    <div class="app-main">
      <main class="app-content">
        <div class="page-wrap">
          <router-view />
        </div>
      </main>

      <AppFooter />
    </div>
  </div>
</template>

<script>
import AppHeader from '@/components/layout/AppHeader.vue'
import AppSidebar from '@/components/layout/AppSideNav.vue'
import AppFooter from '@/components/layout/AppFooter.vue'

export default {
  name: 'DefaultLayout',
  components: {
    AppHeader,
    AppSidebar,
    AppFooter
  }
}
</script>

<template>
  <div class="app-shell">
    <AppHeader />

    <div class="layout-desktop">
      <div class="layout-margin-left"></div>

      <AppSidebar />

      <main class="layout-main">
        <div class="page-wrap">
          <router-view />
        </div>
      </main>

      <div class="layout-margin-right"></div>
    </div>

    <AppFooter />
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
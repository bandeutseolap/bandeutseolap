<template>
  <aside class="layout-sidenav">
    <div class="sidenav-panel panel">
      <div class="panel-header">
        <h2 class="section-title sidebar-title">Navigation</h2>
        <p class="section-desc">프로젝트 공통 메뉴</p>
      </div>

      <div class="panel-body">
        <nav class="sidebar-nav">
          <router-link to="/" class="sidebar-link">Dashboard</router-link>
          <router-link to="/boards" class="sidebar-link">Board</router-link>
          <router-link to="/1" class="sidebar-link">메뉴</router-link>
          <router-link to="/2" class="sidebar-link">메뉴</router-link>
          <router-link to="/3" class="sidebar-link">메뉴</router-link>
        </nav>
      </div>
    </div>
  </aside>
</template>

<script>
export default {
  name: 'AppSideNav'
}
</script>

<style scoped>
.sidebar-title {
  font-size: var(--font-size-lg);
}

.sidebar-nav {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.sidebar-link {
  display: flex;
  align-items: center;
  min-height: 40px;
  padding: 0 var(--space-3);
  border-radius: var(--radius-md);
  color: var(--color-text-soft);
  transition: background-color 0.2s ease, color 0.2s ease;
}

.sidebar-link:hover {
  background: var(--color-neutral-100);
  color: var(--color-text);
}

.sidebar-link.router-link-active {
  background: var(--color-primary-50);
  color: var(--color-primary-700);
  font-weight: 600;
}
</style>
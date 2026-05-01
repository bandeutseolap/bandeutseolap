<template>
  <header class="app-topnav">
    <div class="topnav-inner">
      <div class="topnav-left">
        <h1 class="topnav-logo">반듯서랍</h1>
      </div>

      <div class="topnav-right">
        <router-link to="/boards" class="btn btn-sm btn-ghost">TEMP</router-link>
        <router-link to="/auth/login" class="btn btn-sm btn-common">로그인</router-link>
      </div>
    </div>
  </header>
</template>

<script>
export default {
  name: 'AppHeader'
}
</script>

<style scoped>
.topnav-inner {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-4);
}

.topnav-left,
.topnav-right {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.topnav-logo {
  margin: 0;
  font-size: var(--font-size-lg);
  font-weight: 700;
  color: var(--color-text);
}
</style>
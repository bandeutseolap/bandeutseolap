<template>
  <footer class="app-footer">
    <div class="footer-left">
      <span>© 2026 My Project</span>
    </div>

    <div class="footer-right">
      <span class="text-muted">v1.0.0</span>
    </div>
  </footer>
</template>

<script>
export default {
  name: 'AppFooter'
}
</script>

<style scoped>
.footer-left,
.footer-right {
  display: flex;
  align-items: center;
}
</style>
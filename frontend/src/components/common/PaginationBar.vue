<template>
  <nav v-if="totalPages > 1" class="pagination" aria-label="페이지 이동">
    <button type="button" class="pg-btn" :disabled="page <= 1" aria-label="이전 페이지" @click="go(page - 1)">‹</button>
    <button
      v-for="n in pages"
      :key="n"
      type="button"
      class="pg-btn"
      :class="{ active: n === page }"
      :aria-current="n === page ? 'page' : null"
      @click="go(n)"
    >{{ n }}</button>
    <button type="button" class="pg-btn" :disabled="page >= totalPages" aria-label="다음 페이지" @click="go(page + 1)">›</button>
  </nav>
</template>

<script>
// 현재 페이지 앞뒤 2개씩, 최대 5개 번호를 보여준다
const WINDOW = 2

export default {
  name: 'PaginationBar',
  props: {
    total: { type: Number, required: true },
    page: { type: Number, default: 1 },
    size: { type: Number, default: 10 },
  },
  emits: ['update:page'],
  computed: {
    totalPages() {
      return Math.max(1, Math.ceil(this.total / this.size))
    },
    pages() {
      let start = Math.max(1, this.page - WINDOW)
      const end = Math.min(this.totalPages, start + WINDOW * 2)
      start = Math.max(1, end - WINDOW * 2)
      return Array.from({ length: end - start + 1 }, (_, i) => start + i)
    },
  },
  methods: {
    go(n) {
      if (n >= 1 && n <= this.totalPages && n !== this.page) this.$emit('update:page', n)
    },
  },
}
</script>

<style scoped>
.pagination {
  display: flex;
  justify-content: center;
  gap: var(--space-1);
  padding: var(--space-3) var(--space-4);
  border-top: 1px solid var(--color-border);
}

.pg-btn {
  min-width: 30px;
  height: 30px;
  padding: 0 var(--space-2);
  border: 1px solid transparent;
  border-radius: var(--radius-md);
  background: none;
  font-size: var(--font-size-sm);
  font-variant-numeric: tabular-nums;
  color: var(--color-text-soft);
  cursor: pointer;
}

.pg-btn:hover:not(:disabled) { background: var(--color-surface-muted); color: var(--color-text); }
.pg-btn:disabled { opacity: 0.4; cursor: not-allowed; }
.pg-btn.active { border-color: var(--color-primary-600); background: var(--color-primary-50); font-weight: 700; color: var(--color-primary-700); }
</style>

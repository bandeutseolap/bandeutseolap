<template>
  <div class="pms-page">
    <div class="page-header">
      <h1>프로젝트별 사용량</h1>
      <p>관리자 › 스토리지 관리 · 프로젝트별 활성 파일 집계 (쿼터 {{ quotaMb }}MB / 프로젝트)</p>
    </div>

    <section class="card">
      <form class="search-panel" @submit.prevent="search">
        <div class="search-grid">
          <div class="form-row"><label for="sp-name">프로젝트명</label><input id="sp-name" v-model="form.name" class="form-control" /></div>
          <div class="form-row">
            <label for="sp-status">프로젝트 상태</label>
            <select id="sp-status" v-model="form.status" class="form-control"><option value="">전체</option><option v-for="s in statuses" :key="s" :value="s">{{ s }}</option></select>
          </div>
          <div class="form-row">
            <label for="sp-arch">보관 여부</label>
            <select id="sp-arch" v-model="form.archived" class="form-control"><option value="">전체</option><option value="1">보관</option><option value="0">미보관</option></select>
          </div>
          <div class="form-row">
            <label for="sp-pct">쿼터 사용률</label>
            <select id="sp-pct" v-model="form.percentMin" class="form-control"><option value="">전체</option><option value="50">50% 이상</option><option value="80">80% 이상</option><option value="100">100% 이상</option></select>
          </div>
        </div>
        <div class="search-actions">
          <button type="button" class="btn btn-outline" @click="reset">초기화</button>
          <button type="submit" class="btn btn-primary">검색</button>
        </div>
      </form>

      <div class="bulk-bar">
        <span class="bulk-count">{{ selected.length }}건 선택</span>
        <button type="button" class="btn btn-outline btn-sm" :disabled="!selected.length" @click="confirmOpen = true">재집계</button>
        <span class="grow"></span>
        <span class="text-muted bulk-count">전체 {{ rows.length }}건</span>
      </div>

      <div class="table-wrap">
        <table class="table-nowrap">
          <thead>
            <tr>
              <th class="col-check"><input type="checkbox" :checked="rows.length > 0 && selected.length === rows.length" aria-label="전체 선택" @change="selected = $event.target.checked ? rows.map((r) => r.projectId) : []" /></th>
              <th>프로젝트명</th><th>상태</th><th>보관</th><th>활성 파일 수</th><th>활성 용량</th><th>쿼터</th><th class="usage-col">사용률</th><th>최종 집계</th><th class="col-actions">이동</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="rows.length === 0" class="empty-row"><td colspan="10">조건에 맞는 프로젝트가 없습니다.</td></tr>
            <tr v-for="r in rows" :key="r.projectId">
              <td class="col-check"><input v-model="selected" type="checkbox" :value="r.projectId" :aria-label="`${r.name} 선택`" /></td>
              <td><strong>{{ r.name }}</strong></td>
              <td>{{ r.status }}</td>
              <td><span class="chip" :class="r.archived ? 'badge-muted' : 'chip-success'">{{ r.archived ? '보관' : '운영' }}</span></td>
              <td>{{ r.activeFileCount }}건</td>
              <td>{{ formatBytes(r.activeFileTotalSize) }}</td>
              <td>{{ formatBytes(r.quota) }}</td>
              <td class="usage-col">
                <div class="usage">
                  <div class="progress-track" role="progressbar" :aria-valuenow="Math.min(r.percent, 100)" aria-valuemin="0" aria-valuemax="100" :aria-label="`${r.name} 쿼터 사용률`">
                    <div class="progress-fill" :class="r.percent >= 100 ? 'danger' : r.percent >= 80 ? 'warn' : ''" :style="{ width: Math.min(r.percent, 100) + '%' }"></div>
                  </div>
                  <span class="progress-pct">{{ r.percent }}%</span>
                </div>
              </td>
              <td>{{ r.updatedAt }}</td>
              <td class="col-actions"><router-link :to="{ name: 'StorageFiles', query: { projectId: r.projectId } }" class="btn btn-text">파일 목록</router-link></td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>

    <p class="notice-box mt-4">
      쿼터는 현재 전역 변수(<code>PROJECT_QUOTA_MB</code>) 하나뿐입니다. 프로젝트마다 다른 쿼터가 필요하면 <code>dm_project_file_usage</code> 또는 <code>app_project</code>에 <code>quota_size</code> 컬럼을 추가해야 합니다.
    </p>

    <YesNoModal v-model="confirmOpen" title="재집계" :message="`선택한 ${selected.length}개 프로젝트를 다시 집계할까요?`" sub-message="app_file 기준으로 다시 계산해 집계 테이블과의 오차를 보정합니다." @confirm="onRecount" />
    <AppToast :message="toastMessage" />
  </div>
</template>

<script>
import AppToast from '@/components/common/AppToast.vue'
import YesNoModal from '@/components/common/YesNoModal.vue'
import adminStore from '@/store/adminStore'
import adminViewMixin from '@/mixins/adminViewMixin'
import { formatBytes } from '@/utils/adminMeta'

const blank = () => ({ name: '', status: '', archived: '', percentMin: '' })

export default {
  name: 'StorageProjectsView',
  components: { AppToast, YesNoModal },
  mixins: [adminViewMixin],
  data() { return { form: blank(), applied: blank(), selected: [], confirmOpen: false } },
  computed: {
    quotaMb() { return adminStore.state.systemVariables.PROJECT_QUOTA_MB },
    statuses() { return [...new Set(adminStore.state.projects.map((p) => p.status))] },
    rows() { return adminStore.projectUsageRows(this.applied) },
  },
  methods: {
    formatBytes,
    search() { this.applied = { ...this.form }; this.selected = [] },
    reset() { this.form = blank(); this.search() },
    async onRecount() {
      const result = await adminStore.recountProjects(this.selected)
      const fixed = result.filter((r) => r.beforeCount !== r.afterCount || r.beforeSize !== r.afterSize).length
      this.confirmOpen = false
      this.selected = []
      this.showToast(fixed ? `재집계 완료: ${fixed}개 프로젝트의 오차를 보정했습니다.` : '재집계 완료: 보정할 오차가 없습니다.')
    },
  },
}
</script>

<style scoped>
.usage-col { min-width: 190px; }
.usage { display: flex; align-items: center; gap: var(--space-2); }
.usage .progress-track { flex: 1; }
.progress-fill.warn { background: var(--color-warning-500); }
.progress-fill.danger { background: var(--color-error-500); }
</style>

<template>
  <div>
    <div class="card">
      <div class="toolbar">
        <div style="display:flex; align-items:center; gap:10px;">
          <label style="font-size:12.5px; color:var(--color-text-sub); font-weight:600;">대상 프로젝트</label>
          <select class="select-inline" v-model="selectedProjectId" style="min-width:220px;">
            <option v-for="p in projects" :key="p.id" :value="p.id">{{ p.name }}</option>
          </select>
        </div>
        <!-- 모달 오픈 -> 페이지 이동으로 변경. 선택된 프로젝트를 라우트 params로 전달 -->
        <router-link
          v-if="selectedProjectId"
          :to="{ name: 'PhaseCreate', params: { projectId: selectedProjectId } }"
          class="btn btn-primary"
        >
          + 수행단계 등록
        </router-link>
        <button v-else class="btn btn-primary" disabled>+ 수행단계 등록</button>
      </div>

      <table>
        <thead>
          <tr>
            <th style="width:56px;">순번</th>
            <th>단계명</th>
            <th>기간</th>
            <th>상태</th>
            <th class="col-actions">관리</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="!selectedProjectId || currentPhases.length === 0" class="empty-row">
            <td colspan="5">등록된 수행단계가 없습니다.</td>
          </tr>
          <tr v-for="ph in currentPhases" :key="ph.id">
            <td><span class="drag-handle">⠿</span>{{ ph.order }}</td>
            <td><strong>{{ ph.name }}</strong></td>
            <td>{{ ph.startDate || '-' }} ~ {{ ph.endDate || '-' }}</td>
            <td><span class="badge" :class="statusBadgeClass(ph.status)">{{ statusLabel(ph.status) }}</span></td>
            <td class="col-actions">
              <router-link
                :to="{ name: 'PhaseEdit', params: { projectId: selectedProjectId, id: ph.id } }"
                class="btn btn-text"
              >
                수정
              </router-link>
              <button class="btn btn-danger-text" @click="requestDelete(ph)">삭제</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 삭제는 목록 컨텍스트를 벗어날 필요가 없어 기존처럼 확인 모달 유지 -->
    <ProjectYesNoModal
      v-if="deleteTarget"
      title="수행단계 삭제"
      message="아래 수행단계를 삭제하시겠습니까?"
      :highlight="deleteTarget.name"
      @confirm="confirmDelete"
      @cancel="deleteTarget = null"
    />
  </div>
</template>

<script>
import ProjectYesNoModal from '@/components/common/ProjectYesNoModal.vue';

export default {
  name: 'PhaseListView',
  components: { ProjectYesNoModal },
  props: {
    projects: { type: Array, required: true },
    // { [projectId]: Phase[] } 형태. 부모(views/ProjectManageView.vue)가 phaseStore에서 내려줌.
    phasesByProject: { type: Object, required: true },
  },
  emits: ['delete'],
  data() {
    return {
      // 최초 진입 시 첫 번째 프로젝트를 기본 선택
      selectedProjectId: this.projects[0] ? this.projects[0].id : null,
      deleteTarget: null,
    };
  },
  computed: {
    currentPhases() {
      if (this.selectedProjectId === null) return [];
      return (this.phasesByProject[this.selectedProjectId] || [])
        .slice()
        .sort((a, b) => a.order - b.order);
    },
  },
  methods: {
    statusBadgeClass(status) {
      return { READY: 'badge-muted', IN_PROGRESS: 'badge-primary', DONE: 'badge-success' }[status] || 'badge-muted';
    },
    statusLabel(status) {
      return { READY: '대기', IN_PROGRESS: '진행중', DONE: '완료' }[status] || status;
    },
    requestDelete(phase) {
      this.deleteTarget = phase;
    },
    confirmDelete() {
      this.$emit('delete', { projectId: this.selectedProjectId, phaseId: this.deleteTarget.id });
      this.deleteTarget = null;
    },
  },
};
</script>

<style scoped>
/* router-link를 button처럼 보이게 하기 위한 보정 (a 태그 기본 스타일 제거) */
.btn.btn-primary,
.btn.btn-text {
  text-decoration: none;
}
</style>

<template>
  <div class="pms-page">
    <div class="page-header">
      <div>
        <h1>프로젝트 관리</h1>
        <p>프로젝트와 수행단계를 등록/수정/삭제합니다.</p>
      </div>
    </div>

    <div class="tabs">
      <button class="tab-btn" :class="{ active: activeTab === 'project' }" @click="activeTab = 'project'">
        프로젝트
      </button>
      <button class="tab-btn" :class="{ active: activeTab === 'phase' }" @click="activeTab = 'phase'">
        수행단계
      </button>
    </div>

    <ProjectListView
      v-if="activeTab === 'project'"
      :projects="projects"
      @delete="handleProjectDelete"
    />

    <PhaseListView
      v-else
      :projects="projects"
      :phases-by-project="phasesByProject"
      @delete="handlePhaseDelete"
    />

    <div v-if="toastMessage" class="toast">{{ toastMessage }}</div>
  </div>
</template>

<script>
import ProjectListView from '@/components/project/ProjectListView.vue';
import PhaseListView from '@/components/phase/PhaseListView.vue';
import projectStore from '@/store/projectStore';
import phaseStore from '@/store/phaseStore';

export default {
  name: 'ProjectManageView',
  components: { ProjectListView, PhaseListView },
  data() {
    return {
      // 수행단계 등록/수정 페이지에서 돌아올 때 ?tab=phase 로 복귀시키므로
      // 쿼리스트링을 초기값으로 반영 (없으면 기본 'project')
      activeTab: this.$route.query.tab === 'phase' ? 'phase' : 'project',
      toastMessage: '',
    };
  },
  computed: {
    // 프로젝트/수행단계 등록·수정이 별도 페이지로 분리되면서
    // 페이지 이동 후에도 최신 상태를 반영하도록 store를 단일 소스로 사용.
    projects() {
      return projectStore.state.projects;
    },
    phasesByProject() {
      return phaseStore.state.phasesByProject;
    },
  },
  methods: {
    showToast(msg) {
      this.toastMessage = msg;
      setTimeout(() => { this.toastMessage = ''; }, 2000);
    },
    async handleProjectDelete(id) {
      await projectStore.remove(id);
      // 프로젝트 삭제 시 하위 수행단계도 함께 정리 (cascade)
      delete phaseStore.state.phasesByProject[id];
      this.showToast('프로젝트가 삭제되었습니다.');
    },
    async handlePhaseDelete({ projectId, phaseId }) {
      await phaseStore.remove(projectId, phaseId);
      this.showToast('수행단계가 삭제되었습니다.');
    },
  },
};
</script>

<style scoped>
.pms-page {
  padding: 28px 32px;
  max-width: 1180px;
}
.page-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 18px;
}
.page-header h1 { font-size: 19px; margin: 0 0 4px; }
.page-header p { margin: 0; color: var(--color-text-sub); font-size: 12.5px; }
</style>

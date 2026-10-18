<template>
  <!-- 프로젝트 관리 > 단계 설정 : 프로젝트별 수행단계 등록/수정/삭제
       (기존 '프로젝트 관리' 화면의 '수행단계' 탭을 독립 화면으로 분리) -->
  <div class="pms-page">
    <div class="page-header">
      <div>
        <h1>단계 설정</h1>
        <p>프로젝트별 수행단계를 등록/수정/삭제합니다.</p>
      </div>
    </div>

    <PhaseListView
      :projects="projects"
      :phases-by-project="phasesByProject"
      @delete="handlePhaseDelete"
    />

    <div v-if="toastMessage" class="toast">{{ toastMessage }}</div>
  </div>
</template>

<script>
import PhaseListView from '@/components/phase/PhaseListView.vue';
import projectStore from '@/store/projectStore';
import phaseStore from '@/store/phaseStore';

export default {
  name: 'PhaseSettingView',
  components: { PhaseListView },
  data() {
    return {
      toastMessage: '',
    };
  },
  computed: {
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
    async handlePhaseDelete({ projectId, phaseId }) {
      await phaseStore.remove(projectId, phaseId);
      this.showToast('수행단계가 삭제되었습니다.');
    },
  },
};
</script>

<style scoped>
.pms-page {
  max-width: 1180px;
}
.page-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: var(--space-4);
}
</style>

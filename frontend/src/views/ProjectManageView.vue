<template>
  <!-- 프로젝트 관리 > 관리 페이지 : 프로젝트 등록/수정/삭제
       (수행단계는 '단계 설정' 화면(PhaseSettingView)으로 분리) -->
  <div class="pms-page">
    <div class="page-header">
      <div>
        <h1>프로젝트 설정</h1>
        <p>프로젝트를 등록/수정/삭제합니다.</p>
      </div>
    </div>

    <ProjectListView
      :projects="projects"
      @delete="handleProjectDelete"
    />

    <div v-if="toastMessage" class="toast">{{ toastMessage }}</div>
  </div>
</template>

<script>
import ProjectListView from '@/components/project/ProjectListView.vue';
import projectStore from '@/store/projectStore';
import phaseStore from '@/store/phaseStore';

export default {
  name: 'ProjectManageView',
  components: { ProjectListView },
  data() {
    return {
      toastMessage: '',
    };
  },
  computed: {
    // 프로젝트 등록·수정이 별도 페이지로 분리되면서
    // 페이지 이동 후에도 최신 상태를 반영하도록 store를 단일 소스로 사용.
    projects() {
      return projectStore.state.projects;
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

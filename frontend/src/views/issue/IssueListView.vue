<template>
  <div class="pms-page">
    <div class="page-header">
      <h1>리스크 · 이슈</h1>
      <p>{{ currentProject.name }} · 이슈 및 리스크 트래커</p>
    </div>

    <section class="card">
      <div class="card-head">
        <span class="card-title">이슈 목록</span>
        <router-link to="/issues/new" class="btn btn-primary btn-sm">이슈 등록</router-link>
      </div>
      <IssueTable :issues="issues" />
    </section>

    <AppToast :message="toastMessage" />
  </div>
</template>

<script>
import IssueTable from '@/components/work/IssueTable.vue'
import AppToast from '@/components/common/AppToast.vue'
import sampleStore from '@/store/sampleStore'
import sampleViewMixin from '@/mixins/sampleViewMixin'

export default {
  name: 'IssueListView',
  components: { IssueTable, AppToast },
  mixins: [sampleViewMixin],
  computed: {
    issues() {
      return sampleStore.state.issues
    },
  },
  created() {
    this.showFlash()
  },
}
</script>

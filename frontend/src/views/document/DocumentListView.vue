<template>
  <div class="pms-page">
    <div class="page-header">
      <h1>문서 목록</h1>
      <p>{{ currentProject.name }} · 작성된 문서 {{ documents.length }}건</p>
    </div>

    <section class="card">
      <div class="card-head">
        <span class="card-title">문서 목록</span>
        <div class="card-actions">
          <router-link to="/documents/templates" class="btn btn-ghost btn-sm">템플릿 목록</router-link>
          <router-link to="/documents/new" class="btn btn-primary btn-sm">문서 작성</router-link>
        </div>
      </div>

      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>문서 제목</th>
              <th>템플릿</th>
              <th>작성자</th>
              <th>작성일</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="documents.length === 0" class="empty-row">
              <td colspan="4">작성된 문서가 없습니다.</td>
            </tr>
            <tr v-for="doc in documents" :key="doc.id">
              <td><strong>{{ doc.title }}</strong></td>
              <td>
                <span v-if="doc.templateName" class="chip badge-muted">{{ doc.templateName }}</span>
                <span v-else class="text-muted">-</span>
              </td>
              <td>{{ doc.author }}</td>
              <td>{{ doc.createdAt }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>

    <AppToast :message="toastMessage" />
  </div>
</template>

<script>
import AppToast from '@/components/common/AppToast.vue'
import sampleStore from '@/store/sampleStore'
import sampleViewMixin from '@/mixins/sampleViewMixin'

export default {
  name: 'DocumentListView',
  components: { AppToast },
  mixins: [sampleViewMixin],
  computed: {
    documents() {
      return sampleStore.state.documents
    },
  },
  created() {
    this.showFlash()
  },
}
</script>

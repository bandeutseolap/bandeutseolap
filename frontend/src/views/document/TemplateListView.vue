<template>
  <div class="pms-page">
    <div class="page-header">
      <h1>템플릿 목록</h1>
      <p>{{ currentProject.name }} · 등록된 템플릿 {{ templates.length }}건</p>
    </div>

    <section class="card">
      <div class="card-head">
        <span class="card-title">템플릿 목록</span>
        <div class="card-actions">
          <router-link to="/documents" class="btn btn-ghost btn-sm">문서 목록</router-link>
          <router-link to="/documents/templates/new" class="btn btn-primary btn-sm">템플릿 등록</router-link>
        </div>
      </div>

      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>템플릿명</th>
              <th>설명</th>
              <th>공개 범위</th>
              <th>등록자</th>
              <th class="col-actions">관리</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="templates.length === 0" class="empty-row">
              <td colspan="5">등록된 템플릿이 없습니다.</td>
            </tr>
            <tr v-for="tpl in templates" :key="tpl.id">
              <td><strong>{{ tpl.name }}</strong></td>
              <td class="text-soft">{{ tpl.description || '-' }}</td>
              <td><span class="chip badge-muted">{{ tpl.scope }}</span></td>
              <td>{{ tpl.owner }}</td>
              <td class="col-actions">
                <!-- 이 템플릿으로 바로 문서 작성 (DocumentFormView 가 ?templateId 를 읽어 미리 선택) -->
                <router-link
                  :to="{ name: 'DocumentCreate', query: { templateId: tpl.id } }"
                  class="btn btn-sm btn-outline"
                >
                  문서 작성
                </router-link>
              </td>
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
  name: 'TemplateListView',
  components: { AppToast },
  mixins: [sampleViewMixin],
  computed: {
    templates() {
      return sampleStore.state.templates
    },
  },
  created() {
    this.showFlash()
  },
}
</script>

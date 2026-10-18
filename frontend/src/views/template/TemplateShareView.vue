<template>
  <div class="pms-page">
    <div class="page-header">
      <h1>템플릿 공유</h1>
      <p>프로젝트 간에 문서 템플릿을 공유합니다.</p>
    </div>

    <section class="card">
      <div class="card-head">
        <span class="card-title">템플릿 목록</span>
        <router-link to="/documents/templates/new" class="btn btn-ghost btn-sm">템플릿 등록</router-link>
      </div>

      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>템플릿명</th>
              <th>설명</th>
              <th>등록자</th>
              <th>공개 범위</th>
              <th>공유 상태</th>
              <th class="col-actions">관리</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="templates.length === 0" class="empty-row">
              <td colspan="6">등록된 템플릿이 없습니다.</td>
            </tr>
            <tr v-for="tpl in templates" :key="tpl.id">
              <td><strong>{{ tpl.name }}</strong></td>
              <td class="text-soft">{{ tpl.description || '-' }}</td>
              <td>{{ tpl.owner }}</td>
              <td><span class="chip badge-muted">{{ tpl.scope }}</span></td>
              <td>
                <span class="chip" :class="tpl.shared ? 'chip-success' : 'badge-muted'">
                  {{ tpl.shared ? '공유중' : '비공유' }}
                </span>
              </td>
              <td class="col-actions">
                <button
                  type="button"
                  class="btn btn-sm"
                  :class="tpl.shared ? 'btn-outline' : 'btn-primary'"
                  @click="toggleShare(tpl)"
                >
                  {{ tpl.shared ? '공유 해제' : '공유' }}
                </button>
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
  name: 'TemplateShareView',
  components: { AppToast },
  mixins: [sampleViewMixin],
  computed: {
    templates() {
      return sampleStore.state.templates
    },
  },
  methods: {
    toggleShare(tpl) {
      const next = !tpl.shared
      // TODO(API 연동): await axios.put(`/api/templates/${tpl.id}/share`, { shared: next })
      sampleStore.setTemplateShared(tpl.id, next)
      this.showToast(next ? '템플릿이 공유되었습니다.' : '템플릿 공유가 해제되었습니다.')
    },
  },
}
</script>

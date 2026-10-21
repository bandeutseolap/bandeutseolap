<template>
  <div class="pms-page">
    <div v-if="!user" class="card"><div class="empty-state">사용자를 찾을 수 없습니다.</div></div>

    <template v-else>
      <div class="page-header head-row">
        <div>
          <h1>{{ user.loginId }} <span class="chip" :class="effective[user.effective].chip">{{ effective[user.effective].label }}</span></h1>
          <p>{{ user.userName }} · 관리자 › 사용자/권한 관리 › 사용자 상세</p>
        </div>
        <router-link :to="{ name: 'AdminUserList' }" class="btn btn-outline">← 목록으로</router-link>
      </div>

      <div class="tabs" role="tablist">
        <button
          v-for="t in tabs" :key="t.key" type="button" role="tab" class="tab-btn"
          :class="{ active: activeTab === t.key }" :aria-selected="activeTab === t.key" @click="activeTab = t.key"
        >{{ t.label }}</button>
      </div>

      <component :is="currentTab.component" :key="activeTab" :user="user" @toast="showToast" @error="fail" />
    </template>

    <AppToast :message="toastMessage" />
  </div>
</template>

<script>
import AppToast from '@/components/common/AppToast.vue'
import UserBasicTab from '@/components/admin/user/UserBasicTab.vue'
import UserExtraTab from '@/components/admin/user/UserExtraTab.vue'
import UserSecurityTab from '@/components/admin/user/UserSecurityTab.vue'
import UserConsentTab from '@/components/admin/user/UserConsentTab.vue'
import UserRoleTab from '@/components/admin/user/UserRoleTab.vue'
import UserProjectTab from '@/components/admin/user/UserProjectTab.vue'
import UserSessionTab from '@/components/admin/user/UserSessionTab.vue'
import UserHistoryTab from '@/components/admin/user/UserHistoryTab.vue'
import adminStore from '@/store/adminStore'
import adminViewMixin from '@/mixins/adminViewMixin'
import { EFFECTIVE_STATUS } from '@/utils/adminMeta'

const TABS = [
  { key: 'basic', label: '기본정보', component: 'UserBasicTab' },
  { key: 'extra', label: '부가정보', component: 'UserExtraTab' },
  { key: 'security', label: '보안', component: 'UserSecurityTab' },
  { key: 'consent', label: '동의', component: 'UserConsentTab' },
  { key: 'roles', label: '시스템 역할', component: 'UserRoleTab' },
  { key: 'projects', label: '참여 프로젝트', component: 'UserProjectTab' },
  { key: 'sessions', label: '세션', component: 'UserSessionTab' },
  { key: 'history', label: '변경이력', component: 'UserHistoryTab' },
]

export default {
  name: 'UserDetailView',
  components: { AppToast, UserBasicTab, UserExtraTab, UserSecurityTab, UserConsentTab, UserRoleTab, UserProjectTab, UserSessionTab, UserHistoryTab },
  mixins: [adminViewMixin],
  data() {
    return { tabs: TABS, effective: EFFECTIVE_STATUS, activeTab: TABS.some((t) => t.key === this.$route.query.tab) ? this.$route.query.tab : 'basic' }
  },
  computed: {
    user() { return adminStore.getUser(this.$route.params.userId) },
    currentTab() { return this.tabs.find((t) => t.key === this.activeTab) },
  },
  created() {
    // 사용자 등록 직후 넘어온 경우 안내 후 쿼리를 정리한다
    if (this.$route.query.created) {
      this.showToast('사용자를 등록했습니다. 초기 비밀번호 설정 링크를 이메일로 발송했습니다.', 4000)
      this.$router.replace({ params: this.$route.params })
    }
  },
}
</script>

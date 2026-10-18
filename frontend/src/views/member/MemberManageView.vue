<template>
  <div class="pms-page">
    <div class="page-header">
      <h1>멤버 관리</h1>
      <p>{{ currentProject.name }} · 권한 및 멤버 관리</p>
    </div>

    <section class="card">
      <div class="card-head">
        <span class="card-title">멤버 목록</span>
        <button type="button" class="btn btn-primary btn-sm" @click="showInvite = !showInvite">
          {{ showInvite ? '닫기' : '멤버 초대' }}
        </button>
      </div>

      <!-- 초대 폼 -->
      <div v-if="showInvite" class="invite-box">
        <div class="invite-fields">
          <div class="form-row">
            <label for="invite-id">아이디<span class="req">*</span></label>
            <input id="invite-id" v-model="invite.loginId" class="form-control" placeholder="초대할 멤버의 아이디" maxlength="50" @keyup.enter="onInvite" />
            <p v-if="inviteError" class="field-error">{{ inviteError }}</p>
          </div>
          <div class="form-row">
            <label for="invite-role">역할</label>
            <select id="invite-role" v-model="invite.role" class="form-control">
              <option v-for="role in roles" :key="role" :value="role">{{ role }}</option>
            </select>
          </div>
          <button type="button" class="btn btn-primary" @click="onInvite">초대</button>
        </div>
      </div>

      <ul class="member-list">
        <MemberRow v-for="member in members" :key="member.id" :member="member" />
      </ul>
    </section>

    <AppToast :message="toastMessage" />
  </div>
</template>

<script>
import MemberRow from '@/components/work/MemberRow.vue'
import AppToast from '@/components/common/AppToast.vue'
import sampleStore from '@/store/sampleStore'
import sampleViewMixin from '@/mixins/sampleViewMixin'
import { MEMBER_ROLES } from '@/utils/workMeta'

export default {
  name: 'MemberManageView',
  components: { MemberRow, AppToast },
  mixins: [sampleViewMixin],
  data() {
    return {
      roles: MEMBER_ROLES,
      showInvite: false,
      invite: { loginId: '', role: 'DEV' },
      inviteError: '',
    }
  },
  computed: {
    members() {
      return sampleStore.state.members
    },
  },
  methods: {
    async onInvite() {
      const loginId = this.invite.loginId.trim()
      if (!loginId) {
        this.inviteError = '초대할 아이디를 입력해주세요.'
        return
      }
      if (this.members.some((m) => m.loginId === loginId)) {
        this.inviteError = '이미 등록된 멤버입니다.'
        return
      }
      this.inviteError = ''
      await sampleStore.inviteMember({ loginId, role: this.invite.role })
      this.invite = { loginId: '', role: 'DEV' }
      this.showInvite = false
      this.showToast('멤버가 초대되었습니다.')
    },
  },
}
</script>

<style scoped>
.invite-box {
  padding: var(--space-4);
  background: var(--color-surface-muted);
  border-bottom: 1px solid var(--color-border);
}

.invite-fields {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 160px auto;
  gap: var(--space-3);
  align-items: end;
}

.invite-fields .form-row {
  margin-bottom: 0;
}

@media (max-width: 700px) {
  .invite-fields { grid-template-columns: minmax(0, 1fr); }
}
</style>

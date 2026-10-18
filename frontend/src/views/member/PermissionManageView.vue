<template>
  <div class="pms-page">
    <div class="page-header">
      <h1>권한 관리</h1>
      <p>{{ currentProject.name }} · 멤버별 역할을 지정합니다.</p>
    </div>

    <div class="two-col">
      <section class="card">
        <div class="card-head">
          <span class="card-title">멤버별 역할</span>
          <button type="button" class="btn btn-primary btn-sm" :disabled="!isDirty" @click="onSave">저장</button>
        </div>

        <p v-if="error" class="error-line">{{ error }}</p>

        <ul class="member-list">
          <MemberRow v-for="member in members" :key="member.id" :member="member">
            <select
              v-model="draft[member.id]"
              class="select-inline role-select"
              :aria-label="`${member.loginId} 역할`"
            >
              <option v-for="role in roles" :key="role" :value="role">{{ role }}</option>
            </select>
          </MemberRow>
        </ul>
      </section>

      <section class="card">
        <div class="card-head">
          <span class="card-title">역할별 권한</span>
        </div>
        <dl class="role-desc">
          <div v-for="item in roleDescriptions" :key="item.role">
            <dt><span class="chip" :class="item.chip">{{ item.role }}</span></dt>
            <dd>{{ item.desc }}</dd>
          </div>
        </dl>
      </section>
    </div>

    <AppToast :message="toastMessage" />
  </div>
</template>

<script>
import MemberRow from '@/components/work/MemberRow.vue'
import AppToast from '@/components/common/AppToast.vue'
import sampleStore from '@/store/sampleStore'
import sampleViewMixin from '@/mixins/sampleViewMixin'
import { MEMBER_ROLES, ROLE_CHIP } from '@/utils/workMeta'

export default {
  name: 'PermissionManageView',
  components: { MemberRow, AppToast },
  mixins: [sampleViewMixin],
  data() {
    return {
      roles: MEMBER_ROLES,
      // 저장 전까지는 store 를 건드리지 않도록 편집용 사본(draft)을 둔다: { [memberId]: role }
      draft: Object.fromEntries(sampleStore.state.members.map((m) => [m.id, m.role])),
      error: '',
      roleDescriptions: [
        { role: 'PM', chip: ROLE_CHIP.PM, desc: '프로젝트·멤버·단계 관리, 모든 업무/문서/이슈 열람 및 수정' },
        { role: 'DEV', chip: ROLE_CHIP.DEV, desc: '업무·문서·이슈 등록 및 본인 담당 건 수정' },
        { role: '멤버', chip: ROLE_CHIP['멤버'], desc: '업무·문서·이슈 열람 (수정 불가)' },
      ],
    }
  },
  computed: {
    members() {
      return sampleStore.state.members
    },
    isDirty() {
      return this.members.some((m) => this.draft[m.id] !== m.role)
    },
  },
  methods: {
    async onSave() {
      // PM 이 한 명도 없으면 프로젝트를 관리할 수 없으므로 막는다
      const pmCount = this.members.filter((m) => this.draft[m.id] === 'PM').length
      if (pmCount === 0) {
        this.error = 'PM 역할을 가진 멤버가 최소 1명 필요합니다.'
        return
      }
      this.error = ''
      await sampleStore.setMemberRoles(this.draft)
      this.showToast('권한이 저장되었습니다.')
    },
  },
}
</script>

<style scoped>
.error-line {
  margin: 0;
  padding: 10px var(--space-4);
  background: var(--color-error-50);
  border-bottom: 1px solid var(--color-error-200);
  font-size: var(--font-size-sm);
  color: var(--color-error-700);
}

.role-select {
  min-width: 96px;
}

.role-desc {
  margin: 0;
  padding: var(--space-2) var(--space-4);
}

.role-desc > div {
  padding: 12px 0;
  border-bottom: 1px solid var(--color-border);
}
.role-desc > div:last-child { border-bottom: none; }

.role-desc dt { margin-bottom: 6px; }
.role-desc dd {
  margin: 0;
  font-size: var(--font-size-sm);
  line-height: 1.6;
  color: var(--color-text-soft);
}
</style>

<template>
  <div class="pms-page">
    <div class="page-header head-row">
      <div>
        <h1>로그인 세션 관리</h1>
        <p>로그인된 세션(Redis)을 확인하고 개별 또는 전체 로그아웃합니다.</p>
      </div>
      <router-link :to="{ name: 'MyPage' }" class="btn btn-outline">← 마이페이지</router-link>
    </div>

    <section class="card">
      <div class="card-head">
        <span class="card-title">세션 목록 <span class="count-text">{{ sessions.length }}건</span></span>
        <button type="button" class="btn btn-outline-error btn-sm" :disabled="sessions.length === 0" @click="askRevokeAll">
          전체 로그아웃
        </button>
      </div>

      <div v-if="loading" class="empty-state">세션을 불러오는 중입니다.</div>

      <div v-else class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>로그인 유형</th>
              <th>접속 IP</th>
              <th>마지막 접근</th>
              <th>상태</th>
              <th class="col-actions">관리</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="sessions.length === 0" class="empty-row">
              <td colspan="5">로그인된 세션이 없습니다.</td>
            </tr>
            <tr v-for="session in sessions" :key="session.id">
              <td>
                {{ session.loginType }}
                <span v-if="session.current" class="chip chip-common current-chip">현재 세션</span>
              </td>
              <td>{{ session.ip }}</td>
              <td>{{ session.lastAccessAt }}</td>
              <td><span class="chip" :class="statusMeta(session).chip">{{ statusMeta(session).label }}</span></td>
              <td class="col-actions">
                <button type="button" class="btn btn-danger-text" @click="askRevoke(session)">로그아웃</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>

    <YesNoModal
      v-model="confirmOpen"
      :title="confirmTitle"
      :message="confirmMessage"
      :sub-message="confirmSub"
      :loading="processing"
      @confirm="onConfirm"
    />

    <AppToast :message="toastMessage" />
  </div>
</template>

<script>
import YesNoModal from '@/components/common/YesNoModal.vue'
import AppToast from '@/components/common/AppToast.vue'
import sampleViewMixin from '@/mixins/sampleViewMixin'
import authStore from '@/store/authStore'
import { fetchSessions, revokeSession, revokeAllSessions } from '@/services/myPageService'
import { SESSION_STATUS } from '@/utils/myPageMeta'

export default {
  name: 'MySessionsView',
  components: { YesNoModal, AppToast },
  mixins: [sampleViewMixin],
  data() {
    return {
      loading: true,
      sessions: [],
      // 확인 모달: target 이 null 이면 '전체 로그아웃', 세션 객체면 '개별 로그아웃'
      confirmOpen: false,
      target: null,
      processing: false,
    }
  },
  computed: {
    isAll() {
      return this.target === null
    },
    // 현재 세션을 끊으면 이 브라우저도 로그아웃되므로 문구로 미리 알린다
    endsCurrent() {
      return this.isAll || (this.target && this.target.current)
    },
    confirmTitle() {
      return this.isAll ? '전체 로그아웃' : '세션 로그아웃'
    },
    confirmMessage() {
      return this.isAll ? '모든 세션을 로그아웃할까요?' : '이 세션을 로그아웃할까요?'
    },
    confirmSub() {
      if (this.endsCurrent) return '현재 사용 중인 세션이 포함되어 있어, 로그아웃 후 로그인 화면으로 이동합니다.'
      return '해당 기기는 다시 로그인해야 합니다.'
    },
  },
  created() {
    this.load()
  },
  methods: {
    statusMeta(session) {
      return SESSION_STATUS[session.status] || SESSION_STATUS.EXPIRED
    },
    async load() {
      this.loading = true
      try {
        this.sessions = await fetchSessions()
      } catch (err) {
        // TODO: 공통 에러 처리(토스트/알림) 컨벤션에 맞춰 연결
        console.error('세션 목록 조회 실패', err)
      } finally {
        this.loading = false
      }
    },
    askRevoke(session) {
      this.target = session
      this.confirmOpen = true
    },
    askRevokeAll() {
      this.target = null
      this.confirmOpen = true
    },
    async onConfirm() {
      if (this.processing) return
      this.processing = true
      const endsCurrent = this.endsCurrent
      try {
        if (this.isAll) await revokeAllSessions()
        else await revokeSession(this.target.id)
      } catch (err) {
        // TODO: 공통 에러 처리(토스트/알림) 컨벤션에 맞춰 연결
        console.error('세션 로그아웃 실패', err)
        this.processing = false
        this.confirmOpen = false
        return
      }
      this.processing = false
      this.confirmOpen = false

      if (endsCurrent) {
        // 서버 세션이 사라졌으므로 이 브라우저의 토큰도 정리하고 로그인 화면으로 보낸다
        authStore.logout()
        this.$router.push({ name: 'AuthLogin' })
        return
      }
      this.showToast('세션이 로그아웃되었습니다.')
      await this.load()
    },
  },
}
</script>

<style scoped>
.count-text {
  margin-left: var(--space-1);
  font-weight: 500;
  color: var(--color-text-muted);
}

.current-chip {
  margin-left: var(--space-2);
}
</style>

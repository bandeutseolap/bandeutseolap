<template>
  <div class="pms-page">
    <div class="page-header head-row">
      <div>
        <h1>동의 관리</h1>
        <p>개인정보 수집 등 동의 여부를 확인하고 철회할 수 있습니다.</p>
      </div>
      <router-link :to="{ name: 'MyPage' }" class="btn btn-outline">← 마이페이지</router-link>
    </div>

    <section class="card">
      <div class="card-head"><span class="card-title">동의 내역</span></div>

      <div v-if="loading" class="empty-state">동의 내역을 불러오는 중입니다.</div>

      <div v-else class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>동의 항목</th>
              <th>구분</th>
              <th>동의 여부</th>
              <th>동의일시</th>
              <th>철회일시</th>
              <th class="col-actions">철회</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="consents.length === 0" class="empty-row">
              <td colspan="6">동의 내역이 없습니다.</td>
            </tr>
            <tr v-for="consent in consents" :key="consent.id">
              <td><strong>{{ consent.title }}</strong></td>
              <td>
                <span class="chip" :class="consent.required ? 'chip-warn' : 'badge-muted'">{{ consent.required ? '필수' : '선택' }}</span>
              </td>
              <td>
                <span class="chip" :class="consent.agreed ? 'chip-success' : 'badge-muted'">{{ consent.agreed ? '동의' : '철회됨' }}</span>
              </td>
              <td>{{ consent.agreedAt || '-' }}</td>
              <td>{{ consent.withdrawnAt || '-' }}</td>
              <td class="col-actions">
                <button v-if="consent.agreed" type="button" class="btn btn-danger-text" @click="askWithdraw(consent)">철회</button>
                <button v-else type="button" class="btn btn-text" @click="onAgree(consent)">다시 동의</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>

    <YesNoModal
      v-model="confirmOpen"
      title="동의 철회"
      :message="`'${target ? target.title : ''}'을(를) 철회할까요?`"
      :sub-message="target && target.required ? '필수 항목을 철회하면 일부 서비스 이용이 제한될 수 있습니다.' : '철회 후에도 언제든 다시 동의할 수 있습니다.'"
      :loading="processing"
      @confirm="onWithdraw"
    />

    <AppToast :message="toastMessage" />
  </div>
</template>

<script>
import YesNoModal from '@/components/common/YesNoModal.vue'
import AppToast from '@/components/common/AppToast.vue'
import sampleViewMixin from '@/mixins/sampleViewMixin'
import { fetchConsents, withdrawConsent, agreeConsent } from '@/services/myPageService'

export default {
  name: 'MyConsentsView',
  components: { YesNoModal, AppToast },
  mixins: [sampleViewMixin],
  data() {
    return {
      loading: true,
      consents: [],
      confirmOpen: false,
      target: null,
      processing: false,
    }
  },
  created() {
    this.load()
  },
  methods: {
    async load() {
      try {
        this.consents = await fetchConsents()
      } catch (err) {
        // TODO: 공통 에러 처리(토스트/알림) 컨벤션에 맞춰 연결
        console.error('동의 내역 조회 실패', err)
      } finally {
        this.loading = false
      }
    },
    askWithdraw(consent) {
      this.target = consent
      this.confirmOpen = true
    },
    async onWithdraw() {
      if (this.processing || !this.target) return
      this.processing = true
      try {
        await withdrawConsent(this.target.id)
        this.showToast('동의가 철회되었습니다.')
        await this.load()
      } catch (err) {
        // TODO: 공통 에러 처리(토스트/알림) 컨벤션에 맞춰 연결
        console.error('동의 철회 실패', err)
      } finally {
        this.processing = false
        this.confirmOpen = false
      }
    },
    async onAgree(consent) {
      try {
        await agreeConsent(consent.id)
        this.showToast('동의 처리되었습니다.')
        await this.load()
      } catch (err) {
        // TODO: 공통 에러 처리(토스트/알림) 컨벤션에 맞춰 연결
        console.error('동의 처리 실패', err)
      }
    },
  },
}
</script>

<template>
  <div class="pms-page">
    <div class="page-header head-row">
      <div>
        <h1>버전 이력</h1>
        <p>{{ board ? board.title : '' }} · 관리자 전용</p>
      </div>
      <router-link v-if="board" :to="{ name: 'SystemBoardDetail', params: { boardId }, query: { area: areaKey } }" class="btn btn-outline">← 게시글로</router-link>
    </div>

    <div v-if="!isAdmin || !board" class="card"><div class="empty-state">{{ !board ? '게시글을 찾을 수 없습니다.' : '관리자만 버전 이력을 볼 수 있습니다.' }}</div></div>

    <div v-else class="stack">
      <section class="card">
        <div class="card-head">
          <span class="card-title">버전 목록 <span class="text-muted">현재 v{{ board.currentVersion }}</span></span>
          <span class="text-muted pick-hint">비교할 버전 2개를 선택하세요 ({{ picked.length }}/2)</span>
        </div>
        <div class="table-wrap">
          <table>
            <thead><tr><th class="col-check"></th><th>버전</th><th>작성자</th><th>작성일시</th><th>복원 기준</th><th class="col-actions">복원</th></tr></thead>
            <tbody>
              <tr v-for="v in versions" :key="v.version">
                <td class="col-check"><input v-model="picked" type="checkbox" :value="v.version" :disabled="picked.length >= 2 && !picked.includes(v.version)" :aria-label="`v${v.version} 선택`" /></td>
                <td><strong>v{{ v.version }}</strong> <span v-if="v.version === board.currentVersion" class="chip chip-success">현재</span></td>
                <td>{{ v.createdByName }}</td><td>{{ v.createdAt }}</td>
                <td>{{ v.recoveredVersion ? `v${v.recoveredVersion}에서 복원` : '-' }}</td>
                <td class="col-actions"><button type="button" class="btn btn-text" :disabled="v.version === board.currentVersion" @click="askRestore(v)">이 버전으로 복원</button></td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>

      <section class="card">
        <div class="card-head"><span class="card-title">{{ diffTitle }}</span></div>
        <div v-if="diff.length" class="diff" role="region" aria-label="버전 비교">
          <div v-for="(line, i) in diff" :key="i" class="diff-line" :class="line.type">
            <span class="sign" aria-hidden="true">{{ line.type === 'add' ? '+' : line.type === 'del' ? '−' : ' ' }}</span><span class="text">{{ line.text || ' ' }}</span>
          </div>
        </div>
        <div v-else class="empty-state">비교할 버전을 두 개 선택하면 본문 차이를 보여줍니다.</div>
      </section>
    </div>

    <YesNoModal
      v-model="restoreOpen" title="버전 복원" :message="target ? `v${target.version}의 내용으로 복원할까요?` : ''"
      :sub-message="target ? `v${target.version}의 본문으로 새 버전(v${board.currentVersion + 1})이 만들어지고, 복원 기준 버전이 기록됩니다.` : ''" @confirm="onRestore"
    />
    <AppToast :message="toastMessage" />
  </div>
</template>

<script>
import AppToast from '@/components/common/AppToast.vue'
import YesNoModal from '@/components/common/YesNoModal.vue'
import adminStore from '@/store/adminStore'
import adminViewMixin from '@/mixins/adminViewMixin'
import { diffLines } from '@/utils/adminMeta'

export default {
  name: 'SystemBoardVersionView',
  components: { AppToast, YesNoModal },
  mixins: [adminViewMixin],
  data() { return { picked: [], restoreOpen: false, target: null } },
  computed: {
    boardId() { return Number(this.$route.params.boardId) },
    board() { return adminStore.getBoard(this.boardId) },
    areaKey() { return this.board && this.board.areaCd === 'SYS_FREE' ? 'free' : 'notice' },
    isAdmin() { return adminStore.isAdmin() },
    versions() { return adminStore.versionsOf(this.boardId) },
    // 오래된 버전 → 최신 버전 순서로 비교한다
    pair() { return [...this.picked].sort((a, b) => a - b).map((n) => this.versions.find((v) => v.version === n)) },
    diff() { return this.pair.length === 2 ? diffLines(this.pair[0].content, this.pair[1].content) : [] },
    diffTitle() { return this.pair.length === 2 ? `v${this.pair[0].version} → v${this.pair[1].version} 비교` : '버전 비교' },
  },
  methods: {
    askRestore(v) { this.target = v; this.restoreOpen = true },
    async onRestore() {
      try {
        await adminStore.restoreVersion(this.boardId, this.target.version)
        this.restoreOpen = false
        this.picked = []
        this.showToast(`v${this.target.version}에서 복원했습니다.`)
      } catch (err) { this.restoreOpen = false; this.fail(err) }
    },
  },
}
</script>

<style scoped>
.pick-hint { font-size: var(--font-size-xs); }
.diff { padding: var(--space-3) 0; font-family: ui-monospace, SFMono-Regular, Menlo, Consolas, monospace; font-size: 12.5px; }
.diff-line { display: flex; padding: 1px var(--space-4); white-space: pre-wrap; line-height: 1.7; }
.diff-line .sign { width: 18px; flex-shrink: 0; color: var(--color-text-muted); }
.diff-line.add { background: var(--color-success-50); color: var(--color-success-800); }
.diff-line.del { background: var(--color-error-50); color: var(--color-error-800); }
</style>

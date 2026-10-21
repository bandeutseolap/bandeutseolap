<template>
  <div class="pms-page is-narrow">
    <div v-if="!board" class="card"><div class="empty-state">게시글을 찾을 수 없거나 열람 권한이 없습니다.<div class="retry-row"><router-link :to="{ name: 'SystemBoardList' }" class="btn btn-outline btn-sm">목록으로</router-link></div></div></div>

    <template v-else>
      <article class="card">
        <header class="post-head">
          <div class="post-badges">
            <span class="chip chip-common">{{ area.label }}</span>
            <span v-if="board.fixedTopYn" class="chip chip-warn">📌 고정</span>
            <span v-if="!board.visibleYn" class="chip badge-muted">블라인드</span>
            <span v-if="board.postStatusCd === 'TEMP'" class="chip badge-muted">임시저장</span>
          </div>
          <h1 class="post-title">{{ board.title }}</h1>
          <div class="post-meta">
            <span>{{ board.writerName }}</span><span>{{ board.writtenAt }}</span>
            <span v-if="board.currentVersion > 1">최종 수정 {{ board.updaterName }} · {{ board.updatedAt }}</span>
            <span v-if="board.currentVersion > 1" class="chip chip-info">v{{ board.currentVersion }}</span>
          </div>
        </header>

        <!-- 본문은 텍스트로만 출력한다(HTML 로 해석하지 않음) -->
        <div class="post-body">{{ board.content }}</div>

        <section v-if="board.files.length" class="post-files">
          <div class="files-title">첨부파일 {{ board.files.length }}</div>
          <ul>
            <li v-for="f in board.files" :key="f.fileId">
              <span>📎 {{ f.originFileName }} <span class="text-muted">({{ formatBytes(f.fileSize) }})</span></span>
              <!-- TODO(API 연동): file_id 기반 다운로드 엔드포인트 -->
              <button type="button" class="btn btn-text" @click="showToast('샘플: 실제 파일은 내려받지 않습니다.')">다운로드</button>
            </li>
          </ul>
        </section>

        <footer class="post-actions">
          <router-link :to="{ name: 'SystemBoardList', query: { area: areaKey } }" class="btn btn-outline">목록</router-link>
          <span class="grow"></span>
          <router-link v-if="canEdit" :to="{ name: 'SystemBoardEdit', params: { boardId: board.boardId }, query: { area: areaKey } }" class="btn btn-outline">수정</router-link>
          <button v-if="canEdit" type="button" class="btn btn-outline-error" @click="deleteOpen = true">삭제</button>
          <router-link v-if="isAdmin" :to="{ name: 'SystemBoardVersions', params: { boardId: board.boardId }, query: { area: areaKey } }" class="btn btn-outline">버전 이력</router-link>
          <button v-if="isAdmin && areaKey === 'free'" type="button" class="btn btn-outline" @click="toggleBlind">{{ board.visibleYn ? '블라인드' : '블라인드 해제' }}</button>
        </footer>
      </article>

      <!-- 자유게시판: 댓글 -->
      <section v-if="areaKey === 'free'" class="card mt-4">
        <div class="card-head"><span class="card-title">댓글 {{ comments.filter((c) => c.visibleYn).length }}</span></div>
        <ul class="comment-list">
          <li v-for="c in comments" :key="c.commentId" :class="{ reply: c.parentId, blinded: !c.visibleYn }">
            <div class="comment-head">
              <strong>{{ c.writerName }}</strong><span class="text-muted">{{ c.writtenAt }}</span>
              <span v-if="!c.visibleYn" class="chip badge-muted">블라인드</span>
              <span class="grow"></span>
              <button v-if="!c.parentId" type="button" class="btn btn-text" @click="replyTo = replyTo === c.commentId ? null : c.commentId">답글</button>
              <button v-if="isAdmin" type="button" class="btn btn-text" @click="blindComment(c)">{{ c.visibleYn ? '블라인드' : '해제' }}</button>
            </div>
            <p class="comment-body">{{ c.content }}</p>
            <form v-if="replyTo === c.commentId" class="comment-form" @submit.prevent="submitComment(c.commentId)">
              <input v-model="replyText" class="form-control" placeholder="답글을 입력하세요" aria-label="답글" maxlength="500" />
              <button type="submit" class="btn btn-primary btn-sm">등록</button>
            </form>
          </li>
          <li v-if="!comments.length" class="text-muted no-comment">첫 댓글을 남겨보세요.</li>
        </ul>
        <form class="comment-form main" @submit.prevent="submitComment(null)">
          <textarea v-model="commentText" class="form-control" rows="2" placeholder="댓글을 입력하세요" aria-label="댓글" maxlength="500"></textarea>
          <button type="submit" class="btn btn-primary">댓글 등록</button>
        </form>
      </section>
    </template>

    <YesNoModal v-model="deleteOpen" title="게시글 삭제" message="이 게시글을 삭제할까요?" sub-message="삭제하면 목록에서 사라지고, 첨부파일은 스토리지 정합성 점검에서 '끊긴 참조'로 표시됩니다." @confirm="onDelete" />
    <AppToast :message="toastMessage" />
  </div>
</template>

<script>
import AppToast from '@/components/common/AppToast.vue'
import YesNoModal from '@/components/common/YesNoModal.vue'
import adminStore from '@/store/adminStore'
import sampleStore from '@/store/sampleStore'
import adminViewMixin from '@/mixins/adminViewMixin'
import { BOARD_AREAS, formatBytes } from '@/utils/adminMeta'

export default {
  name: 'SystemBoardDetailView',
  components: { AppToast, YesNoModal },
  mixins: [adminViewMixin],
  data() { return { deleteOpen: false, commentText: '', replyTo: null, replyText: '' } },
  computed: {
    boardId() { return Number(this.$route.params.boardId) },
    board() { return adminStore.getBoard(this.boardId) },
    areaKey() { return this.board && this.board.areaCd === 'SYS_FREE' ? 'free' : 'notice' },
    area() { return BOARD_AREAS[this.areaKey] },
    canEdit() { return !!this.board && adminStore.canEditBoard(this.board) },
    isAdmin() { return adminStore.isAdmin() },
    comments() { return this.board ? adminStore.commentsOf(this.boardId) : [] },
  },
  created() {
    // 진입 시 읽음 상태 upsert (임시저장 글은 제외)
    if (this.board && this.board.postStatusCd === 'PUBLISHED') adminStore.markRead(this.boardId)
    this.showFlash()
  },
  methods: {
    formatBytes,
    async onDelete() {
      try {
        await adminStore.deleteBoard(this.boardId)
        sampleStore.setFlash('게시글을 삭제했습니다.')
        this.$router.push({ name: 'SystemBoardList', query: { area: this.areaKey } })
      } catch (err) { this.deleteOpen = false; this.fail(err) }
    },
    async toggleBlind() {
      try { await adminStore.setBlind(this.boardId, !this.board.visibleYn); this.showToast(this.board.visibleYn ? '블라인드를 해제했습니다.' : '블라인드 처리했습니다.') } catch (err) { this.fail(err) }
    },
    async submitComment(parentId) {
      const text = (parentId ? this.replyText : this.commentText).trim()
      if (!text) return
      await adminStore.addComment(this.boardId, text, parentId)
      if (parentId) { this.replyText = ''; this.replyTo = null } else this.commentText = ''
    },
    async blindComment(c) { try { await adminStore.blindComment(c.commentId, !c.visibleYn) } catch (err) { this.fail(err) } },
  },
}
</script>

<style scoped>
.post-head { padding: var(--space-5) var(--space-6) var(--space-4); border-bottom: 1px solid var(--color-border); }
.post-badges { display: flex; flex-wrap: wrap; gap: 6px; margin-bottom: var(--space-2); }
.post-title { margin: 0 0 var(--space-2); font-size: var(--font-size-xl); letter-spacing: -0.4px; }
.post-meta { display: flex; flex-wrap: wrap; align-items: center; gap: var(--space-3); font-size: var(--font-size-sm); color: var(--color-text-muted); }
.post-body { min-height: 160px; padding: var(--space-6); white-space: pre-wrap; line-height: 1.8; font-size: var(--font-size-md); }
.post-files { margin: 0 var(--space-6) var(--space-5); padding: var(--space-3) var(--space-4); border: 1px solid var(--color-border); border-radius: var(--radius-md); background: var(--color-surface-muted); }
.files-title { margin-bottom: 6px; font-size: var(--font-size-sm); font-weight: 700; }
.post-files ul { margin: 0; padding: 0; list-style: none; }
.post-files li { display: flex; align-items: center; justify-content: space-between; gap: var(--space-2); padding: 3px 0; font-size: var(--font-size-sm); }
.post-actions { display: flex; flex-wrap: wrap; align-items: center; gap: var(--space-2); padding: var(--space-4) var(--space-6); border-top: 1px solid var(--color-border); }
.grow { flex: 1; }
.retry-row { margin-top: var(--space-3); }
.comment-list { margin: 0; padding: 0; list-style: none; }
.comment-list li { padding: var(--space-3) var(--space-5); border-bottom: 1px solid var(--color-border); }
.comment-list li.reply { padding-left: var(--space-10); background: var(--color-surface-muted); }
.comment-list li.blinded .comment-body { color: var(--color-text-faint); }
.comment-head { display: flex; align-items: center; gap: var(--space-2); font-size: var(--font-size-sm); }
.comment-body { margin: 4px 0 0; font-size: var(--font-size-sm); white-space: pre-wrap; }
.comment-form { display: flex; gap: var(--space-2); margin-top: var(--space-2); }
.comment-form.main { align-items: flex-end; margin: 0; padding: var(--space-4) var(--space-5); }
.comment-form.main textarea { flex: 1; }
.no-comment { text-align: center; }
</style>

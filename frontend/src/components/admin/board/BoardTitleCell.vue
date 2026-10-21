<template>
  <span class="title-cell">
    <router-link :to="{ name: 'SystemBoardDetail', params: { boardId: b.boardId }, query: { area: areaKey } }" class="title-link" :class="{ unread: b.readStatus !== 'READ' }">{{ b.title }}</router-link>
    <span v-if="b.hasFile" class="icon" title="첨부파일" aria-label="첨부파일 있음">📎</span>
    <span v-if="b.commentCount" class="comment-count" :title="`댓글 ${b.commentCount}개`">[{{ b.commentCount }}]</span>
    <span v-if="b.readStatus !== 'READ'" class="chip" :class="status[b.readStatus].chip" :title="status[b.readStatus].title">{{ status[b.readStatus].label }}</span>
    <span v-if="!b.visibleYn" class="chip badge-muted">블라인드</span>
    <span v-if="b.postStatusCd === 'TEMP'" class="chip badge-muted">임시저장</span>
    <span v-if="b.openTargetCd === 'ADMIN'" class="chip chip-warn">관리자</span>
  </span>
</template>

<script>
import { READ_STATUS } from '@/utils/adminMeta'

export default {
  name: 'BoardTitleCell',
  props: { b: { type: Object, required: true }, areaKey: { type: String, default: 'notice' } },
  data() { return { status: READ_STATUS } },
}
</script>

<style scoped>
.title-cell { display: inline-flex; flex-wrap: wrap; align-items: center; gap: 6px; }
.title-link { color: var(--color-text); }
.title-link.unread { font-weight: 700; }
.title-link:hover { color: var(--color-primary-700); text-decoration: underline; }
.comment-count { font-size: 12px; font-weight: 700; color: var(--color-primary-600); }
.icon { font-size: 12px; }
</style>

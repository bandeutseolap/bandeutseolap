<template>
  <div class="pms-page">
    <div class="page-header">
      <h1>시스템 게시판</h1>
      <p>전체 사용자 대상 공지사항과 자유게시판입니다.</p>
    </div>

    <section class="card">
      <!-- 탭: board_area_cd 전환 -->
      <div class="tabs inner-tabs" role="tablist">
        <button v-for="(a, key) in areas" :key="key" type="button" role="tab" class="tab-btn" :class="{ active: areaKey === key }" :aria-selected="areaKey === key" @click="changeArea(key)">{{ a.label }}</button>
      </div>

      <form class="board-search" @submit.prevent="search">
        <select v-model="form.scope" class="select-inline" aria-label="검색 범위">
          <option value="title">제목</option><option value="titleContent">제목+본문</option><option value="writer">작성자</option>
        </select>
        <input v-model="form.keyword" class="form-control kw" placeholder="검색어" aria-label="검색어" />
        <input v-model="form.from" type="date" class="form-control date" aria-label="시작일" /><span>~</span><input v-model="form.to" type="date" class="form-control date" aria-label="종료일" />
        <button type="submit" class="btn btn-primary">검색</button>
        <button type="button" class="btn btn-outline" @click="reset">초기화</button>
        <span class="grow"></span>
        <label class="temp-check"><input v-model="tempOnly" type="checkbox" @change="page = 1" /> 내 임시저장</label>
        <router-link v-if="canWrite" :to="{ name: 'SystemBoardCreate', query: { area: areaKey } }" class="btn btn-primary">글쓰기</router-link>
      </form>

      <div class="table-wrap">
        <table class="board-table">
          <thead><tr><th class="num">번호</th><th>제목</th><th class="writer">작성자</th><th class="date-col">작성일</th></tr></thead>
          <tbody>
            <!-- 고정글: 페이지와 무관하게 항상 노출(별도 조회) -->
            <tr v-for="b in result.fixed" :key="'f' + b.boardId" class="fixed-row">
              <td class="num"><span class="chip chip-common">고정</span></td>
              <td><TitleCell :b="b" :area-key="areaKey" /></td>
              <td class="writer">{{ b.writerName }}</td><td class="date-col">{{ b.writtenAt.slice(0, 10) }}</td>
            </tr>
            <tr v-if="!result.fixed.length && !result.items.length" class="empty-row"><td colspan="4">{{ tempOnly ? '임시저장한 글이 없습니다.' : '게시글이 없습니다.' }}</td></tr>
            <tr v-for="(b, i) in result.items" :key="b.boardId">
              <td class="num">{{ result.total - ((page - 1) * size + i) }}</td>
              <td><TitleCell :b="b" :area-key="areaKey" /></td>
              <td class="writer">{{ b.writerName }}</td><td class="date-col">{{ b.writtenAt.slice(0, 10) }}</td>
            </tr>
          </tbody>
        </table>
      </div>
      <PaginationBar v-model:page="page" :total="result.total" :size="size" />
    </section>
    <AppToast :message="toastMessage" />
  </div>
</template>

<script>
import AppToast from '@/components/common/AppToast.vue'
import PaginationBar from '@/components/common/PaginationBar.vue'
import TitleCell from '@/components/admin/board/BoardTitleCell.vue'
import adminStore from '@/store/adminStore'
import adminViewMixin from '@/mixins/adminViewMixin'
import { BOARD_AREAS } from '@/utils/adminMeta'

const blank = () => ({ scope: 'title', keyword: '', from: '', to: '' })

export default {
  name: 'SystemBoardListView',
  components: { AppToast, PaginationBar, TitleCell },
  mixins: [adminViewMixin],
  data() { return { areas: BOARD_AREAS, form: blank(), applied: blank(), page: 1, size: 10, tempOnly: false } },
  computed: {
    areaKey() { return this.$route.query.area === 'free' ? 'free' : 'notice' },
    canWrite() { return adminStore.canWriteIn(this.areaKey) },
    result() { return adminStore.listBoards({ areaKey: this.areaKey, ...this.applied, page: this.page, size: this.size, tempOnly: this.tempOnly }) },
  },
  watch: {
    // 헤더 메뉴(공지사항/자유게시판)로 같은 화면에서 탭만 바뀌는 경우에도 검색 조건을 초기화한다
    areaKey() { this.form = blank(); this.applied = blank(); this.page = 1; this.tempOnly = false },
  },
  created() { this.showFlash() },
  methods: {
    changeArea(key) { if (key !== this.areaKey) this.$router.push({ name: 'SystemBoardList', query: { area: key } }) },
    search() { this.applied = { ...this.form }; this.page = 1 },
    reset() { this.form = blank(); this.search() },
  },
}
</script>

<style scoped>
.inner-tabs { margin: 0; padding: 0 var(--space-4); }
.board-search { display: flex; flex-wrap: wrap; align-items: center; gap: var(--space-2); padding: var(--space-3) var(--space-4); border-bottom: 1px solid var(--color-border); }
.board-search .kw { width: 220px; }
.board-search .date { width: 150px; }
.board-search .grow { flex: 1; }
.temp-check { display: inline-flex; align-items: center; gap: 6px; font-size: var(--font-size-sm); color: var(--color-text-soft); cursor: pointer; }
.board-table .num { width: 80px; text-align: center; color: var(--color-text-muted); font-variant-numeric: tabular-nums; }
.board-table .writer { width: 130px; }
.board-table .date-col { width: 120px; color: var(--color-text-muted); }
.fixed-row td { background: var(--color-primary-50); }
@media (max-width: 700px) { .board-search .kw { width: 100%; } .board-table .writer { display: none; } }
</style>

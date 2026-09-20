<template>
  <div>
    <div class="card">
      <div class="toolbar">
        <div style="display:flex; gap:10px;">
          <div class="search-box">
            <span>🔍</span>
            <input v-model="keyword" placeholder="프로젝트명 / 코드 검색" />
          </div>
          <select class="select-inline" v-model="statusFilter">
            <option value="ALL">전체 상태</option>
            <option value="READY">준비중</option>
            <option value="IN_PROGRESS">진행중</option>
            <option value="HOLD">보류</option>
            <option value="DONE">완료</option>
          </select>
        </div>
        <!-- <button class="btn btn-primary" @click="openCreate">+ 프로젝트 등록</button> -->
        <router-link class="btn btn-primary" to="projects/new">+ 프로젝트 등록</router-link>
      </div>

      <table>
        <thead>
          <tr>
            <th>프로젝트명</th>
            <th>코드</th>
            <th>담당 PM</th>
            <th>기간</th>
            <th>상태</th>
            <th class="col-actions">관리</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="filteredProjects.length === 0" class="empty-row">
            <td colspan="6">조건에 맞는 프로젝트가 없습니다.</td>
          </tr>
          <tr v-for="p in filteredProjects" :key="p.id">
            <td><strong>{{ p.name }}</strong></td>
            <td class="cell-code">{{ p.code }}</td>
            <td>{{ p.manager || '-' }}</td>
            <td>{{ p.startDate || '-' }} ~ {{ p.endDate || '-' }}</td>
            <td><span class="badge" :class="statusBadgeClass(p.status)">{{ statusLabel(p.status) }}</span></td>
            <td class="col-actions">
              <!-- <button class="btn btn-text" @click="openEdit(p)">수정</button> -->
              <router-link :to="{ name: 'ProjectEdit', params: { id: p.id } }" class="btn btn-text">
                수정
              </router-link>
              <button class="btn btn-danger-text" @click="requestDelete(p)">삭제</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <ProjectYesNoModal
      v-if="deleteTarget"
      title="프로젝트 삭제"
      message="아래 프로젝트를 삭제하시겠습니까? 하위 수행단계도 함께 삭제됩니다."
      :highlight="deleteTarget.name"
      @confirm="confirmDelete"
      @cancel="deleteTarget = null"
    />
  </div>
</template>

<script>
import ProjectYesNoModal from '@/components/common/ProjectYesNoModal.vue';

export default {
  name: 'ProjectListView',
  components: { ProjectYesNoModal },
  props: {
    // 목록 데이터는 부모(views/ProjectManageView.vue)에서 내려받는다.
    // 이 컴포넌트는 표시/필터링/모달 트리거만 담당 (단방향 데이터 흐름 유지)
    projects: { type: Array, required: true },
  },
  emits: ['create', 'update', 'delete'],
  data() {
    return {
      keyword: '',
      statusFilter: 'ALL',
      showFormModal: false,
      editTarget: null,   // null = 등록모드, 객체 = 수정모드
      deleteTarget: null, // 삭제 확인 대상
    };
  },
  computed: {
    filteredProjects() {
      return this.projects.filter((p) => {
        const matchKeyword =
          !this.keyword ||
          p.name.includes(this.keyword) ||
          p.code.toLowerCase().includes(this.keyword.toLowerCase());
        const matchStatus = this.statusFilter === 'ALL' || p.status === this.statusFilter;
        return matchKeyword && matchStatus;
      });
    },
  },
  methods: {
    statusBadgeClass(status) {
      return {
        READY: 'badge-muted',
        IN_PROGRESS: 'badge-primary',
        HOLD: 'badge-warning',
        DONE: 'badge-success',
      }[status] || 'badge-muted';
    },
    statusLabel(status) {
      return { READY: '준비중', IN_PROGRESS: '진행중', HOLD: '보류', DONE: '완료' }[status] || status;
    },
    openCreate() {
      this.editTarget = null;
      this.showFormModal = true;
    },
    openEdit(project) {
      this.editTarget = project;
      this.showFormModal = true;
    },
    onFormSubmit(payload) {
      // 실제 CUD 로직(axios 호출)은 부모 view에서 처리 -> 여기선 이벤트만 위임
      this.$emit(this.editTarget ? 'update' : 'create', payload);
      this.showFormModal = false;
    },
    requestDelete(project) {
      this.deleteTarget = project;
    },
    confirmDelete() {
      this.$emit('delete', this.deleteTarget.id);
      this.deleteTarget = null;
    },
  },
};
</script>

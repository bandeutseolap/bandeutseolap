<template>
  <div class="pms-page">
    <div class="page-header">
      <div>
        <h1>{{ isEditMode ? '수행단계 수정' : '수행단계 등록' }}</h1>
        <p v-if="projectName">
          <strong>{{ projectName }}</strong> 프로젝트의 수행단계를 {{ isEditMode ? '수정' : '등록' }}합니다.
        </p>
      </div>
      <button class="btn btn-outline" @click="goBack">← 목록으로</button>
    </div>

    <div class="card form-card">
      <div class="form-body">
        <div class="form-grid-2">
          <div class="form-row">
            <label>순번<span class="req">*</span></label>
            <input type="number" min="1" class="form-control" v-model.number="form.order" />
            <p v-if="errors.order" class="field-error">{{ errors.order }}</p>
          </div>
          <div class="form-row">
            <label>진행 상태</label>
            <select class="form-control" v-model="form.status">
              <option value="READY">대기</option>
              <option value="IN_PROGRESS">진행중</option>
              <option value="DONE">완료</option>
            </select>
          </div>
        </div>

        <div class="form-row">
          <label>단계명<span class="req">*</span></label>
          <input class="form-control" v-model="form.name" placeholder="예) 분석, 설계, 구현, 테스트, 배포" />
          <p v-if="errors.name" class="field-error">{{ errors.name }}</p>
        </div>

        <div class="form-grid-2">
          <div class="form-row">
            <label>시작일</label>
            <input type="date" class="form-control" v-model="form.startDate" />
          </div>
          <div class="form-row">
            <label>종료일</label>
            <input type="date" class="form-control" v-model="form.endDate" />
          </div>
        </div>

        <div class="form-row">
          <label>비고</label>
          <textarea
            class="form-control"
            rows="6"
            v-model="form.description"
            placeholder="단계별 산출물, 참고사항 등"
          ></textarea>
        </div>
      </div>

      <div class="form-footer">
        <button class="btn btn-outline" @click="goBack">취소</button>
        <button class="btn btn-primary" :disabled="submitting" @click="onSubmit">
          {{ submitting ? '저장 중...' : (isEditMode ? '수정 저장' : '등록') }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import phaseStore from '@/store/phaseStore';
import projectStore from '@/store/projectStore';

export default {
  name: 'PhaseFormView',
  // route.params(projectId, id)를 props로 받기 위해 router 설정에서 props: true 필요
  // (router/project.routes.js 참고)
  props: {
    projectId: { type: [String, Number], required: true },
    id: { type: [String, Number], default: null },
  },
  data() {
    return {
      form: {
        id: null,
        order: 1,
        name: '',
        startDate: '',
        endDate: '',
        status: 'READY', // READY | IN_PROGRESS | DONE
        description: '',
      },
      errors: {},
      submitting: false,
    };
  },
  computed: {
    isEditMode() {
      return this.id !== null && this.id !== undefined;
    },
    projectName() {
      const project = projectStore.getById(this.projectId);
      return project ? project.name : '';
    },
  },
  created() {
    if (this.isEditMode) {
      this.loadPhase();
    } else {
      // 신규 등록 시 기본 순번은 현재 프로젝트의 마지막 순번 + 1
      this.form.order = phaseStore.nextOrder(this.projectId);
    }
  },
  methods: {
    loadPhase() {
      // TODO(API 연동): axios.get(`/api/projects/${this.projectId}/phases/${this.id}`)로 교체
      const found = phaseStore.getById(this.projectId, this.id);
      if (!found) {
        // 잘못된 id로 직접 접근한 경우 목록(수행단계 탭)으로 되돌림
        this.$router.replace({ name: 'ProjectManage', query: { tab: 'phase' } });
        return;
      }
      // 원본을 얕은 복사하여 폼에 주입 (store 데이터 직접 변형 방지)
      this.form = { ...found };
    },
    validate() {
      const e = {};
      if (!this.form.name.trim()) e.name = '단계명을 입력해주세요.';
      if (!this.form.order || this.form.order < 1) e.order = '순번은 1 이상이어야 합니다.';
      this.errors = e;
      return Object.keys(e).length === 0;
    },
    async onSubmit() {
      if (!this.validate()) return;
      this.submitting = true;
      try {
        if (this.isEditMode) {
          await phaseStore.update(this.projectId, { ...this.form, id: Number(this.id) });
        } else {
          await phaseStore.create(this.projectId, { ...this.form });
        }
        this.goBack();
      } catch (err) {
        // TODO: 공통 에러 처리(토스트/알림) 컨벤션에 맞춰 연결
        console.error('수행단계 저장 실패', err);
      } finally {
        this.submitting = false;
      }
    },
    goBack() {
      // 수행단계 탭으로 복귀하도록 query 유지
      this.$router.push({ name: 'ProjectManage', query: { tab: 'phase' } });
    },
  },
};
</script>

<style scoped>
.pms-page {
  padding: 28px 32px;
  max-width: 860px;
}
.page-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 18px;
}
.page-header h1 { font-size: 19px; margin: 0 0 4px; }
.page-header p { margin: 0; color: var(--color-text-sub); font-size: 12.5px; }

.form-card { padding: 0; }
.form-body { padding: 24px; }
.form-footer {
  padding: 16px 24px;
  border-top: 1px solid var(--color-border);
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}
</style>

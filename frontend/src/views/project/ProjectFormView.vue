<template>
  <div class="pms-page">
    <div class="page-header">
      <div>
        <h1>{{ isEditMode ? '프로젝트 수정' : '프로젝트 등록' }}</h1>
        <p>{{ isEditMode ? '프로젝트 정보를 수정합니다.' : '신규 프로젝트를 등록합니다.' }}</p>
      </div>
      <!-- 목록으로 돌아가기: 취소와 동일한 동작이지만 상단에도 노출해 이동 동선을 짧게 함 -->
      <button class="btn btn-outline" @click="goBack">← 목록으로</button>
    </div>

    <div class="card form-card">
      <div class="form-body">
        <div class="form-grid-2">
          <div class="form-row">
            <label>프로젝트명<span class="req">*</span></label>
            <input class="form-control" v-model="form.name" placeholder="예) 반듯서랍 협업 플랫폼 고도화" />
            <p v-if="errors.name" class="field-error">{{ errors.name }}</p>
          </div>
          <div class="form-row">
            <label>프로젝트 코드<span class="req">*</span></label>
            <!-- 코드는 식별자 성격상 수정 모드에서는 잠금 -->
            <input class="form-control" v-model="form.code" placeholder="예) BDS-2026-01" :disabled="isEditMode" />
            <p v-if="errors.code" class="field-error">{{ errors.code }}</p>
          </div>
        </div>

        <div class="form-grid-2">
          <div class="form-row">
            <label>담당 PM</label>
            <input class="form-control" v-model="form.manager" placeholder="담당자명" />
          </div>
          <div class="form-row">
            <label>진행 상태</label>
            <select class="form-control" v-model="form.status">
              <option value="READY">준비중</option>
              <option value="IN_PROGRESS">진행중</option>
              <option value="HOLD">보류</option>
              <option value="DONE">완료</option>
            </select>
          </div>
        </div>

        <div class="form-grid-2">
          <div class="form-row">
            <label>시작일</label>
            <input type="date" class="form-control" v-model="form.startDate" />
          </div>
          <div class="form-row">
            <label>종료일</label>
            <input type="date" class="form-control" v-model="form.endDate" />
            <p v-if="errors.endDate" class="field-error">{{ errors.endDate }}</p>
          </div>
        </div>

        <div class="form-row">
          <label>프로젝트 설명</label>
          <textarea
            class="form-control"
            rows="6"
            v-model="form.description"
            placeholder="프로젝트 개요를 입력해주세요."
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
import projectStore from '@/store/projectStore';

export default {
  name: 'ProjectFormView',
  // route.params.id를 props로 직접 받기 위해 router 설정에서 props: true 필요
  // (router/index.js 예시 참고)
  props: {
    id: { type: [String, Number], default: null },
  },
  data() {
    return {
      form: {
        id: null,
        name: '',
        code: '',
        manager: '',
        startDate: '',
        endDate: '',
        status: 'READY', // READY | IN_PROGRESS | HOLD | DONE
        description: '',
      },
      errors: {},
      submitting: false,
    };
  },
  computed: {
    isEditMode() {
      // props.id는 라우트 파라미터 문자열일 수 있어 null/undefined만 명시적으로 체크
      return this.id !== null && this.id !== undefined;
    },
  },
  created() {
    if (this.isEditMode) {
      this.loadProject();
    }
  },
  methods: {
    loadProject() {
      // TODO(API 연동): axios.get(`/api/projects/${this.id}`)로 교체하고
      //                 실패 시(404 등) 목록으로 리다이렉트 처리 필요
      const found = projectStore.getById(this.id);
      if (!found) {
        // 잘못된 id로 직접 접근한 경우 목록으로 되돌림
        this.$router.replace({ name: 'ProjectManage' });
        return;
      }
      // 원본을 얕은 복사하여 폼에 주입 (store 데이터 직접 변형 방지)
      this.form = { ...found };
    },
    validate() {
      const e = {};
      if (!this.form.name.trim()) e.name = '프로젝트명을 입력해주세요.';
      if (!this.form.code.trim()) e.code = '프로젝트 코드를 입력해주세요.';
      if (this.form.startDate && this.form.endDate && this.form.startDate > this.form.endDate) {
        e.endDate = '종료일은 시작일보다 빠를 수 없습니다.';
      }
      this.errors = e;
      return Object.keys(e).length === 0;
    },
    async onSubmit() {
      if (!this.validate()) return;
      this.submitting = true;
      try {
        if (this.isEditMode) {
          await projectStore.update({ ...this.form, id: Number(this.id) });
        } else {
          await projectStore.create({ ...this.form });
        }
        this.$router.push({ name: 'ProjectManage' });
      } catch (err) {
        // TODO: 공통 에러 처리(토스트/알림) 컨벤션에 맞춰 연결
        console.error('프로젝트 저장 실패', err);
      } finally {
        this.submitting = false;
      }
    },
    goBack() {
      this.$router.push({ name: 'ProjectManage' });
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

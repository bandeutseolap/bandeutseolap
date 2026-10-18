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

        <div class="form-row">
          <label for="project-phase">단계명</label>
          <!-- 수정 모드: 현재 진행중인 단계명만 보여준다 (변경은 단계 설정에서) -->
          <template v-if="isEditMode">
            <input
              id="project-phase"
              class="form-control"
              :value="currentPhaseName"
              placeholder="진행중인 단계가 없습니다"
              disabled
            />
            <p class="field-hint">
              수행단계 변경은 <router-link :to="{ name: 'PhaseSetting' }">단계 설정</router-link>에서 합니다.
            </p>
          </template>
          <!-- 등록 모드: 기본값은 단계 설정에서 '진행중'인 단계명. 목록에서 고르거나 직접 입력 -->
          <template v-else>
            <input
              id="project-phase"
              v-model="phaseName"
              class="form-control"
              list="project-phase-names"
              placeholder="예) 분석, 설계, 구현"
              maxlength="50"
              autocomplete="off"
            />
            <datalist id="project-phase-names">
              <option v-for="name in phaseNameOptions" :key="name" :value="name" />
            </datalist>
            <p class="field-hint">
              등록하면 이 단계가 첫 수행단계로 '진행중' 상태로 함께 생성됩니다. 비워두면 만들지 않습니다.
            </p>
          </template>
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
import phaseStore from '@/store/phaseStore';

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
      // 등록 시 첫 수행단계로 만들 단계명 (프로젝트 레코드가 아니라 수행단계에 저장되므로 form 과 분리)
      phaseName: '',
      errors: {},
      submitting: false,
    };
  },
  computed: {
    // 단계 설정에 등록된 단계명 (입력 자동완성 선택지)
    phaseNameOptions() {
      return phaseStore.allNames();
    },
    // 수정 모드에서 보여줄, 이 프로젝트의 진행중 단계명
    currentPhaseName() {
      return this.isEditMode ? phaseStore.inProgressName(this.id) : '';
    },
    isEditMode() {
      // props.id는 라우트 파라미터 문자열일 수 있어 null/undefined만 명시적으로 체크
      return this.id !== null && this.id !== undefined;
    },
  },
  created() {
    if (this.isEditMode) {
      this.loadProject();
    } else {
      // 등록: 단계 설정에서 상태가 '진행중'인 단계명을 기본값으로 채운다
      this.phaseName = phaseStore.firstName();
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
          const created = await projectStore.create({ ...this.form });
          await this.createInitialPhase(created);
        }
        this.$router.push({ name: 'ProjectManage' });
      } catch (err) {
        // TODO: 공통 에러 처리(토스트/알림) 컨벤션에 맞춰 연결
        console.error('프로젝트 저장 실패', err);
      } finally {
        this.submitting = false;
      }
    },
    // 등록한 프로젝트의 첫 수행단계를 '진행중'으로 만든다 (단계명을 비우면 만들지 않음)
    // TODO(API 연동): 프로젝트와 첫 수행단계는 서버에서 한 트랜잭션으로 처리하는 것을 권장
    //                 (프로젝트만 저장되고 단계 생성이 실패하는 중간 상태를 막기 위해)
    async createInitialPhase(project) {
      const name = this.phaseName.trim();
      if (!name) return;
      try {
        await phaseStore.create(project.id, {
          order: phaseStore.nextOrder(project.id),
          name,
          startDate: this.form.startDate,
          endDate: '',
          status: 'IN_PROGRESS',
          description: '',
        });
      } catch (err) {
        // 프로젝트는 이미 저장됐으므로 화면은 이동시키고, 단계는 단계 설정에서 다시 등록할 수 있다
        console.error('첫 수행단계 생성 실패', err);
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
  max-width: 860px;
}
.page-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: var(--space-4);
}

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

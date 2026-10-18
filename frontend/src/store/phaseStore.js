import { reactive } from 'vue';

/**
 * 수행단계(Phase) mock store
 * -----------------------------------------------------------
 * 수행단계 등록/수정이 모달이 아닌 별도 페이지(라우트)로 분리되면서,
 * 컴포넌트 언마운트와 무관하게 데이터가 유지되도록 store로 분리했습니다.
 *
 * 수행단계는 프로젝트에 종속된 하위 리소스이므로
 * projectId를 key로 하는 맵 구조(phasesByProject)를 유지합니다.
 *
 * 실제 백엔드 연동 시:
 * - 프로젝트 상세 진입 시 axios.get(`/api/projects/${projectId}/phases`)로
 *   해당 프로젝트의 phasesByProject[projectId]를 채우세요.
 * - create/update/remove 내부의 mock 로직을 axios 호출로 교체하세요.
 * -----------------------------------------------------------
 */

const state = reactive({
  phasesByProject: {
    1: [
      { id: 101, projectId: 1, order: 1, name: '분석', startDate: '2026-01-05', endDate: '2026-01-20', status: 'DONE', description: '요구사항 정의' },
      { id: 102, projectId: 1, order: 2, name: '설계', startDate: '2026-01-21', endDate: '2026-02-15', status: 'IN_PROGRESS', description: '화면/API 설계' },
      { id: 103, projectId: 1, order: 3, name: '구현', startDate: '2026-02-16', endDate: '2026-05-15', status: 'READY', description: '' },
    ],
    2: [],
    3: [
      { id: 301, projectId: 3, order: 1, name: '분석', startDate: '2025-09-01', endDate: '2025-09-15', status: 'DONE', description: '' },
    ],
  },
  nextId: 302, // mock 채번용. 실 연동 시 서버 응답의 id를 그대로 사용.
});

function listByProject(projectId) {
  const numericId = Number(projectId);
  return (state.phasesByProject[numericId] || []).slice().sort((a, b) => a.order - b.order);
}

function projectIds() {
  return Object.keys(state.phasesByProject).map(Number).sort((a, b) => a - b);
}

/** 해당 프로젝트에서 상태가 '진행중'인 수행단계의 단계명 (없으면 빈 문자열) */
function inProgressName(projectId) {
  const found = listByProject(projectId).find((ph) => ph.status === 'IN_PROGRESS');
  return found ? found.name : '';
}

/**
 * 단계 설정 전체에서 '첫 단계'의 단계명 (프로젝트 등록 폼의 기본값).
 * 순번(order)이 가장 앞선 수행단계를 고르고, 순번이 같으면 프로젝트 id 가 작은 쪽을 쓴다.
 * 등록된 수행단계가 하나도 없으면 빈 문자열.
 */
function firstName() {
  const all = projectIds().flatMap((id) => listByProject(id));
  if (all.length === 0) return '';
  // projectIds() 가 id 오름차순이고 sort 는 안정 정렬이므로, 순번이 같으면 id 가 작은 프로젝트가 앞에 온다
  const first = all.slice().sort((x, y) => x.order - y.order)[0];
  return first.name;
}

/** 단계 설정에 등록된 단계명 목록 (중복 제거, 프로젝트/순번 순) — 등록 폼의 선택지 */
function allNames() {
  const names = [];
  projectIds().forEach((id) => {
    listByProject(id).forEach((ph) => {
      if (!names.includes(ph.name)) names.push(ph.name);
    });
  });
  return names;
}

function getById(projectId, id) {
  // route.params는 문자열이므로 Number 변환 후 비교 (타입 불일치로 인한 조회 실패 방지)
  const numericProjectId = Number(projectId);
  const numericId = Number(id);
  const list = state.phasesByProject[numericProjectId] || [];
  return list.find((ph) => ph.id === numericId) || null;
}

function nextOrder(projectId) {
  return listByProject(projectId).length + 1;
}

async function create(projectId, payload) {
  // TODO(API 연동): const { data } = await axios.post(`/api/projects/${projectId}/phases`, payload); return data;
  const numericProjectId = Number(projectId);
  if (!state.phasesByProject[numericProjectId]) state.phasesByProject[numericProjectId] = [];

  const id = state.nextId++;
  const created = { ...payload, id, projectId: numericProjectId };
  state.phasesByProject[numericProjectId].push(created);
  return created;
}

async function update(projectId, payload) {
  // TODO(API 연동): await axios.put(`/api/projects/${projectId}/phases/${payload.id}`, payload);
  const numericProjectId = Number(projectId);
  const list = state.phasesByProject[numericProjectId] || [];
  const idx = list.findIndex((ph) => ph.id === payload.id);
  if (idx !== -1) list.splice(idx, 1, { ...payload, projectId: numericProjectId });
  return payload;
}

async function remove(projectId, id) {
  // TODO(API 연동): await axios.delete(`/api/projects/${projectId}/phases/${id}`);
  const numericProjectId = Number(projectId);
  const numericId = Number(id);
  const list = state.phasesByProject[numericProjectId] || [];
  state.phasesByProject[numericProjectId] = list.filter((ph) => ph.id !== numericId);
}

export default {
  state, listByProject, getById, nextOrder, create, update, remove,
  inProgressName, firstName, allNames,
};

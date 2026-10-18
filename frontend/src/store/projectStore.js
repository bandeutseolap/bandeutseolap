import { reactive } from 'vue';

/**
 * 프로젝트 목록 mock store
 * -----------------------------------------------------------
 * 프로젝트 등록/수정이 모달이 아닌 별도 페이지(라우트)로 분리되면서,
 * 컴포넌트가 언마운트되어도 데이터가 유지되도록 store로 분리했습니다.
 *
 * 실제 백엔드 연동 시:
 * - state.projects 초기값을 비우고, 목록 화면 mounted()에서
 *   axios.get('/api/projects') 결과로 채우면 됩니다.
 * - create/update/remove 내부의 mock 로직을 axios 호출로 교체하세요.
 * - 프로젝트 규모가 커지면 Pinia(Vue 3 공식 상태관리)로 승격 권장.
 * -----------------------------------------------------------
 */

const state = reactive({
  projects: [
    { id: 1, name: '반듯서랍 협업 플랫폼 고도화', code: 'BDS-2026-01', manager: '김유진', startDate: '2026-01-05', endDate: '2026-06-30', status: 'IN_PROGRESS', description: '게시판/파일함/알림 기능 고도화' },
    { id: 2, name: '사내 그룹웨어 이관', code: 'BDS-2026-02', manager: '박서준', startDate: '2026-03-01', endDate: '2026-09-30', status: 'READY', description: '레거시 그룹웨어 → 반듯서랍 이관' },
    { id: 3, name: '모바일 앱 1차 출시', code: 'BDS-2025-11', manager: '이하늘', startDate: '2025-09-01', endDate: '2026-01-31', status: 'DONE', description: 'iOS/Android 1차 릴리즈' },
  ],
  nextId: 4, // mock 채번용. 실 연동 시 서버 응답의 id를 그대로 사용.
});

function getById(id) {
  // route.params.id는 문자열이므로 Number 변환 후 비교 (타입 불일치로 인한 조회 실패 방지)
  const numericId = Number(id);
  return state.projects.find((p) => p.id === numericId) || null;
}

async function create(payload) {
  // TODO(API 연동): const { data } = await axios.post('/api/projects', payload); return data;
  const id = state.nextId++;
  const created = { id, ...payload };
  state.projects.push(created);
  return created;
}

async function update(payload) {
  // TODO(API 연동): await axios.put(`/api/projects/${payload.id}`, payload);
  const idx = state.projects.findIndex((p) => p.id === payload.id);
  if (idx !== -1) state.projects.splice(idx, 1, { ...payload });
  return payload;
}

async function remove(id) {
  // TODO(API 연동): await axios.delete(`/api/projects/${id}`);
  state.projects = state.projects.filter((p) => p.id !== id);
}

export default { state, getById, create, update, remove };

import { reactive } from 'vue'
import dayjs from 'dayjs'
import { today } from '@/utils/workMeta'

/**
 * 샘플 데이터 store (시안 반듯서랍.html 의 샘플 데이터 기준)
 * -----------------------------------------------------------
 * 아직 API 가 없는 화면(멤버/문서/업무/이슈/계정/템플릿 공유/전체 대시보드)이
 * 공통으로 사용하는 mock store 이다. projectStore / phaseStore 와 같은 방식(reactive singleton)이라
 * 라우트가 바뀌어도 등록한 데이터가 유지된다.
 *
 * 실제 백엔드 연동 시: 각 함수 안의 mock 로직을 axios 호출로 교체하면 화면은 수정이 필요 없다.
 * -----------------------------------------------------------
 */

// 멤버 아바타 그라디언트 (시안 mem-av 색상)
const AVATAR_GRADIENTS = [
  'linear-gradient(135deg, var(--color-primary-600), var(--color-accent-500))',
  'linear-gradient(135deg, var(--color-success-500), var(--color-success-700))',
  'linear-gradient(135deg, var(--color-warning-500), var(--color-error-500))',
  'linear-gradient(135deg, var(--color-info-600), var(--color-info-500))',
]

const state = reactive({
  // 로그인 사용자 (샘플) — TODO(API 연동): 로그인 응답/내 정보 API 로 교체
  currentUser: { loginId: 'hyjdevloper', name: 'hyjdevloper', email: 'hyjdevloper@example.com' },

  // 사이드바 '내 프로젝트'
  selectedProjectId: 1,
  myProjects: [
    { id: 1, name: '반듯서랍 v1.0', role: 'PM', color: 'var(--color-primary-600)' },
    { id: 2, name: '사내 포털 리뉴얼', role: 'DEV', color: 'var(--color-success-500)' },
    { id: 3, name: 'AI 보고서 자동화', role: '멤버', color: 'var(--color-warning-500)' },
  ],

  members: [
    { id: 1, loginId: 'ycrdevloper', initial: 'Y', role: 'PM', desc: '시스템 설계 / ERD', gradient: AVATAR_GRADIENTS[0] },
    { id: 2, loginId: 'ljhdevloper', initial: 'L', role: 'DEV', desc: '게시판 API + Kafka', gradient: AVATAR_GRADIENTS[1] },
    { id: 3, loginId: 'hyjdevloper', initial: 'H', role: 'DEV', desc: '인증 / 로그인 + Kafka', gradient: AVATAR_GRADIENTS[2] },
    { id: 4, loginId: 'kyrdevloper', initial: 'K', role: 'DEV', desc: 'Frontend', gradient: AVATAR_GRADIENTS[3] },
  ],

  tasks: [
    { id: 1, title: 'GlobalExceptionHandler 구현', status: 'DONE', assignee: 'hyjdevloper', dueDate: '2026-10-15', completedAt: '2026-10-15', description: '' },
    { id: 2, title: 'JWT 인증 + Redis 토큰 관리', status: 'DONE', assignee: 'hyjdevloper', dueDate: '2026-10-10', completedAt: '2026-10-10', description: '' },
    { id: 3, title: '게시판 API 개발 — 목록/상세/등록', status: 'IN_PROGRESS', assignee: 'ljhdevloper', dueDate: '2026-10-25', completedAt: '', description: '' },
    { id: 4, title: '프로젝트 생성 시 기본 Step 자동 생성', status: 'DELAYED', assignee: 'ycrdevloper', dueDate: '2026-10-20', completedAt: '', description: '' },
    { id: 5, title: '중복 로그인 방지 (Redis accessToken)', status: 'WAIT', assignee: 'hyjdevloper', dueDate: '2026-11-05', completedAt: '', description: '' },
    { id: 6, title: '권한 설계 (app_user_role_map)', status: 'WAIT', assignee: 'ycrdevloper', dueDate: '2026-11-10', completedAt: '', description: '' },
    { id: 7, title: 'Kafka 로그인 이벤트 게시판 연동', status: 'WAIT', assignee: 'ljhdevloper', dueDate: '', completedAt: '', description: '' },
    { id: 8, title: 'JWT 인증 구현 완료', status: 'DONE', assignee: 'hyjdevloper', dueDate: '', completedAt: '2026-10-10', description: '' },
  ],

  documents: [
    { id: 1, title: 'API 설계서 v2', author: 'ycrdevloper', createdAt: '2026-10-15', templateName: '개발 명세서 템플릿', content: '' },
    { id: 2, title: 'ERD 설명서', author: 'ycrdevloper', createdAt: '2026-10-08', templateName: '', content: '' },
    { id: 3, title: '인증/인가 설계서', author: 'hyjdevloper', createdAt: '2026-10-10', templateName: '개발 명세서 템플릿', content: '' },
    { id: 4, title: '게시판 API 명세서', author: 'ljhdevloper', createdAt: '2026-10-13', templateName: '개발 명세서 템플릿', content: '' },
    { id: 5, title: '10월 3주차 회의록', author: 'ycrdevloper', createdAt: '2026-10-16', templateName: '회의록 템플릿', content: '' },
  ],

  templates: [
    { id: 1, name: '회의록 템플릿', scope: '공용', shared: true, owner: 'ycrdevloper', description: '정기 회의 기록용', content: '■ 일시:\n■ 참석자:\n■ 안건:\n■ 결정 사항:\n■ 후속 조치:' },
    { id: 2, name: '개발 명세서 템플릿', scope: '공용', shared: true, owner: 'ycrdevloper', description: 'API/기능 명세 작성용', content: '■ 개요\n■ 요청/응답 명세\n■ 예외 처리\n■ 변경 이력' },
    { id: 3, name: '장애 보고서 템플릿', scope: '프로젝트', shared: false, owner: 'hyjdevloper', description: '장애 발생 시 원인/조치 기록용', content: '■ 발생 일시:\n■ 영향 범위:\n■ 원인:\n■ 조치 내용:' },
  ],

  issues: [
    { id: 1, severity: 'HIGH', title: 'app_task FK 제약 위반 (step_id 미검증)', type: 'BUG', assignee: 'ycrdevloper', status: 'PROGRESS', createdAt: '2026-10-14', description: '' },
    { id: 2, severity: 'MED', title: 'Reflection 기반 FileCommonService 성능 이슈', type: 'IMPROVE', assignee: 'hyjdevloper', status: 'WAIT', createdAt: '2026-10-12', description: '' },
  ],

  // 단계별 진행률 (시안 '단계별 진행률')
  stages: [
    { id: 1, name: '인증', percent: 100, tone: 'ok' },
    { id: 2, name: '게시판 API', percent: 40, tone: '' },
    { id: 3, name: '프로젝트 관리', percent: 65, tone: 'warn' },
    { id: 4, name: '문서 관리', percent: 20, tone: '' },
    { id: 5, name: '이슈 트래커', percent: 10, tone: '' },
  ],

  // 마이페이지(내 정보)에서 수정한 이름/이메일 (로그인 아이디별) — 내 정보 조회 샘플 응답에 덮어쓴다
  profileEdits: {},

  // 마이페이지 샘플 데이터 (이름/이메일은 profileEdits + 내 정보 조회가 담당)
  // TODO(API 연동): 각 화면의 조회/저장 API 가 생기면 myPageService 가 이 데이터 대신 서버를 호출한다
  myPage: {
    info: {
      phone: '010-1234-5678',
      imagePath: '',
      imageUrl: '', // 업로드 미리보기용 브라우저 URL (서버 연동 시에는 imagePath 로 조회)
      birthDate: '1990-01-01',
      job: '개발자',
      nationality: 'KR',
    },
    security: {
      mfaMethod: 'OTP',
      mfaVerifiedAt: '2026-10-12 18:20',
      lastLoginAt: '2026-10-19 09:12',
      lastLoginIp: '192.168.0.12',
    },
    // Redis 세션 목록. current 는 지금 이 브라우저의 세션
    sessions: [
      { id: 's1', loginType: 'ID/PW', ip: '192.168.0.12', lastAccessAt: dayjs().format('YYYY-MM-DD HH:mm'), status: 'ACTIVE', current: true },
      { id: 's2', loginType: 'SSO', ip: '10.10.3.45', lastAccessAt: '2026-10-19 17:42', status: 'ACTIVE', current: false },
      { id: 's3', loginType: 'ID/PW + MFA', ip: '203.0.113.8', lastAccessAt: '2026-10-17 11:05', status: 'ACTIVE', current: false },
      { id: 's4', loginType: 'ID/PW', ip: '192.168.0.31', lastAccessAt: '2026-10-10 08:30', status: 'EXPIRED', current: false },
    ],
    consents: [
      { id: 'c1', title: '개인정보 수집·이용 동의', required: true, agreed: true, agreedAt: '2026-09-01 10:00', withdrawnAt: '' },
      { id: 'c2', title: '마케팅 정보 수신 동의', required: false, agreed: false, agreedAt: '2026-09-01 10:00', withdrawnAt: '2026-10-05 14:30' },
    ],
  },

  // 등록 화면 → 목록 화면으로 넘어갈 때 한 번만 보여줄 토스트 메시지
  flash: '',
})

function nextId(list) {
  return list.reduce((max, item) => Math.max(max, item.id), 0) + 1
}

function selectProject(id) {
  state.selectedProjectId = id
}

function getSelectedProject() {
  return state.myProjects.find((p) => p.id === state.selectedProjectId) || state.myProjects[0] || null
}

/** 사이드바 카운트 뱃지용 */
function counts() {
  return {
    members: state.members.length,
    documents: state.documents.length,
    tasks: state.tasks.length,
    // 이슈는 미해결(완료가 아닌) 건수만 센다
    issues: state.issues.filter((i) => i.status !== 'DONE').length,
  }
}

function setFlash(message) {
  state.flash = message
}

/** 저장된 메시지를 꺼내고 비운다 (한 번만 표시) */
function consumeFlash() {
  const message = state.flash
  state.flash = ''
  return message
}

// ---- 업무 ----
function toggleTask(id) {
  const task = state.tasks.find((t) => t.id === id)
  if (!task) return
  if (task.status === 'DONE') {
    task.status = 'IN_PROGRESS'
    task.completedAt = ''
  } else {
    task.status = 'DONE'
    task.completedAt = today()
  }
}

async function addTask(payload) {
  // TODO(API 연동): await axios.post('/api/tasks', payload)
  const created = {
    ...payload,
    id: nextId(state.tasks),
    completedAt: payload.status === 'DONE' ? today() : '',
  }
  state.tasks.unshift(created)
  return created
}

// ---- 이슈 ----
async function addIssue(payload) {
  // TODO(API 연동): await axios.post('/api/issues', payload)
  const created = { ...payload, id: nextId(state.issues), createdAt: today() }
  state.issues.unshift(created)
  return created
}

// ---- 문서 / 템플릿 ----
async function addDocument(payload) {
  // TODO(API 연동): await axios.post('/api/documents', payload)
  const created = { ...payload, id: nextId(state.documents), author: state.currentUser.loginId, createdAt: today() }
  state.documents.unshift(created)
  return created
}

async function addTemplate(payload) {
  // TODO(API 연동): await axios.post('/api/templates', payload)
  const created = { ...payload, id: nextId(state.templates), shared: payload.scope === '공용', owner: state.currentUser.loginId }
  state.templates.push(created)
  return created
}

function setTemplateShared(id, shared) {
  const tpl = state.templates.find((t) => t.id === id)
  if (tpl) tpl.shared = shared
}

// ---- 멤버 ----
async function inviteMember({ loginId, role }) {
  // TODO(API 연동): await axios.post('/api/members/invite', { loginId, role })
  const id = nextId(state.members)
  const created = {
    id,
    loginId,
    initial: loginId.charAt(0).toUpperCase(),
    role,
    desc: '신규 멤버',
    gradient: AVATAR_GRADIENTS[(id - 1) % AVATAR_GRADIENTS.length],
  }
  state.members.push(created)
  return created
}

/** roleMap: { [memberId]: role } */
async function setMemberRoles(roleMap) {
  // TODO(API 연동): await axios.put('/api/members/roles', roleMap)
  state.members.forEach((m) => {
    if (roleMap[m.id]) m.role = roleMap[m.id]
  })
}

// ---- 계정 ----
async function saveProfile(loginId, { name, email }) {
  // TODO(API 연동): await axios.put('/api/auth/me', { name, email })
  state.profileEdits[loginId] = { name, email }
}

export default {
  state,
  selectProject,
  getSelectedProject,
  counts,
  setFlash,
  consumeFlash,
  toggleTask,
  addTask,
  addIssue,
  addDocument,
  addTemplate,
  setTemplateShared,
  inviteMember,
  setMemberRoles,
  saveProfile,
}

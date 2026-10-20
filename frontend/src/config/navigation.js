/**
 * 헤더/사이드바 메뉴 정의 (한 곳에서 관리)
 * -----------------------------------------------------------
 * - to    : 이동할 경로
 * - names : 이 메뉴가 '활성'으로 보여야 하는 라우트 name 목록
 *           (경로 prefix 로 판단하면 /documents 와 /documents/new 처럼 겹치는 경우 오작동하므로 name 기준)
 * - icon  : 24x24 viewBox 의 svg path
 * - countKey : 사이드바 카운트 뱃지 (sampleStore.counts() 의 키)
 * 메뉴를 추가/변경할 때는 이 파일만 수정하면 된다.
 * -----------------------------------------------------------
 */

const ICON = {
  dashboard: 'M3 3h7v7H3z M14 3h7v7h-7z M14 14h7v7h-7z M3 14h7v7H3z',
  members: 'M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2 M9 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8z M23 21v-2a4 4 0 0 0-3-3.87 M16 3.13a4 4 0 0 1 0 7.75',
  board: 'M4 4h16v12H8l-4 4z',
  document: 'M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z M14 2v6h6 M8 13h8 M8 17h8',
  task: 'M9 11l3 3L22 4 M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11',
  issue: 'M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z M12 9v4 M12 17h.01',
  manage: 'M3 7a2 2 0 0 1 2-2h4l2 2h8a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z',
  stage: 'M12 2L2 7l10 5 10-5-10-5z M2 17l10 5 10-5 M2 12l10 5 10-5',
  overview: 'M18 20V10 M12 20V4 M6 20v-6',
}

const PROJECT_MANAGE_NAMES = ['ProjectManage', 'ProjectCreate', 'ProjectEdit']
const PHASE_NAMES = ['PhaseSetting', 'PhaseCreate', 'PhaseEdit']

/** 사이드바 '메뉴' 섹션 — children 이 있으면 펼침/접힘 그룹 */
export const sidebarMenu = [
//  { label: '대시보드', to: '/', names: ['dashboard'], icon: ICON.dashboard },
  { label: '대시보드', to: '/projects/dashboard', names: ['ProjectOverview'], icon: ICON.dashboard },
  {
    label: '게시판', icon: ICON.board,
    children: [
      { label: '게시판 목록', to: '/boards', names: ['boards', 'boards-create', 'boards-detail', 'boards-edit', 'boards-delete'] },
    ],
  },
  {
    label: '문서', icon: ICON.document, countKey: 'documents',
    children: [
//      { label: '템플릿 등록', to: '/documents/templates/new', names: ['TemplateCreate'] },
      { label: '템플릿 목록', to: '/documents/templates', names: ['TemplateList'] },
//      { label: '템플릿 공유', to: '/templates/share', names: ['TemplateShare'] },
//      { label: '문서 작성', to: '/documents/new', names: ['DocumentCreate'] },
      { label: '문서 목록', to: '/documents', names: ['DocumentList'] },
    ],
  },
  {
    label: '업무', icon: ICON.task, countKey: 'tasks',
    children: [
//      { label: '업무 등록', to: '/tasks/new', names: ['TaskCreate'] },
      { label: '업무 목록', to: '/tasks', names: ['TaskList'] },
    ],
  },
  {
    label: '리스크 및 이슈', icon: ICON.issue, countKey: 'issues', countTone: 'danger',
    children: [
//      { label: '이슈 등록', to: '/issues/new', names: ['IssueCreate'] },
      { label: '이슈 목록', to: '/issues', names: ['IssueList'] },
    ],
  },
]

/** 사이드바 '프로젝트 관리' 섹션 */
export const sidebarProjectManage = [
  { label: '관리 페이지', to: '/projects', names: PROJECT_MANAGE_NAMES, icon: ICON.manage },
  { label: '단계 설정', to: '/projects/phases', names: PHASE_NAMES, icon: ICON.stage },
//  { label: '대시보드', to: '/', names: ['dashboard'], icon: ICON.dashboard }
//  { label: '전체 대시보드', to: '/projects/dashboard', names: ['ProjectOverview'], icon: ICON.overview },
  {
    label: '멤버 설정', icon: ICON.members, countKey: 'members',
    children: [
      { label: '멤버 관리', to: '/members', names: ['MemberManage'] },
      { label: '권한 관리', to: '/members/permissions', names: ['PermissionManage'] },
    ],
  },
]

/** 헤더 상단 메뉴 (hover 시 드롭다운) */
export const headerMenu = [
//  {
//    label: '계정',
//    children: [
//      { label: '마이페이지', to: '/mypage', names: ['MyPage', 'MyProfile', 'MySecurity', 'MySessions', 'MyConsents'] },
//    ],
//  },
  {
    label: '시스템 설정',
    children: [
      { label: '공지사항', to: '', names: '' },
      { label: '자유게시판', to: '', names: '' },
    ],
  },
]

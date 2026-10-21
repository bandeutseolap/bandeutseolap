import { reactive } from 'vue'
import dayjs from 'dayjs'
import authStore from '@/store/authStore'
import { effectiveStatus, isRoleEffective, now, inRange, SIZE_RANGES, BOARD_AREAS } from '@/utils/adminMeta'

/**
 * 관리자 화면(사용자/권한 · 스토리지 · 시스템 게시판) 샘플 데이터 store
 * -----------------------------------------------------------
 * - ERD 문서의 테이블(app_user, app_user_security_info, app_user_role_map, app_auth_role,
 *   app_auth_permission, app_role_permission_map, app_file, rel_*_file, dm_project_file_usage,
 *   app_board, app_board_cont_ver, app_board_readstat_map, app_board_comment, cfg_system_hist)를
 *   화면에서 쓰기 좋은 형태로 풀어 둔 mock 이다.
 * - 규칙(마지막 ADMIN 보호, 삭제 취소 제한, 고정글 최대 개수 등)은 서버가 검증할 내용을 미리 구현해 둔 것이며,
 *   실제 API 연동 시에는 서버 응답의 오류 코드를 같은 방식(BusinessError.code)으로 화면에 전달하면 된다.
 * - TODO(API 연동): 각 함수가 axios 호출로 바뀌어도 화면은 수정이 필요 없도록 반환 형태를 맞춰 두었다.
 * -----------------------------------------------------------
 */

const MB = 1024 * 1024
const ago = (days, hours = 0) => dayjs().subtract(days, 'day').subtract(hours, 'hour').format('YYYY-MM-DD HH:mm')
const later = (days) => dayjs().add(days, 'day').format('YYYY-MM-DD HH:mm')

/** 화면에서 code 로 메시지를 고르기 위한 업무 오류 */
export class BusinessError extends Error {
  constructor(code) {
    super(code)
    this.code = code
  }
}

export const ERROR_MESSAGES = {
  LAST_ADMIN_CANNOT_BE_REVOKED: '활성 ADMIN 이 최소 1명은 남아 있어야 합니다. 마지막 ADMIN 의 권한은 회수할 수 없습니다.',
  LOGIN_ID_DUPLICATE: '이미 사용 중인 아이디입니다.',
  LOGIN_ID_INVALID: '아이디는 영문 소문자와 숫자 4~20자여야 합니다.',
  EMAIL_DUPLICATE: '이미 사용 중인 이메일입니다.',
  ROLE_CD_DUPLICATE: '이미 사용 중인 역할코드입니다.',
  ROLE_CD_INVALID: '역할코드는 영문 대문자와 숫자, 밑줄(_)만 사용할 수 있습니다.',
  SYSTEM_ROLE_PROTECTED: '시스템 기본 역할(ADMIN, USER)은 사용 중지할 수 없습니다.',
  TARGET_DELETED: '연결된 대상이 삭제된 상태라 삭제 취소할 수 없습니다.',
  FIXED_MAX_EXCEEDED: '상단 고정은 영역별 최대 개수를 넘을 수 없습니다.',
  FORBIDDEN: '권한이 없습니다.',
  NOT_FOUND: '대상을 찾을 수 없습니다.',
}

// ---- 사용자 샘플 ----
const sec = (o = {}) => ({
  pwChangedAt: ago(30), failCount: 0, lastFailAt: '', lockYn: false, lockedAt: '',
  mfaYn: false, mfaMethod: 'NONE', mfaVerifiedAt: '', ...o,
})
const extra = (o = {}) => ({ imagePath: '', birthDt: '1990-01-01', jobCd: '개발자', countryCd: 'KR', ...o })
const user = (userId, loginId, userName, email, phone, accTypeCd, accStatusCd, o = {}) => ({
  userId, loginId, userName, email, phone, accTypeCd, accStatusCd,
  createdAt: ago(200 - userId * 9), updatedAt: ago(10), lastLoginAt: ago(0, userId),
  extra: extra(), security: sec(), consent: { agreed: true, agreedAt: ago(190) }, ...o,
})

const users = [
  user(1, 'admin01', '관리자', 'admin01@bandeut.example', '010-1111-1001', 'INTERNAL', 'ACTIVE', { security: sec({ mfaYn: true, mfaMethod: 'OTP', mfaVerifiedAt: ago(3) }) }),
  user(2, 'hyjdevloper', 'hyjdevloper', 'hyjdevloper@example.com', '010-1234-5678', 'INTERNAL', 'ACTIVE', { security: sec({ mfaYn: true, mfaMethod: 'OTP', mfaVerifiedAt: ago(5) }) }),
  user(3, 'ycrdevloper', 'ycrdevloper', 'ycrdevloper@example.com', '010-2222-3003', 'INTERNAL', 'ACTIVE'),
  user(4, 'ljhdevloper', 'ljhdevloper', 'ljhdevloper@example.com', '010-3333-4004', 'INTERNAL', 'ACTIVE', { security: sec({ failCount: 1, lastFailAt: ago(1) }) }),
  user(5, 'kyrdevloper', 'kyrdevloper', 'kyrdevloper@example.com', '010-4444-5005', 'GENERAL', 'ACTIVE'),
  user(6, 'operator01', '운영자', 'operator01@bandeut.example', '010-5555-6006', 'INTERNAL', 'ACTIVE', { security: sec({ mfaYn: true, mfaMethod: 'SMS', mfaVerifiedAt: ago(2) }) }),
  user(7, 'guest_ext', '외부 협력사', 'partner@vendor.example', '010-6666-7007', 'EXTERNAL', 'ACTIVE',
    { security: sec({ failCount: 5, lastFailAt: ago(0, 3), lockYn: true, lockedAt: ago(0, 3) }) }),
  user(8, 'suspended01', '정지 계정', 'suspended01@example.com', '010-7777-8008', 'GENERAL', 'SUSPENDED'),
  user(9, 'withdrawn01', '탈퇴 계정', 'withdrawn01@example.com', '010-8888-9009', 'GENERAL', 'WITHDRAWN', { lastLoginAt: ago(60) }),
  user(10, 'newbie01', '신규 가입', 'newbie01@example.com', '010-9999-1010', 'GENERAL', 'ACTIVE',
    { lastLoginAt: '', consent: { agreed: false, agreedAt: '' }, createdAt: ago(1) }),
]

const roles = [
  { roleId: 1, roleCd: 'ADMIN', roleName: '관리자', roleDesc: '시스템 전체 관리 권한', useYn: true, system: true, createdBy: 'admin01', createdAt: ago(300), updatedBy: 'admin01', updatedAt: ago(300) },
  { roleId: 2, roleCd: 'OPERATOR', roleName: '운영자', roleDesc: '조회 및 공지 작성 권한', useYn: true, system: false, createdBy: 'admin01', createdAt: ago(250), updatedBy: 'admin01', updatedAt: ago(40) },
  { roleId: 3, roleCd: 'USER', roleName: '일반 사용자', roleDesc: '기본 사용자 권한', useYn: true, system: true, createdBy: 'admin01', createdAt: ago(300), updatedBy: 'admin01', updatedAt: ago(300) },
  { roleId: 4, roleCd: 'AUDITOR', roleName: '감사자', roleDesc: '감사 목적의 조회 전용 역할', useYn: true, system: false, createdBy: 'admin01', createdAt: ago(60), updatedBy: 'admin01', updatedAt: ago(60) },
]

// UNIQUE(user_id, role_id): 같은 (사용자, 역할) 쌍은 한 행만 두고 use_yn 으로 부여/회수를 표현한다
let mapSeq = 0
const rm = (userId, roleId, o = {}) => ({ mapId: ++mapSeq, userId, roleId, grantedBy: 1, grantedAt: ago(100), expiredAt: '', useYn: true, ...o })
const userRoleMaps = [
  rm(1, 1), rm(1, 3),
  rm(2, 1, { grantedBy: 1 }), rm(2, 3),
  rm(3, 2, { expiredAt: later(30) }), rm(3, 3),
  rm(4, 3),
  rm(5, 3), rm(5, 1, { expiredAt: ago(10), grantedAt: ago(90) }), // 만료된 ADMIN: use_yn = 1 이지만 실효하지 않음
  rm(6, 2), rm(6, 3),
  rm(7, 3), rm(8, 3), rm(9, 3, { useYn: false }), rm(10, 3),
]

// 권한: 리소스 x 동작(READ/WRITE). 시스템 공지 작성은 WRITE 만 존재
const RESOURCES = [
  { cd: 'USER', name: '사용자관리', actions: ['READ', 'WRITE'] },
  { cd: 'ROLE', name: '역할관리', actions: ['READ', 'WRITE'] },
  { cd: 'SYSTEM', name: '시스템설정', actions: ['READ', 'WRITE'] },
  { cd: 'STORAGE', name: '스토리지', actions: ['READ', 'WRITE'] },
  { cd: 'NOTICE', name: '시스템공지 작성', actions: ['WRITE'] },
]
const permissions = []
RESOURCES.forEach((r) => r.actions.forEach((a) => permissions.push({
  permissionId: permissions.length + 1, permissionCd: `ADMIN_${r.cd}_${a}`, permissionName: `${r.name} ${a === 'READ' ? '조회' : '변경'}`,
  resourceCd: r.cd, actionCd: a, useYn: true,
})))
const pid = (cd) => permissions.find((p) => p.permissionCd === cd).permissionId
// UNIQUE(role_id, permission_id): 'roleId:permissionId' 문자열 집합으로 표현
const rolePermissions = [
  ...permissions.map((p) => `1:${p.permissionId}`),
  ...['ADMIN_USER_READ', 'ADMIN_SYSTEM_READ', 'ADMIN_STORAGE_READ', 'ADMIN_NOTICE_WRITE'].map((cd) => `2:${pid(cd)}`),
]

const userSessions = {
  1: [
    { id: 'u1-s1', loginType: 'ID/PW + MFA', ip: '192.168.0.10', issuedAt: ago(0, 5), lastAccessAt: ago(0, 1), expiresAt: later(1), status: 'ACTIVE' },
  ],
  2: [
    { id: 'u2-s1', loginType: 'ID/PW + MFA', ip: '192.168.0.12', issuedAt: ago(0, 4), lastAccessAt: ago(0, 0), expiresAt: later(1), status: 'ACTIVE' },
    { id: 'u2-s2', loginType: 'SSO', ip: '10.10.3.45', issuedAt: ago(1), lastAccessAt: ago(0, 8), expiresAt: later(0), status: 'ACTIVE' },
    { id: 'u2-s3', loginType: 'ID/PW', ip: '192.168.0.31', issuedAt: ago(9), lastAccessAt: ago(8), expiresAt: ago(7), status: 'EXPIRED' },
  ],
  3: [
    { id: 'u3-s1', loginType: 'ID/PW', ip: '192.168.0.21', issuedAt: ago(0, 6), lastAccessAt: ago(0, 2), expiresAt: later(1), status: 'ACTIVE' },
  ],
}

const participations = {
  1: [{ projectName: '반듯서랍 협업 플랫폼 고도화', memberStatus: '활동', joinedAt: ago(120), projectRole: 'PM' }],
  2: [
    { projectName: '반듯서랍 협업 플랫폼 고도화', memberStatus: '활동', joinedAt: ago(110), projectRole: 'DEV' },
    { projectName: '사내 그룹웨어 이관', memberStatus: '활동', joinedAt: ago(70), projectRole: 'DEV' },
  ],
  3: [{ projectName: '반듯서랍 협업 플랫폼 고도화', memberStatus: '활동', joinedAt: ago(115), projectRole: 'PM' }],
  4: [{ projectName: '사내 그룹웨어 이관', memberStatus: '휴면', joinedAt: ago(60), projectRole: 'DEV' }],
}

// cfg_system_hist
let histSeq = 0
const hist = (targetTable, targetId, workedBy, actionType, changes, reason, workedAt = now()) =>
  ({ histId: ++histSeq, targetTable, targetId, workedAt, workedBy, actionType, changes, reason })
const history = [
  hist('app_user', 7, 'system', 'LOCK', [{ field: 'lock_yn', before: '0', after: '1' }], '로그인 5회 실패 자동 잠금', ago(0, 3)),
  hist('app_user', 3, 'admin01', 'UPDATE', [{ field: 'user_name', before: 'ycr', after: 'ycrdevloper' }], '이름 정정', ago(12)),
  hist('app_user_role_map', 3, 'admin01', 'GRANT', [{ field: 'role', before: '-', after: 'OPERATOR' }], '운영 업무 위임', ago(30)),
]

// ---- 스토리지 샘플 ----
const systemVariables = {
  PROJECT_QUOTA_MB: 128, // 프로젝트당 파일 쿼터(전역)
  ORPHAN_FILE_TTL_HOURS: 24,
  PASSWORD_EXPIRE_DAYS: 90,
  BOARD_FIXED_MAX: 3,
  DELETE_GRACE_DAYS: 7,
}

const projects = [
  { projectId: 1, name: '반듯서랍 협업 플랫폼 고도화', status: '진행중', archived: false },
  { projectId: 2, name: '사내 그룹웨어 이관', status: '진행중', archived: false },
  { projectId: 3, name: '모바일 앱 1차 출시', status: '완료', archived: false },
  { projectId: 4, name: 'AI 보고서 자동화', status: '진행중', archived: false },
  { projectId: 5, name: '레거시 CRM 전환', status: '완료', archived: true },
  { projectId: 6, name: '신규 영업관리', status: '보류', archived: false },
]

const MIME = {
  docx: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', pdf: 'application/pdf', png: 'image/png',
  xlsx: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', zip: 'application/zip', svg: 'image/svg+xml',
  csv: 'text/csv', tmp: 'application/octet-stream', log: 'text/plain',
}
let fileSeq = 0
const link = (targetType, targetId, targetName, projectId, o = {}) => ({
  targetType, targetId, targetName, projectId, fileRoleCd: 'ATTACH', attachedBy: 'ycrdevloper', attachedAt: ago(20), targetDeleted: false, ...o,
})
const file = (name, size, statusCd, uploadedAgo, uploader, links, o = {}) => {
  const id = ++fileSeq
  const ext = name.split('.').pop().toLowerCase()
  return {
    fileId: id, originFileName: name, fileExt: ext, fileSize: Math.round(size), mimeType: MIME[ext] || 'application/octet-stream',
    fileStatusCd: statusCd, storageKey: `bandeut/${dayjs().format('YYYY')}/${String(id).padStart(4, '0')}/${id}a9c3e1`,
    storedFileName: `f_${id}_${(id * 7919).toString(16)}.${ext}`, fileHash: `h${(id * 104729).toString(16)}`,
    uploadedBy: uploader, uploadedAt: ago(...uploadedAgo), deleteScheduledAt: '', physicalDeletedAt: '', links, ...o,
  }
}
const files = [
  file('요구사항정의서_v3.docx', 2.4 * MB, 'ACTIVE', [20], 'ycrdevloper', [link('DOCUMENT', 11, '요구사항정의서', 1)], { fileHash: 'hDUP-A' }),
  file('아키텍처다이어그램.png', 0.86 * MB, 'ACTIVE', [18], 'ycrdevloper', [link('PROJECT', 1, '반듯서랍 협업 플랫폼 고도화', 1, { fileRoleCd: 'COVER' })]),
  file('화면설계서.pdf', 12.5 * MB, 'ACTIVE', [15], 'hyjdevloper', [link('DOCUMENT', 12, '화면설계서', 1)], { fileHash: 'hDUP-B' }),
  file('ERD_v2.png', 1.2 * MB, 'ACTIVE', [14], 'ljhdevloper', [link('BOARD', 31, 'ERD 공유드립니다', 1)]),
  file('회의록_1018.pdf', 0.34 * MB, 'ACTIVE', [3], 'ycrdevloper', [link('DOCUMENT', 21, '10월 3주차 회의록', 2)]),
  file('공지_점검안내.pdf', 0.41 * MB, 'ACTIVE', [2], 'admin01', [link('SYS_BOARD', 101, '시스템 정기 점검 안내', null, { attachedBy: 'admin01' })]),
  file('시스템가이드.zip', 48 * MB, 'ACTIVE', [3], 'admin01', [link('SYS_BOARD', 103, '신규 기능 업데이트 안내', null, { attachedBy: 'admin01' })]),
  file('샘플데이터.xlsx', 3.1 * MB, 'ACTIVE', [40], 'kyrdevloper', [link('DOCUMENT', 41, '모바일 샘플 데이터', 3)]),
  file('로고.svg', 0.024 * MB, 'ACTIVE', [90], 'kyrdevloper', [link('PROJECT', 3, '모바일 앱 1차 출시', 3, { fileRoleCd: 'COVER' })]),
  file('old_report.pdf', 5.4 * MB, 'DELETE_SCHEDULED', [30], 'ycrdevloper', [link('DOCUMENT', 22, '구 보고서', 2, { targetDeleted: true })], { deleteScheduledAt: later(3) }),
  file('temp_export.csv', 0.9 * MB, 'DELETE_SCHEDULED', [28], 'ljhdevloper', [link('BOARD', 32, '임시 내보내기', 1, { targetDeleted: true })], { deleteScheduledAt: later(6) }),
  file('backup_2025.zip', 120 * MB, 'DELETE_SCHEDULED', [60], 'admin01', [link('PROJECT', 5, '레거시 CRM 전환', 5)], { deleteScheduledAt: later(1) }),
  file('upload_fail.tmp', 15 * MB, 'UPLOADING', [1, 6], 'hyjdevloper', []),
  file('upload_ok.tmp', 2 * MB, 'UPLOADING', [0, 1], 'hyjdevloper', []),
  file('unlinked_image.png', 0.7 * MB, 'ACTIVE', [12], 'ljhdevloper', []),
  file('요구사항정의서_복사본.docx', 2.4 * MB, 'ACTIVE', [19], 'ljhdevloper', [link('DOCUMENT', 13, '요구사항정의서 사본', 1)], { fileHash: 'hDUP-A' }),
  file('화면설계서_복사본.pdf', 12.5 * MB, 'ACTIVE', [10], 'ljhdevloper', [link('DOCUMENT', 14, '화면설계서 사본', 2)], { fileHash: 'hDUP-B' }),
  file('deleted_old.log', 1 * MB, 'DELETED', [50], 'admin01', [], { physicalDeletedAt: ago(5) }),
  file('weekly_report.docx', 1.1 * MB, 'ACTIVE', [25], 'hyjdevloper', [link('DOCUMENT', 23, '주간 보고(삭제됨)', 1, { targetDeleted: true })]),
  file('스프린트회고.pdf', 0.6 * MB, 'ACTIVE', [8], 'ycrdevloper', [link('BOARD', 33, '스프린트 회고', 2)]),
  file('설계자료_원본.zip', 85 * MB, 'ACTIVE', [22], 'hyjdevloper', [link('PROJECT', 1, '반듯서랍 협업 플랫폼 고도화', 1, { fileRoleCd: 'REF' })]),
]

/** 프로젝트 소속(ACTIVE 파일만): 링크의 project_id 기준으로 파일을 한 번만 센다 */
function projectIdOfFile(f) {
  const l = f.links.find((x) => x.projectId)
  return l ? l.projectId : null
}
// dm_project_file_usage — 초기에는 파일 기준 집계와 맞추되, 2번 프로젝트는 일부러 어긋나 있어 '재집계'를 시연할 수 있다
const dmUsage = projects.map((p) => {
  const mine = files.filter((f) => f.fileStatusCd === 'ACTIVE' && projectIdOfFile(f) === p.projectId)
  return { projectId: p.projectId, activeFileCount: mine.length, activeFileTotalSize: mine.reduce((s, f) => s + f.fileSize, 0), updatedAt: ago(1) }
})
dmUsage[1].activeFileCount += 2
dmUsage[1].activeFileTotalSize += 7 * MB

// 일 단위 업로드 추이 (dm_storage_daily 가 있다고 가정한 샘플)
const uploadTrend = Array.from({ length: 30 }, (_, i) => {
  const count = ((i * 7) % 9) + 1 + (i % 6 === 0 ? 4 : 0)
  return { date: dayjs().subtract(29 - i, 'day').format('MM-DD'), count, size: count * (3 + (i % 5)) * MB }
})

const deleteBatch = { lastRunAt: ago(0, 4), processed: 12, failed: 1 }

// ---- 시스템 게시판 샘플 ----
const board = (boardId, areaCd, title, writerId, writtenAgo, o = {}) => ({
  boardId, areaCd, title, writtenBy: writerId, writtenAt: ago(...[].concat(writtenAgo)), updatedBy: writerId, updatedAt: ago(...[].concat(writtenAgo)),
  fixedTopYn: false, noticeYn: false, noticedAt: '', visibleYn: true, postStatusCd: 'PUBLISHED', openTargetCd: 'ALL', currentVersion: 1, ...o,
})
const boards = [
  board(101, 'SYS_NOTICE', '시스템 정기 점검 안내', 1, 2, { fixedTopYn: true, noticeYn: true, noticedAt: ago(2) }),
  board(102, 'SYS_NOTICE', '개인정보 처리방침 개정', 1, 19, { fixedTopYn: true, noticeYn: true, noticedAt: ago(19), currentVersion: 2, updatedAt: ago(18), updatedBy: 1 }),
  board(103, 'SYS_NOTICE', '신규 기능 업데이트 안내', 6, 3, { currentVersion: 3, updatedAt: ago(1), updatedBy: 6, noticeYn: true, noticedAt: ago(3) }),
  board(104, 'SYS_NOTICE', '10월 프로젝트 마감 일정', 6, 10),
  board(105, 'SYS_NOTICE', '[운영] 장애 대응 절차', 1, 8, { openTargetCd: 'ADMIN' }),
  board(106, 'SYS_NOTICE', '로그인 사용자 전용 이용 안내', 1, 9, { openTargetCd: 'LOGIN' }),
  board(201, 'SYS_FREE', '점심 맛집 추천해주세요', 4, 1),
  board(202, 'SYS_FREE', '개발 팁 공유: Vite 빌드 속도', 3, 4, { fixedTopYn: true, openTargetCd: 'LOGIN' }),
  board(203, 'SYS_FREE', '부적절한 내용의 게시글', 5, 6, { visibleYn: false }),
  board(204, 'SYS_FREE', '(임시저장) 사내 스터디 모집', 2, 0, { postStatusCd: 'TEMP' }),
]
for (let i = 0; i < 12; i++) boards.push(board(110 + i, 'SYS_NOTICE', `정기 공지 ${12 - i}월호`, i % 2 ? 6 : 1, 20 + i * 4))
for (let i = 0; i < 6; i++) boards.push(board(210 + i, 'SYS_FREE', `자유게시판 이야기 #${i + 1}`, 3 + (i % 3), 5 + i * 3, { openTargetCd: 'LOGIN' }))

const contents = {
  101: ['■ 점검 일시: 매월 둘째 주 일요일 02:00 ~ 04:00', '■ 영향 범위: 전체 서비스 일시 중단', '■ 문의: 운영팀'],
  102: ['■ 개정 사유: 관련 법령 개정 반영', '■ 시행일: 2026-11-01', '■ 주요 변경: 수집 항목 및 보유 기간 명확화'],
  103: ['■ 신규 기능', '- 마이페이지: 보안 설정, 세션 관리', '- 캘린더 대시보드', '- 파일 탐색기'],
}
const versions = []
// createdAt: 숫자(며칠 전)/[일, 시간] 또는 이미 만들어 둔 'YYYY-MM-DD HH:mm' 문자열
const addVer = (boardId, version, content, createdBy, createdAt, recoveredVersion = null) =>
  versions.push({ boardId, version, content, createdBy, createdAt: typeof createdAt === 'string' ? createdAt : ago(...[].concat(createdAt)), recoveredVersion })
boards.forEach((b) => {
  if (b.boardId === 102) {
    addVer(102, 1, ['■ 개정 사유: 법령 개정', '■ 시행일: 2026-10-01'].join('\n'), 1, 19)
    addVer(102, 2, contents[102].join('\n'), 1, 18)
  } else if (b.boardId === 103) {
    addVer(103, 1, ['■ 신규 기능', '- 마이페이지', '- 캘린더 대시보드'].join('\n'), 6, 3)
    addVer(103, 2, ['■ 신규 기능', '- 마이페이지: 보안 설정', '- 캘린더 대시보드', '- 파일 탐색기 (예정)'].join('\n'), 6, 2)
    addVer(103, 3, contents[103].join('\n'), 6, 1, 1) // v1 을 바탕으로 복원 후 수정한 버전
  } else {
    addVer(b.boardId, 1, (contents[b.boardId] || [`${b.title} 내용입니다.`, '자세한 사항은 문의 바랍니다.']).join('\n'), b.writtenBy, b.writtenAt)
  }
})
// 현재 로그인 사용자(hyjdevloper = 2) 기준 읽음 기록: 101 은 최신본을 읽음, 103 은 v1 만 읽음 → '수정됨'
const readStats = [
  { userId: 2, boardId: 101, firstReadAt: ago(2), lastReadVer: 1, lastReadAt: ago(1) },
  { userId: 2, boardId: 103, firstReadAt: ago(3), lastReadVer: 1, lastReadAt: ago(3) },
  { userId: 2, boardId: 201, firstReadAt: ago(1), lastReadVer: 1, lastReadAt: ago(1) },
]
const comments = [
  { commentId: 1, boardId: 201, parentId: null, content: '회사 앞 국밥집 추천합니다.', visibleYn: true, writtenBy: 3, writtenAt: ago(0, 20) },
  { commentId: 2, boardId: 201, parentId: 1, content: '저도 좋아해요!', visibleYn: true, writtenBy: 2, writtenAt: ago(0, 18) },
  { commentId: 3, boardId: 201, parentId: null, content: '부적절한 댓글(블라인드)', visibleYn: false, writtenBy: 5, writtenAt: ago(0, 10) },
]
// 시스템 게시판 첨부(app_file 의 SYS_BOARD 링크)는 files 에서 조회한다

const state = reactive({ users, roles, userRoleMaps, permissions, resources: RESOURCES, rolePermissions, userSessions, participations, history,
  systemVariables, projects, files, dmUsage, uploadTrend, deleteBatch, boards, versions, readStats, comments })

// =========================================================
// 공통 헬퍼
// =========================================================
function nextId(list, key) { return list.reduce((m, x) => Math.max(m, x[key]), 0) + 1 }
const byId = (userId) => state.users.find((u) => u.userId === userId)
const userName = (userId) => (byId(userId) ? byId(userId).loginId : String(userId))

/** 현재 로그인 사용자 (샘플 사용자에 없으면 일반 사용자 취급) */
function currentUser() {
  return state.users.find((u) => u.loginId === authStore.state.loginId) || null
}
function currentLoginId() { return authStore.state.loginId || 'hyjdevloper' }
/** 로그인 정보가 없으면 샘플 기본 사용자(hyjdevloper)로 본다 — 라우트 가드가 생기면 제거 */
function actor() { return currentUser() || state.users.find((u) => u.loginId === 'hyjdevloper') }

function log(targetTable, targetId, actionType, changes, reason = '') {
  state.history.unshift(hist(targetTable, targetId, currentLoginId(), actionType, changes, reason))
}

// =========================================================
// 사용자/권한
// =========================================================
function effectiveRoles(userId) {
  return state.userRoleMaps
    .filter((m) => m.userId === userId && isRoleEffective(m))
    .map((m) => ({ map: m, role: state.roles.find((r) => r.roleId === m.roleId) }))
    .filter((x) => x.role)
}
function roleCdsOf(userId) { return effectiveRoles(userId).map((x) => x.role.roleCd) }
function hasPermission(permissionCd, userId = actor().userId) {
  const p = state.permissions.find((x) => x.permissionCd === permissionCd)
  if (!p) return false
  return effectiveRoles(userId).some((x) => state.rolePermissions.includes(`${x.role.roleId}:${p.permissionId}`))
}
const isAdmin = () => roleCdsOf(actor().userId).includes('ADMIN')
const canWriteNotice = () => hasPermission('ADMIN_NOTICE_WRITE')

function userRow(u) {
  return { ...u, roles: effectiveRoles(u.userId).map((x) => x.role), effective: effectiveStatus(u) }
}

function searchUsers(f = {}) {
  return state.users.map(userRow).filter((u) => {
    if (f.loginId && !u.loginId.includes(f.loginId.trim())) return false
    if (f.userName && !u.userName.includes(f.userName.trim())) return false
    if (f.email && !u.email.includes(f.email.trim())) return false
    if (f.accStatusCd && u.accStatusCd !== f.accStatusCd) return false
    if (f.accTypeCd && u.accTypeCd !== f.accTypeCd) return false
    if (f.lockYn === '1' && !u.security.lockYn) return false
    if (f.lockYn === '0' && u.security.lockYn) return false
    if (f.roleId && !u.roles.some((r) => r.roleId === Number(f.roleId))) return false
    if ((f.lastLoginFrom || f.lastLoginTo) && !inRange(u.lastLoginAt, f.lastLoginFrom, f.lastLoginTo)) return false
    if ((f.createdFrom || f.createdTo) && !inRange(u.createdAt, f.createdFrom, f.createdTo)) return false
    return true
  })
}

function getUser(userId) {
  const u = byId(Number(userId))
  return u ? userRow(u) : null
}

function diff(before, after, keys) {
  return keys.filter((k) => String(before[k] ?? '') !== String(after[k] ?? '')).map((k) => ({ field: k, before: String(before[k] ?? ''), after: String(after[k] ?? '') }))
}

async function updateUserBasic(userId, patch, reason = '') {
  const u = byId(userId)
  if (state.users.some((x) => x.userId !== userId && x.email === patch.email)) throw new BusinessError('EMAIL_DUPLICATE')
  const keys = ['userName', 'email', 'phone', 'accTypeCd', 'accStatusCd']
  const changes = diff(u, patch, keys)
  Object.assign(u, Object.fromEntries(keys.map((k) => [k, patch[k]])), { updatedAt: now() })
  if (changes.length) log('app_user', userId, 'UPDATE', changes, reason)
}

async function updateUserExtra(userId, patch) {
  const u = byId(userId)
  const changes = diff(u.extra, patch, ['birthDt', 'jobCd', 'countryCd', 'imagePath'])
  Object.assign(u.extra, patch)
  if (changes.length) log('app_user', userId, 'UPDATE', changes)
}

async function createUser({ loginId, userName: name, email, phone, accTypeCd, roleId }) {
  if (!/^[a-z0-9]{4,20}$/.test(loginId)) throw new BusinessError('LOGIN_ID_INVALID')
  if (state.users.some((u) => u.loginId === loginId)) throw new BusinessError('LOGIN_ID_DUPLICATE')
  if (state.users.some((u) => u.email === email)) throw new BusinessError('EMAIL_DUPLICATE')
  const userId = nextId(state.users, 'userId')
  state.users.push({
    userId, loginId, userName: name, email, phone, accTypeCd, accStatusCd: 'ACTIVE', createdAt: now(), updatedAt: now(), lastLoginAt: '',
    extra: extra(), security: sec({ pwChangedAt: '' }), consent: { agreed: false, agreedAt: '' },
  })
  state.userRoleMaps.push({ mapId: nextId(state.userRoleMaps, 'mapId'), userId, roleId, grantedBy: actor().userId, grantedAt: now(), expiredAt: '', useYn: true })
  log('app_user', userId, 'CREATE', [{ field: 'login_id', before: '-', after: loginId }], '관리자 생성 · 초기 비밀번호 설정 링크 메일 발송')
  return byId(userId)
}

async function unlockUsers(userIds) {
  userIds.forEach((id) => {
    const u = byId(id)
    if (!u || !u.security.lockYn) return
    u.security.lockYn = false; u.security.failCount = 0; u.security.lockedAt = ''
    log('app_user', id, 'UNLOCK', [{ field: 'lock_yn', before: '1', after: '0' }, { field: 'login_fail_count', before: '5', after: '0' }])
  })
}
async function suspendUsers(userIds, reason = '') {
  userIds.forEach((id) => {
    const u = byId(id)
    if (!u || u.accStatusCd !== 'ACTIVE') return
    u.accStatusCd = 'SUSPENDED'; u.updatedAt = now()
    log('app_user', id, 'UPDATE', [{ field: 'acc_status_cd', before: 'ACTIVE', after: 'SUSPENDED' }], reason)
  })
}
async function resetPassword(userId) {
  // 임시 비밀번호는 화면에 노출하지 않고 재설정 링크를 메일로만 보낸다
  log('app_user', userId, 'PASSWORD_RESET', [], '비밀번호 재설정 링크 메일 발송')
}
async function resetMfa(userId) {
  const s = byId(userId).security
  log('app_user', userId, 'MFA_RESET', [{ field: 'mfa_method', before: s.mfaMethod, after: 'NONE' }])
  Object.assign(s, { mfaYn: false, mfaMethod: 'NONE', mfaVerifiedAt: '' })
}

// ---- 역할 부여/회수 ----
/** 실효 ADMIN 을 가진 활성 사용자 수 */
function activeAdminIds() {
  const admin = state.roles.find((r) => r.roleCd === 'ADMIN')
  return new Set(
    state.userRoleMaps
      .filter((m) => m.roleId === admin.roleId && isRoleEffective(m) && byId(m.userId).accStatusCd === 'ACTIVE')
      .map((m) => m.userId),
  )
}

async function grantRole(userId, roleId, expiredAt = '') {
  const role = state.roles.find((r) => r.roleId === roleId)
  let map = state.userRoleMaps.find((m) => m.userId === userId && m.roleId === roleId)
  if (map) Object.assign(map, { useYn: true, grantedBy: actor().userId, grantedAt: now(), expiredAt })
  else { map = { mapId: nextId(state.userRoleMaps, 'mapId'), userId, roleId, grantedBy: actor().userId, grantedAt: now(), expiredAt, useYn: true }; state.userRoleMaps.push(map) }
  log('app_user_role_map', userId, 'GRANT', [{ field: 'role', before: '-', after: role.roleCd }], expiredAt ? `만료 ${expiredAt}` : '')
}
async function bulkGrant(userIds, roleId, expiredAt = '') {
  for (const id of userIds) await grantRole(id, roleId, expiredAt)
  return userIds.length
}

/** 회수 시 활성 ADMIN 이 0명이 되는지 사전 검증 (서버에서는 비관적 락 + 트랜잭션으로 처리) */
function assertAdminRemains(userIds, roleId) {
  const role = state.roles.find((r) => r.roleId === roleId)
  if (!role || role.roleCd !== 'ADMIN') return
  const remaining = new Set(activeAdminIds())
  userIds.forEach((id) => remaining.delete(id))
  if (remaining.size === 0) throw new BusinessError('LAST_ADMIN_CANNOT_BE_REVOKED')
}
async function revokeRoles(userIds, roleId) {
  assertAdminRemains(userIds, roleId)
  const role = state.roles.find((r) => r.roleId === roleId)
  userIds.forEach((userId) => {
    const map = state.userRoleMaps.find((m) => m.userId === userId && m.roleId === roleId && m.useYn)
    if (!map) return
    map.useYn = false // 물리 삭제 대신 use_yn = 0 (부여/회수 이력 보존)
    log('app_user_role_map', userId, 'REVOKE', [{ field: 'role', before: role.roleCd, after: '-' }])
  })
}
const revokeRole = (userId, roleId) => revokeRoles([userId], roleId)

async function extendRole(userId, roleId, expiredAt) {
  const map = state.userRoleMaps.find((m) => m.userId === userId && m.roleId === roleId)
  if (!map) throw new BusinessError('NOT_FOUND')
  const before = map.expiredAt || '무기한'
  map.expiredAt = expiredAt
  log('app_user_role_map', userId, 'EXTEND', [{ field: 'expired_at', before, after: expiredAt || '무기한' }])
}

/** 사용자의 역할 매핑 전체(회수/만료 포함) — 사용자 상세 '시스템 역할' 탭 */
function roleMapsOf(userId) {
  return state.userRoleMaps.filter((m) => m.userId === userId).map((m) => ({
    ...m, role: state.roles.find((r) => r.roleId === m.roleId), grantedByName: userName(m.grantedBy), effective: isRoleEffective(m),
  }))
}
/** 역할별 사용자 — 부여/회수 이력까지 포함한 매핑 */
function usersOfRole(roleId, { onlyEffective = false } = {}) {
  return state.userRoleMaps.filter((m) => m.roleId === roleId && m.useYn).map((m) => ({
    ...m, user: byId(m.userId), grantedByName: userName(m.grantedBy), effective: isRoleEffective(m),
  })).filter((x) => !onlyEffective || x.effective)
}

// ---- 역할 / 권한 ----
function roleHolderCount(roleId) {
  return state.userRoleMaps.filter((m) => m.roleId === roleId && isRoleEffective(m)).length
}
async function createRole({ roleCd, roleName, roleDesc, useYn }) {
  if (!/^[A-Z][A-Z0-9_]*$/.test(roleCd)) throw new BusinessError('ROLE_CD_INVALID')
  if (state.roles.some((r) => r.roleCd === roleCd)) throw new BusinessError('ROLE_CD_DUPLICATE')
  const role = { roleId: nextId(state.roles, 'roleId'), roleCd, roleName, roleDesc, useYn, system: false, createdBy: currentLoginId(), createdAt: now(), updatedBy: currentLoginId(), updatedAt: now() }
  state.roles.push(role)
  log('app_auth_role', role.roleId, 'CREATE', [{ field: 'role_cd', before: '-', after: roleCd }])
}
async function updateRole(roleId, { roleName, roleDesc, useYn }) {
  const role = state.roles.find((r) => r.roleId === roleId)
  if (role.system && !useYn) throw new BusinessError('SYSTEM_ROLE_PROTECTED') // ADMIN/USER 는 사용 중지 불가
  const changes = diff(role, { roleName, roleDesc, useYn: useYn ? 1 : 0 }, ['roleName', 'roleDesc']).concat(role.useYn !== useYn ? [{ field: 'use_yn', before: role.useYn ? '1' : '0', after: useYn ? '1' : '0' }] : [])
  Object.assign(role, { roleName, roleDesc, useYn, updatedBy: currentLoginId(), updatedAt: now() })
  if (changes.length) log('app_auth_role', roleId, 'UPDATE', changes)
}
async function setRolePermissions(roleId, permissionIds) {
  const keep = state.rolePermissions.filter((k) => !k.startsWith(`${roleId}:`))
  state.rolePermissions.splice(0, state.rolePermissions.length, ...keep, ...permissionIds.map((id) => `${roleId}:${id}`))
  log('app_role_permission_map', roleId, 'UPDATE', [{ field: 'permissions', before: '', after: `${permissionIds.length}건` }])
}

// ---- 세션 / 이력 ----
const sessionsOf = (userId) => state.userSessions[userId] || []
async function revokeUserSession(userId, sessionId) {
  state.userSessions[userId] = sessionsOf(userId).filter((s) => s.id !== sessionId)
  log('app_user', userId, 'FORCE_LOGOUT', [], `세션 ${sessionId} 강제 로그아웃`)
}
async function revokeAllUserSessions(userId) {
  state.userSessions[userId] = []
  log('app_user', userId, 'FORCE_LOGOUT', [], '전체 세션 강제 로그아웃')
}
const historyOf = (userId) => state.history.filter((h) => ['app_user', 'app_user_role_map'].includes(h.targetTable) && h.targetId === userId)
const participationsOf = (userId) => state.participations[userId] || []
async function logExport(count, reason) {
  log('app_user', 0, 'EXPORT', [{ field: 'rows', before: '-', after: `${count}건` }], reason)
}

// =========================================================
// 스토리지
// =========================================================
const quotaBytes = () => state.systemVariables.PROJECT_QUOTA_MB * MB
const liveFiles = () => state.files.filter((f) => f.fileStatusCd !== 'DELETED')
const primaryTarget = (f) => (f.links[0] ? f.links[0].targetType : null)

function storageSummary() {
  const by = (st) => state.files.filter((f) => f.fileStatusCd === st)
  const sum = (arr) => arr.reduce((s, f) => s + f.fileSize, 0)
  const live = liveFiles()
  return {
    totalCount: live.length, totalSize: sum(live),
    activeSize: sum(by('ACTIVE')), scheduledSize: sum(by('DELETE_SCHEDULED')), uploadingCount: by('UPLOADING').length,
  }
}
/** 영역별 사용량 — 파일의 첫 연결 대상 기준, 연결이 없으면 '연결 없음' */
function usageByArea() {
  const keys = ['PROJECT', 'DOCUMENT', 'BOARD', 'SYS_BOARD', 'NONE']
  const out = Object.fromEntries(keys.map((k) => [k, { count: 0, size: 0 }]))
  liveFiles().forEach((f) => {
    const k = primaryTarget(f) || 'NONE'
    out[k].count += 1; out[k].size += f.fileSize
  })
  return out
}
function extDistribution() {
  const map = {}
  liveFiles().forEach((f) => {
    const e = map[f.fileExt] || (map[f.fileExt] = { ext: f.fileExt, count: 0, size: 0 })
    e.count += 1; e.size += f.fileSize
  })
  return Object.values(map).sort((a, b) => b.size - a.size)
}
function projectUsageRows(f = {}) {
  return state.projects.map((p) => {
    const dm = state.dmUsage.find((d) => d.projectId === p.projectId)
    const percent = quotaBytes() ? Math.round((dm.activeFileTotalSize / quotaBytes()) * 1000) / 10 : 0
    return { ...p, ...dm, quota: quotaBytes(), percent }
  }).filter((r) => {
    if (f.name && !r.name.includes(f.name.trim())) return false
    if (f.status && r.status !== f.status) return false
    if (f.archived === '1' && !r.archived) return false
    if (f.archived === '0' && r.archived) return false
    if (f.percentMin && r.percent < Number(f.percentMin)) return false
    return true
  })
}
function topProjects(n = 10) {
  return projectUsageRows().sort((a, b) => b.activeFileTotalSize - a.activeFileTotalSize).slice(0, n)
}
/** 선택한 프로젝트를 app_file 기준으로 다시 집계해 dm 테이블과의 오차를 보정한다 */
async function recountProjects(projectIds) {
  const result = []
  projectIds.forEach((projectId) => {
    const mine = state.files.filter((f) => f.fileStatusCd === 'ACTIVE' && projectIdOfFile(f) === projectId)
    const dm = state.dmUsage.find((d) => d.projectId === projectId)
    result.push({ projectId, beforeCount: dm.activeFileCount, afterCount: mine.length, beforeSize: dm.activeFileTotalSize, afterSize: mine.reduce((s, f) => s + f.fileSize, 0) })
    dm.activeFileCount = mine.length
    dm.activeFileTotalSize = mine.reduce((s, f) => s + f.fileSize, 0)
    dm.updatedAt = now()
  })
  return result
}

function fileRow(f) {
  const l = f.links[0]
  return {
    ...f,
    targetLabel: l ? `[${l.targetType}] ${l.targetName}` : '연결 없음',
    targetType: l ? l.targetType : '',
    fileRoleCd: l ? l.fileRoleCd : '',
    projectId: projectIdOfFile(f),
    daysLeft: f.deleteScheduledAt ? Math.ceil(dayjs(f.deleteScheduledAt).diff(dayjs(), 'hour') / 24) : null,
  }
}
function searchFiles(q = {}) {
  const range = SIZE_RANGES.find((r) => r.code === q.sizeRange)
  return state.files.map(fileRow).filter((f) => {
    if (q.name && !f.originFileName.toLowerCase().includes(q.name.trim().toLowerCase())) return false
    if (q.ext && f.fileExt !== q.ext.trim().toLowerCase().replace('.', '')) return false
    if (q.mime && f.mimeType !== q.mime) return false
    if (q.statusCd && f.fileStatusCd !== q.statusCd) return false
    if (q.targetType === 'NONE' ? f.links.length > 0 : q.targetType && !f.links.some((l) => l.targetType === q.targetType)) return false
    if (q.projectId && f.projectId !== Number(q.projectId)) return false
    if (q.uploader && !f.uploadedBy.includes(q.uploader.trim())) return false
    if ((q.from || q.to) && !inRange(f.uploadedAt, q.from, q.to)) return false
    if (range && !(f.fileSize >= range.min && f.fileSize < range.max)) return false
    return true
  }).sort((a, b) => b.uploadedAt.localeCompare(a.uploadedAt))
}
async function logFileDownload(fileId) {
  log('app_file', fileId, 'DOWNLOAD', [], '관리자 다운로드')
}
async function scheduleDelete(fileIds) {
  let n = 0
  fileIds.forEach((id) => {
    const f = state.files.find((x) => x.fileId === id)
    if (!f || !['ACTIVE', 'UPLOADING'].includes(f.fileStatusCd)) return
    log('app_file', id, 'DELETE_SCHEDULE', [{ field: 'file_status_cd', before: f.fileStatusCd, after: 'DELETE_SCHEDULED' }])
    f.fileStatusCd = 'DELETE_SCHEDULED'
    f.deleteScheduledAt = later(state.systemVariables.DELETE_GRACE_DAYS)
    n += 1
  })
  return n
}
async function cancelDelete(fileId) {
  const f = state.files.find((x) => x.fileId === fileId)
  if (f.links.some((l) => l.targetDeleted)) throw new BusinessError('TARGET_DELETED') // 대상이 Soft Delete 상태면 복원 불가
  f.fileStatusCd = 'ACTIVE'; f.deleteScheduledAt = ''
  log('app_file', fileId, 'DELETE_CANCEL', [{ field: 'file_status_cd', before: 'DELETE_SCHEDULED', after: 'ACTIVE' }])
}
async function purgeFile(fileId, reason) {
  const f = state.files.find((x) => x.fileId === fileId)
  f.fileStatusCd = 'DELETED'; f.physicalDeletedAt = now(); f.deleteScheduledAt = ''
  log('app_file', fileId, 'PURGE', [{ field: 'file_status_cd', before: 'DELETE_SCHEDULED', after: 'DELETED' }], reason)
}

function integrityReport() {
  const ttl = state.systemVariables.ORPHAN_FILE_TTL_HOURS
  const orphans = state.files.filter((f) =>
    (f.fileStatusCd === 'UPLOADING' && dayjs().diff(dayjs(f.uploadedAt), 'hour') >= ttl) ||
    (f.fileStatusCd === 'ACTIVE' && f.links.length === 0),
  ).map((f) => ({ ...f, elapsedHours: dayjs().diff(dayjs(f.uploadedAt), 'hour') }))
  const dangling = state.files.filter((f) => f.fileStatusCd === 'ACTIVE' && f.links.some((l) => l.targetDeleted))
  const groups = {}
  liveFiles().forEach((f) => { (groups[f.fileHash] = groups[f.fileHash] || []).push(f) })
  const duplicates = Object.entries(groups).filter(([, list]) => list.length > 1).map(([hash, list]) => ({
    hash, count: list.length, wastedSize: list.slice(1).reduce((s, f) => s + f.fileSize, 0), files: list,
  }))
  return { orphans, dangling, duplicates }
}

// =========================================================
// 시스템 게시판
// =========================================================
function allowedTargets() {
  const t = ['ALL']
  if (authStore.state.loggedIn) t.push('LOGIN')
  if (isAdmin()) t.push('ADMIN')
  return t
}
function readStatusOf(b) {
  const r = state.readStats.find((x) => x.userId === actor().userId && x.boardId === b.boardId)
  if (!r) return 'NEW'
  return r.lastReadVer < b.currentVersion ? 'UPDATED' : 'READ'
}
function currentContent(boardId) {
  const b = state.boards.find((x) => x.boardId === boardId)
  const v = state.versions.find((x) => x.boardId === boardId && x.version === b.currentVersion)
  return v ? v.content : ''
}
const attachmentsOf = (boardId) => state.files.filter((f) => f.fileStatusCd !== 'DELETED' && f.links.some((l) => l.targetType === 'SYS_BOARD' && l.targetId === boardId))
const commentCountOf = (boardId) => state.comments.filter((c) => c.boardId === boardId && c.visibleYn).length

function boardRow(b) {
  return { ...b, writerName: userName(b.writtenBy), updaterName: userName(b.updatedBy), readStatus: readStatusOf(b), hasFile: attachmentsOf(b.boardId).length > 0, commentCount: commentCountOf(b.boardId) }
}
function boardVisible(b) {
  if (!allowedTargets().includes(b.openTargetCd)) return false
  if (!b.visibleYn && !isAdmin()) return false // 블라인드는 관리자만 본다
  return true
}
/**
 * 목록: 고정글은 페이지와 무관하게 별도 조회, 일반글만 페이지네이션.
 * q: { areaKey: 'notice'|'free', scope: 'title'|'titleContent'|'writer', keyword, from, to, page, size, tempOnly }
 */
function listBoards(q) {
  const area = BOARD_AREAS[q.areaKey].code
  const kw = (q.keyword || '').trim()
  const match = (b) => {
    if (!kw && !q.from && !q.to) return true
    if ((q.from || q.to) && !inRange(b.writtenAt, q.from, q.to)) return false
    if (!kw) return true
    if (q.scope === 'writer') return userName(b.writtenBy).includes(kw)
    if (b.title.includes(kw)) return true
    return q.scope === 'titleContent' && currentContent(b.boardId).includes(kw) // 본문 검색은 현재 버전 기준
  }
  if (q.tempOnly) {
    const mine = state.boards.filter((b) => b.areaCd === area && b.postStatusCd === 'TEMP' && b.writtenBy === actor().userId)
    return { fixed: [], items: mine.map(boardRow), total: mine.length }
  }
  const base = state.boards.filter((b) => b.areaCd === area && b.postStatusCd === 'PUBLISHED' && boardVisible(b) && match(b))
  const fixed = base.filter((b) => b.fixedTopYn).sort((a, b) => b.writtenAt.localeCompare(a.writtenAt)).map(boardRow)
  const normal = base.filter((b) => !b.fixedTopYn).sort((a, b) => b.writtenAt.localeCompare(a.writtenAt) || b.boardId - a.boardId)
  const size = q.size || 10
  const page = q.page || 1
  return { fixed, items: normal.slice((page - 1) * size, page * size).map(boardRow), total: normal.length }
}
function getBoard(boardId) {
  const b = state.boards.find((x) => x.boardId === Number(boardId))
  if (!b || b.postStatusCd === 'DELETED') return null
  const mine = b.writtenBy === actor().userId
  if (b.postStatusCd === 'TEMP' && !mine) return null
  if (!mine && !boardVisible(b)) return null
  return { ...boardRow(b), content: currentContent(b.boardId), files: attachmentsOf(b.boardId) }
}
/** 진입 시 읽음 상태 upsert: first_read_at 은 최초 1회만, last_read_* 는 매번 갱신 */
function markRead(boardId) {
  const b = state.boards.find((x) => x.boardId === boardId)
  let r = state.readStats.find((x) => x.userId === actor().userId && x.boardId === boardId)
  if (!r) { r = { userId: actor().userId, boardId, firstReadAt: now(), lastReadVer: b.currentVersion, lastReadAt: now() }; state.readStats.push(r) }
  else { r.lastReadVer = b.currentVersion; r.lastReadAt = now() }
}

function canEditBoard(b) {
  return b.writtenBy === actor().userId || isAdmin()
}
// 공지사항: 'ADMIN_NOTICE_WRITE' 권한(관리자/운영자). 자유게시판: 로그인 사용자 — 라우트 가드가 생기기 전까지는 누구나 작성 가능으로 둔다
function canWriteIn(areaKey) {
  return areaKey === 'notice' ? canWriteNotice() : true
}
function assertFixedAllowed(areaCd, boardId, fixedTopYn) {
  if (!fixedTopYn) return
  const count = state.boards.filter((b) => b.areaCd === areaCd && b.fixedTopYn && b.postStatusCd !== 'DELETED' && b.boardId !== boardId).length
  if (count >= state.systemVariables.BOARD_FIXED_MAX) throw new BusinessError('FIXED_MAX_EXCEEDED')
}
function attachNewFiles(boardId, boardTitle, attachments) {
  ;(attachments || []).forEach((a) => {
    state.files.unshift(file(a.name, a.size, 'ACTIVE', [0], currentLoginId(), [link('SYS_BOARD', boardId, boardTitle, null, { attachedBy: currentLoginId() })]))
  })
}

async function createBoard(p) {
  const areaCd = BOARD_AREAS[p.areaKey].code
  if (!canWriteIn(p.areaKey)) throw new BusinessError('FORBIDDEN')
  // 자유게시판의 상단 고정은 관리자만 지정할 수 있다
  const fixedTopYn = p.areaKey === 'free' && !isAdmin() ? false : !!p.fixedTopYn
  assertFixedAllowed(areaCd, 0, fixedTopYn)
  const boardId = nextId(state.boards, 'boardId')
  const noticeYn = p.areaKey === 'notice' && !!p.noticeYn && p.postStatusCd === 'PUBLISHED'
  state.boards.unshift({
    boardId, areaCd, title: p.title, writtenBy: actor().userId, writtenAt: now(), updatedBy: actor().userId, updatedAt: now(),
    fixedTopYn, noticeYn, noticedAt: noticeYn ? now() : '', visibleYn: p.areaKey === 'free' && !isAdmin() ? true : p.visibleYn !== false,
    postStatusCd: p.postStatusCd, openTargetCd: p.areaKey === 'free' ? 'LOGIN' : p.openTargetCd, currentVersion: 1,
  })
  // TODO(API 연동): 에디터 HTML 을 쓰게 되면 저장 직전에 서버에서 화이트리스트 기반 sanitize (Stored XSS 방지). 지금은 일반 텍스트만 저장한다.
  state.versions.push({ boardId, version: 1, content: p.content, createdBy: actor().userId, createdAt: now(), recoveredVersion: null })
  attachNewFiles(boardId, p.title, p.attachments)
  return boardId
}

async function updateBoard(boardId, p) {
  const b = state.boards.find((x) => x.boardId === boardId)
  if (!canEditBoard(b)) throw new BusinessError('FORBIDDEN')
  const fixedTopYn = b.areaCd === 'SYS_FREE' && !isAdmin() ? b.fixedTopYn : !!p.fixedTopYn
  assertFixedAllowed(b.areaCd, boardId, fixedTopYn)
  const content = currentContent(boardId)
  if (p.content !== content) {
    b.currentVersion += 1 // 본문 수정 = 새 버전 생성 (append-only)
    state.versions.push({ boardId, version: b.currentVersion, content: p.content, createdBy: actor().userId, createdAt: now(), recoveredVersion: null })
  }
  const noticeYn = b.areaCd === 'SYS_NOTICE' && !!p.noticeYn && p.postStatusCd === 'PUBLISHED'
  Object.assign(b, {
    title: p.title, fixedTopYn, updatedBy: actor().userId, updatedAt: now(), postStatusCd: p.postStatusCd,
    openTargetCd: b.areaCd === 'SYS_FREE' ? 'LOGIN' : p.openTargetCd,
    noticeYn: noticeYn || b.noticeYn, noticedAt: noticeYn && !b.noticedAt ? now() : b.noticedAt,
    visibleYn: isAdmin() ? p.visibleYn !== false : b.visibleYn,
  })
  ;(p.removeFileIds || []).forEach((fileId) => {
    const f = state.files.find((x) => x.fileId === fileId)
    if (f) f.links = f.links.filter((l) => !(l.targetType === 'SYS_BOARD' && l.targetId === boardId)) // rel 행 삭제 → 파일은 연결 없음(고아 후보)이 된다
  })
  attachNewFiles(boardId, p.title, p.attachments)
}
async function deleteBoard(boardId) {
  const b = state.boards.find((x) => x.boardId === boardId)
  if (!canEditBoard(b)) throw new BusinessError('FORBIDDEN')
  b.postStatusCd = 'DELETED'
  // 대상이 Soft Delete 되면 첨부 파일의 연결은 '끊긴 참조'가 되어 스토리지 정합성 점검에 나타난다
  state.files.forEach((f) => f.links.forEach((l) => { if (l.targetType === 'SYS_BOARD' && l.targetId === boardId) l.targetDeleted = true }))
}
async function setBlind(boardId, visible) {
  if (!isAdmin()) throw new BusinessError('FORBIDDEN')
  state.boards.find((x) => x.boardId === boardId).visibleYn = visible
}
const versionsOf = (boardId) => state.versions.filter((v) => v.boardId === boardId).sort((a, b) => b.version - a.version).map((v) => ({ ...v, createdByName: userName(v.createdBy) }))
async function restoreVersion(boardId, version) {
  if (!isAdmin()) throw new BusinessError('FORBIDDEN')
  const b = state.boards.find((x) => x.boardId === boardId)
  const src = state.versions.find((v) => v.boardId === boardId && v.version === version)
  b.currentVersion += 1 // 선택한 버전의 내용으로 '새 버전'을 만들고, 원본 버전을 recovered_version 에 기록
  state.versions.push({ boardId, version: b.currentVersion, content: src.content, createdBy: actor().userId, createdAt: now(), recoveredVersion: version })
  b.updatedBy = actor().userId; b.updatedAt = now()
}
function commentsOf(boardId) {
  return state.comments.filter((c) => c.boardId === boardId && (c.visibleYn || isAdmin())).map((c) => ({ ...c, writerName: userName(c.writtenBy) }))
}
async function addComment(boardId, content, parentId = null) {
  // 대댓글은 1단계까지만: 부모가 이미 대댓글이면 최상위 부모에 붙인다
  const parent = parentId ? state.comments.find((c) => c.commentId === parentId) : null
  state.comments.push({ commentId: nextId(state.comments, 'commentId'), boardId, parentId: parent ? parent.parentId || parent.commentId : null, content, visibleYn: true, writtenBy: actor().userId, writtenAt: now() })
}
async function blindComment(commentId, visible) {
  if (!isAdmin()) throw new BusinessError('FORBIDDEN')
  state.comments.find((c) => c.commentId === commentId).visibleYn = visible
}

export default {
  state, ERROR_MESSAGES,
  // 공통
  actor, isAdmin, canWriteNotice, hasPermission, roleCdsOf, userName,
  // 사용자/권한
  searchUsers, getUser, updateUserBasic, updateUserExtra, createUser, unlockUsers, suspendUsers, resetPassword, resetMfa,
  grantRole, bulkGrant, revokeRole, revokeRoles, extendRole, roleMapsOf, usersOfRole, activeAdminIds,
  roleHolderCount, createRole, updateRole, setRolePermissions,
  sessionsOf, revokeUserSession, revokeAllUserSessions, historyOf, participationsOf, logExport,
  // 스토리지
  storageSummary, usageByArea, extDistribution, projectUsageRows, topProjects, recountProjects,
  searchFiles, logFileDownload, scheduleDelete, cancelDelete, purgeFile, integrityReport,
  // 시스템 게시판
  listBoards, getBoard, markRead, canEditBoard, canWriteIn, createBoard, updateBoard, deleteBoard, setBlind,
  versionsOf, restoreVersion, commentsOf, addComment, blindComment,
}

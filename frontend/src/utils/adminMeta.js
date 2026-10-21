import dayjs from 'dayjs'

/**
 * 관리자 화면(사용자/권한, 스토리지, 시스템 게시판) 공통 코드·포맷 유틸
 * chip 값은 common.css 의 .chip-* / .badge-muted 클래스명이다.
 * 코드 값은 실제로는 cfg_system_code 에서 내려오는 값이며, 지금은 샘플로 고정해 둔다.
 */

// ---- 사용자 ----
export const ACC_STATUS = {
  ACTIVE: { label: '정상' },
  SUSPENDED: { label: '정지' },
  WITHDRAWN: { label: '탈퇴' },
}

export const ACC_TYPE = {
  GENERAL: { label: '일반' },
  INTERNAL: { label: '내부' },
  EXTERNAL: { label: '외부' },
}

// 실효 상태: 관리자 조치(acc_status_cd)와 로그인 실패 자동 잠금(lock_yn)을 한 값으로 합쳐 보여준다
export const EFFECTIVE_STATUS = {
  NORMAL: { label: '정상', chip: 'chip-success' },
  SUSPENDED: { label: '정지', chip: 'chip-warn' },
  WITHDRAWN: { label: '탈퇴', chip: 'badge-muted' },
  LOCKED: { label: '잠금', chip: 'chip-error' },
}

/** 우선순위: 탈퇴 > 정지 > 잠금 > 정상 */
export function effectiveStatus(user) {
  if (user.accStatusCd === 'WITHDRAWN') return 'WITHDRAWN'
  if (user.accStatusCd === 'SUSPENDED') return 'SUSPENDED'
  if (user.security.lockYn) return 'LOCKED'
  return 'NORMAL'
}

export const JOBS = ['개발자', '기획자', '디자이너', '운영자', '기타']
export const COUNTRIES = [
  { code: 'KR', label: '대한민국' },
  { code: 'US', label: '미국' },
  { code: 'JP', label: '일본' },
  { code: 'ETC', label: '기타' },
]

/** ye***@domain.com */
export function maskEmail(email) {
  if (!email || !email.includes('@')) return email || '-'
  const [local, domain] = email.split('@')
  return `${local.slice(0, 2)}***@${domain}`
}

/** 010-****-1234 */
export function maskPhone(phone) {
  if (!phone) return '-'
  const digits = phone.replace(/\D/g, '')
  if (digits.length < 8) return '***'
  return `${digits.slice(0, 3)}-****-${digits.slice(-4)}`
}

/** 역할 매핑이 지금 유효한가: use_yn = 1 AND (expired_at IS NULL OR expired_at > NOW()) */
export function isRoleEffective(map, now = dayjs()) {
  return !!map.useYn && (!map.expiredAt || dayjs(map.expiredAt).isAfter(now))
}

// ---- 스토리지 ----
export const FILE_STATUS = {
  UPLOADING: { label: '업로드 중', chip: 'badge-muted' },
  ACTIVE: { label: '사용 중', chip: 'chip-success' },
  DELETE_SCHEDULED: { label: '삭제 예정', chip: 'chip-warn' },
  DELETED: { label: '삭제 완료', chip: 'chip-error' },
}

export const TARGET_TYPES = {
  PROJECT: { label: '프로젝트' },
  DOCUMENT: { label: '문서' },
  BOARD: { label: '프로젝트 게시판' },
  SYS_BOARD: { label: '시스템 게시판' },
}

export const FILE_ROLE = {
  ATTACH: '첨부',
  COVER: '대표 이미지',
  REF: '참고 자료',
}

export const SIZE_RANGES = [
  { code: 'S', label: '1MB 미만', min: 0, max: 1024 ** 2 },
  { code: 'M', label: '1MB ~ 10MB', min: 1024 ** 2, max: 10 * 1024 ** 2 },
  { code: 'L', label: '10MB 이상', min: 10 * 1024 ** 2, max: Infinity },
]

/** 1536 → '1.5 KB' */
export function formatBytes(bytes) {
  if (bytes === null || bytes === undefined) return '-'
  if (bytes < 1024) return `${bytes} B`
  const units = ['KB', 'MB', 'GB', 'TB']
  let value = bytes / 1024
  let i = 0
  while (value >= 1024 && i < units.length - 1) {
    value /= 1024
    i += 1
  }
  return `${value >= 100 ? value.toFixed(0) : value.toFixed(1)} ${units[i]}`
}

// ---- 시스템 게시판 ----
export const BOARD_AREAS = {
  notice: { code: 'SYS_NOTICE', label: '공지사항' },
  free: { code: 'SYS_FREE', label: '자유게시판' },
}

export const OPEN_TARGETS = {
  ALL: { label: '전체' },
  LOGIN: { label: '로그인 사용자' },
  ADMIN: { label: '관리자' },
}

export const READ_STATUS = {
  NEW: { label: 'N', chip: 'chip-error', title: '읽지 않음' },
  UPDATED: { label: '수정됨', chip: 'chip-warn', title: '읽은 뒤 내용이 변경됨' },
  READ: { label: '', chip: '', title: '읽음' },
}

// ---- 공통 ----
export const now = () => dayjs().format('YYYY-MM-DD HH:mm')

/** 화면 간 단순 비교용 — 'YYYY-MM-DD HH:mm' / 'YYYY-MM-DD' 모두 허용 */
export function inRange(value, from, to) {
  if (!value) return !from && !to
  const day = dayjs(value).format('YYYY-MM-DD')
  if (from && day < from) return false
  if (to && day > to) return false
  return true
}

/** 두 텍스트의 줄 단위 diff (LCS). [{type:'same'|'add'|'del', text}] */
export function diffLines(before, after) {
  const a = (before || '').split('\n')
  const b = (after || '').split('\n')
  const dp = Array.from({ length: a.length + 1 }, () => new Array(b.length + 1).fill(0))
  for (let i = a.length - 1; i >= 0; i--) {
    for (let j = b.length - 1; j >= 0; j--) {
      dp[i][j] = a[i] === b[j] ? dp[i + 1][j + 1] + 1 : Math.max(dp[i + 1][j], dp[i][j + 1])
    }
  }
  const out = []
  let i = 0
  let j = 0
  while (i < a.length && j < b.length) {
    if (a[i] === b[j]) { out.push({ type: 'same', text: a[i] }); i++; j++ }
    else if (dp[i + 1][j] >= dp[i][j + 1]) { out.push({ type: 'del', text: a[i] }); i++ }
    else { out.push({ type: 'add', text: b[j] }); j++ }
  }
  while (i < a.length) out.push({ type: 'del', text: a[i++] })
  while (j < b.length) out.push({ type: 'add', text: b[j++] })
  return out
}

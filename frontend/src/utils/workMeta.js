import dayjs from 'dayjs'

/**
 * 샘플 화면(업무/이슈/멤버 등)에서 공통으로 쓰는 코드 → 라벨/칩 클래스 매핑.
 * chip 값은 common.css 의 .chip-* / .badge-muted 클래스명이다.
 */
export const TASK_STATUS = {
  DONE: { label: '완료', chip: 'chip-success' },
  IN_PROGRESS: { label: '진행중', chip: 'chip-common' },
  DELAYED: { label: '지연', chip: 'chip-warn' },
  WAIT: { label: '대기', chip: 'badge-muted' },
}

export const ISSUE_SEVERITY = {
  HIGH: { label: 'HIGH', color: 'var(--color-error-500)' },
  MED: { label: 'MED', color: 'var(--color-warning-500)' },
  LOW: { label: 'LOW', color: 'var(--color-success-500)' },
}

export const ISSUE_TYPE = {
  BUG: { label: '버그', chip: 'chip-error' },
  IMPROVE: { label: '개선', chip: 'chip-warn' },
  ETC: { label: '기타', chip: 'badge-muted' },
}

export const ISSUE_STATUS = {
  PROGRESS: { label: '처리중', chip: 'chip-warn' },
  WAIT: { label: '대기', chip: 'badge-muted' },
  DONE: { label: '완료', chip: 'chip-success' },
}

export const MEMBER_ROLES = ['PM', 'DEV', '멤버']

export const ROLE_CHIP = {
  PM: 'chip-common',
  DEV: 'chip-success',
  '멤버': 'badge-muted',
}

/** '2026-10-25' → '10월 25일' (값이 없으면 빈 문자열) */
export function formatMonthDay(value) {
  return value ? dayjs(value).format('M월 D일') : ''
}

/** 오늘 날짜 'YYYY-MM-DD' */
export function today() {
  return dayjs().format('YYYY-MM-DD')
}

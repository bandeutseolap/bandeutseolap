/**
 * 마이페이지 공통 코드 → 라벨/칩 매핑
 * chip 값은 common.css 의 .chip-* / .badge-muted 클래스명이다.
 */
export const MFA_METHODS = {
  NONE: { label: '사용 안 함' },
  OTP: { label: '인증 앱 (OTP)' },
  SMS: { label: 'SMS 인증' },
  EMAIL: { label: '이메일 인증' },
}

export const SESSION_STATUS = {
  ACTIVE: { label: '활성', chip: 'chip-success' },
  EXPIRED: { label: '만료', chip: 'badge-muted' },
}

export const NATIONALITIES = [
  { code: 'KR', label: '대한민국' },
  { code: 'US', label: '미국' },
  { code: 'JP', label: '일본' },
  { code: 'CN', label: '중국' },
  { code: 'GB', label: '영국' },
  { code: 'DE', label: '독일' },
  { code: 'ETC', label: '기타' },
]

/** 마이페이지 첫 화면에 보여줄 4개 화면 (이미지의 '화면 / 필드' 표와 동일) */
export const MYPAGE_MENUS = [
  {
    name: 'MyProfile',
    title: '내 정보',
    fields: '이름, 이메일, 휴대폰, 프로필 이미지(image_path), 생년월일, 직업, 국적',
    icon: 'M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2 M12 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8z',
  },
  {
    name: 'MySecurity',
    title: '보안 설정',
    fields: '비밀번호 변경, MFA 설정(수단, 최종 인증일), 마지막 로그인',
    icon: 'M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z',
  },
  {
    name: 'MySessions',
    title: '로그인 세션 관리',
    fields: 'Redis 세션 목록(로그인 유형, 접속 IP, 마지막 접근, 상태). 개별 또는 전체 로그아웃',
    icon: 'M2 3h20v14H2z M8 21h8 M12 17v4',
  },
  {
    name: 'MyConsents',
    title: '동의 관리',
    fields: '개인정보 수집 동의 여부, 동의일시, 철회',
    icon: 'M9 11l3 3L22 4 M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11',
  },
]

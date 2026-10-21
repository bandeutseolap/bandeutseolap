/**
 * 메인 화면('/') 라우트
 * -----------------------------------------------------------
 * 메인 화면은 '전체 대시보드'(ProjectOverView.vue, name: 'ProjectOverview')이다.
 * 화면 라우트는 router/modules/project.js 에 한 번만 정의하고, 여기서는 '/' 를 그쪽으로 보내기만 한다.
 *
 * 이전에는 이 파일에서도 '/projects/dashboard' 를 한 번 더 정의했는데,
 *  1) import 경로의 대소문자(ProjectOverview.vue ≠ ProjectOverView.vue)가 달라 Linux/Docker 빌드에서 파일을 못 찾았고
 *  2) 같은 이름·같은 경로가 project.js 와 중복되었으며
 *  3) '/' 자체에는 연결된 화면이 없어 로그인 직후(router.push('/')) 빈 화면이 보였다.
 * -----------------------------------------------------------
 */
export default [
    { path: '/', redirect: { name: 'ProjectOverview' } }
]

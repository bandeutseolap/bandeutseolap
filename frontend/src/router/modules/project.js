import DefaultLayout from '@/layouts/DefaultLayout.vue'

export default [
    {
        path: '/projects',
        component: DefaultLayout,
        children: [
          { // 프로젝트 관리 > 관리 페이지
            path: '/projects',
            name: 'ProjectManage',
            component: () => import('@/views/ProjectManageView.vue'),
            // 예전 주소(/projects?tab=phase)로 들어온 북마크/링크는 단계 설정 화면으로 보낸다
            beforeEnter: (to) => (to.query.tab === 'phase' ? { name: 'PhaseSetting' } : true),
          },
          { // 프로젝트 관리 > 단계 설정 (기존 '수행단계' 탭)
            path: '/projects/phases',
            name: 'PhaseSetting',
            component: () => import('@/views/phase/PhaseSettingView.vue'),
          },
          { // 프로젝트 관리 > 전체 대시보드
            path: '/projects/dashboard',
            name: 'ProjectOverview',
            component: () => import('@/views/project/ProjectOverviewView.vue'),
          },
          {
            path: '/projects/new',
            name: 'ProjectCreate',
            component: () => import('@/views/project/ProjectFormView.vue'),
            // id prop이 없으면 ProjectFormView가 자동으로 등록(신규) 모드로 동작
          },
          {
            path: '/projects/:id/edit',
            name: 'ProjectEdit',
            component: () => import('@/views/project/ProjectFormView.vue'),
            props: true, // route.params.id -> component의 id prop으로 전달
          },
          {
            path: '/projects/:projectId/phases/new',
            name: 'PhaseCreate',
            component: () => import('@/views/phase/PhaseFormView.vue'),
            // id prop이 없으면 PhaseFormView가 자동으로 등록(신규) 모드로 동작
          },
          {
            path: '/projects/:projectId/phases/:id/edit',
            name: 'PhaseEdit',
            component: () => import('@/views/phase/PhaseFormView.vue'),
            props: true, // route.params.id -> component의 id prop으로 전달
          },
        ]
    }
]

import DefaultLayout from '@/layouts/DefaultLayout.vue'

export default [
    {
        path: '/projects',
        component: DefaultLayout,
        children: [
          {
            path: '/projects',
            name: 'ProjectManage',
            component: () => import('@/views/ProjectManageView.vue'),
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
            // id prop이 없으면 ProjectFormView가 자동으로 등록(신규) 모드로 동작
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
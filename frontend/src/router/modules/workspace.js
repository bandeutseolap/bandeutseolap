import DefaultLayout from '@/layouts/DefaultLayout.vue'

/**
 * 멤버 / 문서 / 업무 / 이슈 / 마이페이지 / 템플릿 공유 라우트
 * (사이드바·헤더 메뉴의 라우트 name 은 config/navigation.js 와 일치해야 한다)
 */
export default [
    {
        path: '/members',
        component: DefaultLayout,
        children: [
            { path: '', name: 'MemberManage', component: () => import('@/views/member/MemberManageView.vue') },
            { path: 'permissions', name: 'PermissionManage', component: () => import('@/views/member/PermissionManageView.vue') }
        ]
    },
    {
        path: '/documents',
        component: DefaultLayout,
        children: [
            { path: '', name: 'DocumentList', component: () => import('@/views/document/DocumentListView.vue') },
            { path: 'new', name: 'DocumentCreate', component: () => import('@/views/document/DocumentFormView.vue') },
            { path: 'templates', name: 'TemplateList', component: () => import('@/views/document/TemplateListView.vue') },
            { path: 'templates/new', name: 'TemplateCreate', component: () => import('@/views/document/TemplateFormView.vue') }
        ]
    },
    {
        path: '/tasks',
        component: DefaultLayout,
        children: [
            { path: '', name: 'TaskList', component: () => import('@/views/task/TaskListView.vue') },
            { path: 'new', name: 'TaskCreate', component: () => import('@/views/task/TaskFormView.vue') }
        ]
    },
    {
        path: '/issues',
        component: DefaultLayout,
        children: [
            { path: '', name: 'IssueList', component: () => import('@/views/issue/IssueListView.vue') },
            { path: 'new', name: 'IssueCreate', component: () => import('@/views/issue/IssueFormView.vue') }
        ]
    },
    {
        path: '/mypage',
        component: DefaultLayout,
        children: [
            { path: '', name: 'MyPage', component: () => import('@/views/mypage/MyPageView.vue') },
            { path: 'profile', name: 'MyProfile', component: () => import('@/views/mypage/MyProfileView.vue') },
            { path: 'security', name: 'MySecurity', component: () => import('@/views/mypage/MySecurityView.vue') },
            { path: 'sessions', name: 'MySessions', component: () => import('@/views/mypage/MySessionsView.vue') },
            { path: 'consents', name: 'MyConsents', component: () => import('@/views/mypage/MyConsentsView.vue') }
        ]
    },
    // 예전 '계정 관리' 주소는 마이페이지로 보낸다
    { path: '/account', redirect: { name: 'MyPage' } },
    {
        path: '/templates/share',
        component: DefaultLayout,
        children: [
            { path: '', name: 'TemplateShare', component: () => import('@/views/template/TemplateShareView.vue') }
        ]
    }
]

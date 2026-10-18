import DefaultLayout from '@/layouts/DefaultLayout.vue'

/**
 * 멤버 / 문서 / 업무 / 이슈 / 계정 / 템플릿 공유 라우트
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
        path: '/account',
        component: DefaultLayout,
        children: [
            { path: '', name: 'AccountManage', component: () => import('@/views/account/AccountManageView.vue') }
        ]
    },
    {
        path: '/templates/share',
        component: DefaultLayout,
        children: [
            { path: '', name: 'TemplateShare', component: () => import('@/views/template/TemplateShareView.vue') }
        ]
    }
]

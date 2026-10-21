import DefaultLayout from '@/layouts/DefaultLayout.vue'

/**
 * 관리자 영역 라우트: 사용자/권한 관리 · 스토리지 관리 · 시스템 게시판
 * (헤더 메뉴의 names 는 config/navigation.js 의 headerMenu 와 일치해야 한다)
 * 정적 경로('new', 'permissions' 등)를 동적 경로(':userId')보다 먼저 선언한다.
 */
export default [
    {
        path: '/admin/users',
        component: DefaultLayout,
        children: [
            { path: '', name: 'AdminUserList', component: () => import('@/views/admin/user/UserListView.vue') },
            { path: 'new', name: 'AdminUserCreate', component: () => import('@/views/admin/user/UserCreateView.vue') },
            { path: ':userId(\\d+)', name: 'AdminUserDetail', component: () => import('@/views/admin/user/UserDetailView.vue') }
        ]
    },
    {
        path: '/admin/roles',
        component: DefaultLayout,
        children: [
            { path: '', name: 'AdminRoleList', component: () => import('@/views/admin/role/RoleListView.vue') },
            { path: 'permissions', name: 'AdminRolePermission', component: () => import('@/views/admin/role/RolePermissionView.vue') },
            { path: 'users', name: 'AdminRoleUsers', component: () => import('@/views/admin/role/RoleUsersView.vue') }
        ]
    },
    {
        path: '/admin/storage',
        component: DefaultLayout,
        children: [
            { path: '', name: 'StorageDashboard', component: () => import('@/views/admin/storage/StorageDashboardView.vue') },
            { path: 'projects', name: 'StorageProjects', component: () => import('@/views/admin/storage/StorageProjectsView.vue') },
            { path: 'files', name: 'StorageFiles', component: () => import('@/views/admin/storage/StorageFilesView.vue') },
            { path: 'deletions', name: 'StorageDeletions', component: () => import('@/views/admin/storage/StorageDeletionsView.vue') },
            { path: 'integrity', name: 'StorageIntegrity', component: () => import('@/views/admin/storage/StorageIntegrityView.vue') }
        ]
    },
    {
        // 시스템 게시판: 공지사항/자유게시판은 같은 화면이며 ?area=notice|free 로 구분한다
        path: '/system-board',
        component: DefaultLayout,
        children: [
            { path: '', name: 'SystemBoardList', component: () => import('@/views/systemboard/SystemBoardListView.vue') },
            { path: 'new', name: 'SystemBoardCreate', component: () => import('@/views/systemboard/SystemBoardFormView.vue') },
            { path: ':boardId(\\d+)', name: 'SystemBoardDetail', component: () => import('@/views/systemboard/SystemBoardDetailView.vue') },
            { path: ':boardId(\\d+)/edit', name: 'SystemBoardEdit', component: () => import('@/views/systemboard/SystemBoardFormView.vue') },
            { path: ':boardId(\\d+)/versions', name: 'SystemBoardVersions', component: () => import('@/views/systemboard/SystemBoardVersionView.vue') }
        ]
    }
]

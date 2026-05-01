import DefaultLayout from '@/layouts/DefaultLayout.vue'
import BoardListView from '@/views/BoardListView.vue'
import BoardDetailView from '@/views/BoardDetailView.vue'
import BoardFormView from '@/views/BoardFormView.vue'

export default [
    {
        path: '/boards',
        component: DefaultLayout,
        children: [
            {
                path: '',
                name: 'boards',
                component: BoardListView
            },
            { // 생성
                path: 'new',
                name: 'boards-create',
                component: BoardFormView
            },
            {
                path: ':id',
                name: 'boards-detail',
                component: BoardDetailView
            },
            { // 수정
                path: ':id/edit',
                name: 'boards-edit',
                component: BoardFormView
            }
        ]
    }
]
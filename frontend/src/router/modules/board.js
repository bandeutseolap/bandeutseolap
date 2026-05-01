import DefaultLayout from '../../layouts/DefaultLayout.vue'
import BoardListView from '../../views/BoardListView.vue'
import BoardDetailView from '../../views/BoardDetailView.vue'

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
            {
                path: ':id',
                name: 'boards-detail',
                component: BoardDetailView
            }
        ]
    }
]
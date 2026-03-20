import DefaultLayout from '../../layouts/DefaultLayout.vue'
import BoardView from '../../views/BoardView.vue'

export default [
    {
        path: '/boards',
        component: DefaultLayout,
        children: [
            {
                path: '',
                name: 'boards',
                component: BoardView
            }
        ]
    }
]
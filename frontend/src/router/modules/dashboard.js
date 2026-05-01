import DefaultLayout from '../../layouts/DefaultLayout.vue'
import DashboardView from '../../views/DashBoardView.vue'

export default [
    {
        path: '/',
        component: DefaultLayout,
        children: [
            {
                path: '',
                name: 'dashboard',
                component: DashboardView
            }
        ]
    }
]
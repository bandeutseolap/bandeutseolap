import AuthLoginView from '@/views/AuthLoginView.vue'

export default [
    {
        path: '/auth',
        component: '',
        children: [
            {
                path: 'login',
                name: 'AuthLogin',
                component: AuthLoginView
            }
        ]
    }
]
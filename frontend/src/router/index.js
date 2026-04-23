import { createRouter, createWebHistory } from 'vue-router'

import dashboardRoutes from '@/router/modules/dashboard'
import boardRoutes from '@/router/modules/board'
import authRoutes from '@/router/modules/auth'

const routes = [
    ...dashboardRoutes,
    ...authRoutes,
    ...boardRoutes,
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router
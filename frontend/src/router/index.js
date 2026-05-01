import { createRouter, createWebHistory } from 'vue-router'

import dashboardRoutes from './modules/dashboard'
import boardRoutes from './modules/board'

const routes = [
    ...dashboardRoutes,
    ...boardRoutes,
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router
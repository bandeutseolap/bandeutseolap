import { createRouter, createWebHistory } from 'vue-router'

import dashboardRoutes from '@/router/modules/dashboard'
import boardRoutes from '@/router/modules/board'
import authRoutes from '@/router/modules/auth'
import projectRoutes from '@/router/modules/project'
import workspaceRoutes from '@/router/modules/workspace'

const routes = [
    ...dashboardRoutes,
    ...authRoutes,
    ...boardRoutes,
    ...projectRoutes,
    ...workspaceRoutes
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router
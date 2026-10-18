import { createApp } from 'vue'
import router from './router'
import './assets/styles/common.css'
import './assets/styles/pms-common.css'
import App from './App.vue'

createApp(App).use(router).mount('#app')

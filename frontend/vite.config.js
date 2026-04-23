import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    extensions:['.mjs', '.js', '.mts', '.ts', '.jsx', '.tsx', '.json', '.vue'], //.vue 추가!
    alias: {
      "@": path.resolve(__dirname, "src"),
    },
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        /*
        * 브라우저에선 항상 5173만 접속 (Vue 개발화면 그대로 봄)
        * API 호출만 8080으로 전달 → CORS 걱정 없음
        * Vue 수정 시 자동 리로드 (HMR) 그대로 동작
        * */
        rewrite: path => path.replace(/^\/api/, ''),
        configure: (proxy, options) => {
          proxy.on('proxyReq', (proxyReq, req, res) => {
            console.log('Proxy Request:', req.url)
          })
        }
      },
    },
  },
})

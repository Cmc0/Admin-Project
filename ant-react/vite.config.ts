import {defineConfig, loadEnv} from 'vite'
import react from '@vitejs/plugin-react'
import {resolve} from 'path'

// https://vitejs.dev/config/
export default ({mode}) =>
    defineConfig({
        plugins: [react()],
        resolve: {
            alias: [
                {
                    find: /^~/,
                    replacement: '',
                },
                {
                    find: '@',
                    replacement: resolve(__dirname, './src')
                }
            ],
        },
        server: {
            port: 9529,
            proxy: {
                '/api': {
                    target: loadEnv(mode, process.cwd()).VITE_API_BASE_URL,
                    changeOrigin: true,
                    rewrite: (path) => path.replace(/^\/api/, ''),
                },
            },
        },
        css: {
            preprocessorOptions: {
                less: {
                    javascriptEnabled: true,
                },
            },
        },
    })

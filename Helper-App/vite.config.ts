import {defineConfig} from 'vite'
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
        css: {
            preprocessorOptions: {
                less: {
                    javascriptEnabled: true,
                },
            },
        },
    })

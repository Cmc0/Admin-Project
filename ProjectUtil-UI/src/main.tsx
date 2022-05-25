import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App'
import zhCN from 'antd/lib/locale/zh_CN'
import {ConfigProvider} from 'antd'
import './index.less'
import "antd/dist/antd.css"

ReactDOM.createRoot(document.getElementById('root')!).render(
    <React.StrictMode>
        <ConfigProvider locale={zhCN}>
            <App/>
        </ConfigProvider>
    </React.StrictMode>
)

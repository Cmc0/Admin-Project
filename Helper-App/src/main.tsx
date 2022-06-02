import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App'
import './index.less'

import {ConfigProvider} from 'antd'
import zhCN from 'antd/lib/locale/zh_CN'
import "antd/dist/antd.css"

import moment from 'moment';
import 'moment/dist/locale/zh-cn';

import {Provider} from 'react-redux'
import redux from './store'

moment.locale('zh-cn');

ReactDOM.createRoot(document.getElementById('root')!).render(
    <React.StrictMode>
        <Provider store={redux}>
            <ConfigProvider locale={zhCN}>
                <App/>
            </ConfigProvider>
        </Provider>
    </React.StrictMode>
)

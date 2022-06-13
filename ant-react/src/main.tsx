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
import redux from './redux'
import ApiSyncUtil from "../util/ApiSyncUtil";

moment.locale('zh-cn');

if (import.meta.env.DEV) {
    window.ApiSyncUtil = ApiSyncUtil
}

ReactDOM.createRoot(document.getElementById('root')!).render(
    <React.StrictMode>
        <Provider store={redux}>
            <ConfigProvider locale={zhCN}>
                <App/>
            </ConfigProvider>
        </Provider>
    </React.StrictMode>
)

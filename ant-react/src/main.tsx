import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App'
import './index.less'

import {BackTop, ConfigProvider} from 'antd'
import zhCN from 'antd/es/locale/zh_CN'
import "antd/dist/antd.css"

import moment from 'moment';
import 'moment/dist/locale/zh-cn';

import {Provider} from 'react-redux'
import store from './store'

moment.locale('zh-cn');

// 自定义 console.error ↓
const consoleOldError = console.error

console.error = (message?: any, ...optionalParams: any[]) => {
    if (optionalParams && optionalParams.length) {
        if (optionalParams[0] === 'findDOMNode') { // 过滤 findDOMNode警告，TODO：等 antd 官方修复，然后删除
            return
        }
    }
    if (typeof message === 'string' && message.startsWith("Warning: Tree missing follow keys:")) {
        // TODO：等 antd pro 官方修复，然后删除
        console.log(message)
        return;
    }
    consoleOldError(message, ...optionalParams)
}
// 自定义 console.error ↑

ReactDOM.createRoot(document.getElementById('root')!).render(
    <React.StrictMode>
        <Provider store={store}>
            <ConfigProvider locale={zhCN}>
                <App/>
                <BackTop/>
            </ConfigProvider>
        </Provider>
    </React.StrictMode>
)

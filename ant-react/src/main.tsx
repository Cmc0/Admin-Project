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
// appLoading ↓
import lottie, {AnimationItem} from 'lottie-web'

moment.locale('zh-cn');

// 自定义 console.error ↓
const consoleOldError = console.error

console.error = (message?: any, ...optionalParams: any[]) => {
    if (optionalParams && optionalParams.length) {
        if (optionalParams[0] === 'findDOMNode') { // 过滤 findDOMNode警告
            return
        }
    }
    if (typeof message === 'string' && message.startsWith("Warning: Tree missing follow keys:")) {
        // TODO：等官方修复，然后删除
        console.log(message)
        return;
    }
    consoleOldError(message, ...optionalParams)
}
// 自定义 console.error ↑

let lottieObj: AnimationItem

initAppLoading() // 执行【初始化】

// 添加 appLoading
export function addAppLoading() {
    const appLoading = document.createElement('div')
    appLoading.id = 'appLoading'
    const appLoadingDiv = document.createElement('div')
    appLoadingDiv.id = 'appLoadingDiv'
    appLoading.appendChild(appLoadingDiv)
    document.body.appendChild(appLoading) // 添加到 body里面
    initAppLoading()
}

// 初始化 appLoading
function initAppLoading() {
    lottieObj = lottie.loadAnimation({
        container: document.getElementById('appLoadingDiv')!, // 容器
        renderer: 'svg', // 'svg' / 'canvas' / 'html'
        loop: true, // 动画是否循环
        autoplay: true, //是否自动播放
        path: '/appLoading.json', // 动画文件路径
    })
}

// 销毁 appLoading
export function destroyAppLoading() {
    if (lottieObj) {
        lottieObj.destroy()
    }
    document.getElementById('appLoading')?.remove()
}

// appLoading ↑

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

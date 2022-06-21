import LocalStorageKey from "../src/model/constant/LocalStorageKey";
import {ToastError} from "./ToastUtil";
import {getAppNav} from "@/App";
import SessionStorageKey from "@/model/constant/SessionStorageKey";
import CommonConstant from "@/model/constant/CommonConstant";
import {logout} from "./UserUtil";
import {webSocketRegister} from "@/api/WebSocketController";

let webSocketUrl: string | undefined = ''
let code: string | undefined = ''
let webSocket: WebSocket | null = null

function getWebSocketRegData() {

  webSocketUrl = ''
  code = ''

  return webSocketRegister({
    value: Number(localStorage.getItem(LocalStorageKey.WEB_SOCKET_TYPE)) || 1
  })
      .then(({data}) => {
        webSocketUrl = data.webSocketUrl
        code = data.code
      })
      .catch(async () => {
        if (!localStorage.getItem(LocalStorageKey.JWT)) {
          return // 如果没有 jwt，则不重连了，目的：防止一直连
        }
        await new Promise((resolve) => {
          setTimeout(async () => {
            await getWebSocketRegData() // 等 2秒，再次获取 webSocket服务器
            resolve(null)
          }, 2000)
        })
      })
}

// 关闭 webSocket
export function closeWebSocket() {
  if (webSocket) {
    webSocket.close()
    webSocket = null
  }
}

export interface IWebSocketMessage {
  code: 1 | 2 | 3 | 4 | 5 | 6 // 1 webSocket连接记录主键id 2 账号已在其他地方登录，您被迫下线 3 登录过期，请重新登录 4 账号已被注销 5 有新的通知 6 有新的公告
  json?: Record<string, any> // 额外信息
  codeDescription?: string // code 说明
}

// 连接 webSocket
export function connectWebSocket(
    doSetSocketMessage: (params: IWebSocketMessage) => void,
    doSetSocketStatus: (params: boolean) => void
) {
  if (webSocket) {
    return
  }
  if (!localStorage.getItem(LocalStorageKey.JWT)) {
    return // 如果没有 jwt，则不重连了，目的：防止一直连
  }
  return new Promise(async (resolve, reject) => {
    if (window.WebSocket) {
      await getWebSocketRegData()
      if (!webSocketUrl || !code) {
        return reject(new Error('连接 webSocket失败：暂无可用的服务器'))
      }
      if (webSocket) {
        return
      }
      webSocket = new WebSocket(
          `${
              import.meta.env.DEV ? 'ws:' : 'ws:'
          }//${webSocketUrl}/ws?code=${code}&category=1`
      )

      webSocket.onmessage = (message: MessageEvent<string>) => {

        const data: IWebSocketMessage = JSON.parse(message.data)

        if (data.code === 1) {
          // 存储本次 webSocket连接记录主键id
          if (!data.json || !data.json.webSocketId) {
            sessionStorage.setItem(SessionStorageKey.WEB_SOCKET_ID, '')
            console.error(new Error('系统故障：webSocketId为空'))
          } else {
            sessionStorage.setItem(
                SessionStorageKey.WEB_SOCKET_ID,
                data.json.webSocketId
            )
          }
          return resolve(null) // 得到 webSocketId之后，才调用 resolve()方法
        }

        if (data.code === 2 || data.code === 3 || data.code === 4) {
          // 2 账号已在其他地方登录，您被迫下线
          // 3 登录过期，请重新登录
          // 4 账号已被注销
          ToastError(data.codeDescription || 'Error：webSocket#onmessage', 5)
          logout()
          return
        }

        // 更新 redux里面 webSocket的值
        doSetSocketMessage({} as IWebSocketMessage) // 先重置，再设置值
        setTimeout(() => {
          doSetSocketMessage(data)
        }, 200)
      }
      webSocket.onopen = (event) => {
        console.log(`webSocket连接 :>> ${webSocketUrl}`)
        doSetSocketStatus(true)
        if (sessionStorage.getItem(SessionStorageKey.LOAD_MENU_FLAG) === 'false') {
          getAppNav()(CommonConstant.MAIN_PATH) // 如果此时，还没有加载完菜单，则再加载一次
        }
      }
      webSocket.onclose = (event) => {
        console.log('webSocket 关闭')
        doSetSocketStatus(false)
        setTimeout(() => {
          webSocket = null // 重置 webSocket对象，为了可以重新获取 webSocket连接地址
          connectWebSocket(doSetSocketMessage, doSetSocketStatus)
        }, 2000) // 等待 2秒，再去重连webSocket
      }
    } else {
      ToastError('您的浏览器不支持 webSocket协议，请更换浏览器再试', 5)
    }
  })
}

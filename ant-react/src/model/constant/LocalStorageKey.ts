const LocalStorageKey = {
    JWT: "JWT",
    WEB_SOCKET_TYPE: 'WEB_SOCKET_TYPE', // webSocket 在线状态
    USER_BASE_INFO: 'USER_BASE_INFO', // 用户基本信息
}

export default LocalStorageKey

export type TWebSocketType = '1' | '2' // 1 在线 2 隐身

export function GetWebSocketType(): TWebSocketType {
    return (localStorage.getItem(LocalStorageKey.WEB_SOCKET_TYPE) || '1') as TWebSocketType
}

export function SetWebSocketType(webSocketType: TWebSocketType = '2') {
    localStorage.setItem(LocalStorageKey.WEB_SOCKET_TYPE, webSocketType)
}

import NotNullByte from "@/model/dto/NotNullByte";
import $http from "../../util/HttpUtil";

interface WebSocketRegVO {
    webSocketUrl: string // WebSocket 连接地址，ip:port
    code: string // WebSocket 连接码，备注：只能使用一次
}

// webSocket 获取 webSocket连接地址和随机码
export function webSocketReg(form: NotNullByte) {
    return $http.myPost<WebSocketRegVO>("/webSocket/reg", form,
        {
            timeout: 2000,
            headers: {
                hiddenErrorMsg: true, // 隐藏接口请求报错提示
            }
        })
}

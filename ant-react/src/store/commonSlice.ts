import {createSlice, PayloadAction} from '@reduxjs/toolkit'
import {IWebSocketMessage} from "../../util/WebSocketUtil";

interface ICommonSlice {
    webSocketMessage: IWebSocketMessage // webSocket消息模板
    webSocketStatus: boolean // webSocket连接状态
    rsaPublicKey: string // 非对称：公钥
}

const initialState: ICommonSlice = {
    webSocketMessage: {} as IWebSocketMessage,
    webSocketStatus: false,
    rsaPublicKey: 'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDadmaCaffN63JC5QsMK/+le5voCB4DzOsV9xOBZgGJyqnizh9/UcFkIoRae5rebdWUtnPO4CTgdJbuSvu/TtIIPj9De5/wiJilFAWd1Ve7qGaxxTxqWwFNp7p/FLr0YpMeBjOylds9GyA1cnjIqruNdYv+qRZnseE0Sq2WEZus9QIDAQAB',
}

export const commonSlice = createSlice({
    name: 'commonSlice',
    initialState,
    reducers: {

        setWebSocketMessage(state, action: PayloadAction<IWebSocketMessage>) {
            state.webSocketMessage = action.payload
        },
        setWebSocketStatus(state, action: PayloadAction<boolean>) {
            state.webSocketStatus = action.payload
        },
    },
})

export const {setWebSocketMessage, setWebSocketStatus} = commonSlice.actions

export default commonSlice.reducer

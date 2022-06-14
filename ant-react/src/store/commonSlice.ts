import {createSlice, PayloadAction} from '@reduxjs/toolkit'
import {IWebSocketMessage} from "../../util/WebSocketUtil";

interface ICommonSlice {
    webSocketMessage: IWebSocketMessage // webSocket消息模板
    webSocketStatus: boolean // webSocket连接状态
}

const initialState: ICommonSlice = {
    webSocketMessage: {} as IWebSocketMessage,
    webSocketStatus: false,
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

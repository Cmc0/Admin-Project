import {createSlice, PayloadAction} from '@reduxjs/toolkit'
import SessionStorageKey from "@/model/constant/SessionStorageKey";
import {IWebSocketMessage} from "../../util/WebSocketUtil";

interface ICommonSlice {
    loadMenuFlag: boolean // 是否加载过菜单
    webSocketMessage: IWebSocketMessage // webSocket消息模板
    webSocketStatus: boolean // webSocket连接状态
}

const initialState: ICommonSlice = {
    loadMenuFlag: false,
    webSocketMessage: {} as IWebSocketMessage,
    webSocketStatus: false,
}

export const commonSlice = createSlice({
    name: 'commonSlice',
    initialState,
    reducers: {
        setLoadMenuFlag(state, action: PayloadAction<boolean>) {
            state.loadMenuFlag = action.payload
            sessionStorage.setItem(
                SessionStorageKey.LOAD_MENU_FLAG,
                String(action.payload)
            )
        },
        setWebSocketMessage(state, action: PayloadAction<IWebSocketMessage>) {
            state.webSocketMessage = action.payload
        },
        setWebSocketStatus(state, action: PayloadAction<boolean>) {
            state.webSocketStatus = action.payload
        },
    },
})

export const {setLoadMenuFlag, setWebSocketMessage, setWebSocketStatus} = commonSlice.actions

export default commonSlice.reducer

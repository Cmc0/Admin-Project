import {createSlice} from '@reduxjs/toolkit'

interface ICommonSlice {
    // leftMenuCollapsed: boolean // 左侧菜单是否收起
}

const initialState: ICommonSlice = {
    // leftMenuCollapsed: false
}

export const commonSlice = createSlice({
    name: 'commonSlice',
    initialState,
    reducers: {
        // setLeftMenuCollapsed(state, action: PayloadAction<boolean>) {
        //     state.leftMenuCollapsed = action.payload
        // },
    },
})

export const {} = commonSlice.actions

export default commonSlice.reducer

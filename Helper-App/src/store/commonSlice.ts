import {createSlice} from '@reduxjs/toolkit'

interface ICommonSlice {
    // xxx: boolean //
}

const initialState: ICommonSlice = {
    // xxx: false
}

export const commonSlice = createSlice({
    name: 'commonSlice',
    initialState,
    reducers: {
        // setXxx(state, action: PayloadAction<boolean>) {
        //     state.leftMenuCollapsed = action.payload
        // },
    },
})

export const {} = commonSlice.actions

export default commonSlice.reducer

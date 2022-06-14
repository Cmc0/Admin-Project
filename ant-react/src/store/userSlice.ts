import {createSlice, PayloadAction} from '@reduxjs/toolkit'
import BaseMenuDO from "../model/entity/BaseMenuDO";
import {UserBaseInfoVO} from "../api/UserController";
import LocalStorageKey from "../model/constant/LocalStorageKey";

interface IUserSlice {
  userBaseInfo: UserBaseInfoVO // 用户基本信息
  userMenuList: BaseMenuDO[] // 用户菜单
}

const initialState: IUserSlice = {
  userBaseInfo: JSON.parse(
      localStorage.getItem(LocalStorageKey.USER_BASE_INFO) || '{}'
  ),
  userMenuList: [] as BaseMenuDO[],
}

export const userSlice = createSlice({
  name: 'userSlice',
  initialState,
  reducers: {
    setUserBaseInfo: (state, action: PayloadAction<UserBaseInfoVO>) => {
      state.userBaseInfo = action.payload
      localStorage.setItem(
          LocalStorageKey.USER_BASE_INFO,
          JSON.stringify(action.payload)
      )
    },
    setUserMenuList: (state, action: PayloadAction<BaseMenuDO[]>) => {
      state.userMenuList = action.payload
    }
  }
})

export const {setUserBaseInfo, setUserMenuList} =
    userSlice.actions

export default userSlice.reducer

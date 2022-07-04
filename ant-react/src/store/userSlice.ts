import {createSlice, PayloadAction} from '@reduxjs/toolkit'
import LocalStorageKey from "../model/constant/LocalStorageKey";
import SessionStorageKey from "@/model/constant/SessionStorageKey";
import {SysMenuDO} from "@/api/SysMenuController";
import {UserSelfBaseInfoVO} from "@/api/UserSelfController";

interface IUserSlice {
  loadMenuFlag: boolean // 是否加载过菜单
  userSelfBaseInfo: UserSelfBaseInfoVO // 当前用户，基本信息
  userMenuList: SysMenuDO[] // 用户菜单
}

const initialState: IUserSlice = {
  loadMenuFlag: false,
  userSelfBaseInfo: JSON.parse(
      localStorage.getItem(LocalStorageKey.USER_SELF_BASE_INFO) || '{}'
  ),
  userMenuList: [] as SysMenuDO[],
}

function setSessionStorageLoadMenuFlag(loadMenuFlag: boolean) {
  sessionStorage.setItem(
      SessionStorageKey.LOAD_MENU_FLAG,
      String(loadMenuFlag)
  )
}

function setLocalStorageUserBaseInfo(userBaseInfo: UserSelfBaseInfoVO) {
  localStorage.setItem(
      LocalStorageKey.USER_SELF_BASE_INFO,
      JSON.stringify(userBaseInfo)
  )
}

export const userSlice = createSlice({
  name: 'userSlice',
  initialState,
  reducers: {
    setLoadMenuFlag(state, action: PayloadAction<boolean>) {
      state.loadMenuFlag = action.payload
      setSessionStorageLoadMenuFlag(action.payload)
      if (!action.payload) {
        state.userSelfBaseInfo = {} // 重置，备注：这里会由 Login页面，自动清除 localStorage，sessionStorage
        state.userMenuList = []
      }
    },
    setUserSelfBaseInfo: (state, action: PayloadAction<UserSelfBaseInfoVO>) => {
      state.userSelfBaseInfo = action.payload
      setLocalStorageUserBaseInfo(action.payload)
    },
    setUserMenuList: (state, action: PayloadAction<SysMenuDO[]>) => {
      state.userMenuList = action.payload
      state.loadMenuFlag = true
      setSessionStorageLoadMenuFlag(true)
    }
  }
})

export const {setLoadMenuFlag, setUserSelfBaseInfo, setUserMenuList} =
    userSlice.actions

export default userSlice.reducer

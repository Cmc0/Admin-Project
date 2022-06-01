import {MenuDataItem} from "@ant-design/pro-components"

export interface IMainLayoutRouterList extends MenuDataItem {
    element?: string
}

const MainLayoutRouterList: IMainLayoutRouterList[] = [
    {
        name: '数据库管理',
        path: '/main/dbManage',
        element: 'DbManageDbManage',
    },
    {
        name: 'java转换',
        path: '/main/javaConvert',
        element: 'JavaConvertJavaConvert',
    }
]

export const MainLayoutRouterPathList = MainLayoutRouterList.map((item) => item.path)

export default MainLayoutRouterList

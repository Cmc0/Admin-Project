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
        name: 'sql转java',
        path: '/main/sqlToJava',
        element: 'SqlToJavaSqlToJava',
    }
]

export const MainLayoutRouterPathList = MainLayoutRouterList.map((item) => item.path)

export default MainLayoutRouterList

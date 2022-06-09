import {MenuDataItem} from "@ant-design/pro-components"

export interface IMainLayoutRouterList extends MenuDataItem {
    element?: string
}

const MainLayoutRouterList: IMainLayoutRouterList[] = [
    {
        path: '/main/dashboard/workplace',
        element: 'dashboardWorkplaceWorkplace',
    },
]

export const MainLayoutRouterPathList = MainLayoutRouterList.map((item) => item.path)

export default MainLayoutRouterList

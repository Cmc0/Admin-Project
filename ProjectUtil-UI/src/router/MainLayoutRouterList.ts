import {MenuDataItem} from "@ant-design/pro-components"

export interface IMainLayoutRouterList extends MenuDataItem {
    element?: string
}

const MainLayoutRouterList: IMainLayoutRouterList[] = [
    {
        name: '转换',
        path: '/main/MyConvert',
        element: 'MyConvertMyConvert',
    }
]

export const MainLayoutRouterPathList = MainLayoutRouterList.map((item) => item.path)

export default MainLayoutRouterList

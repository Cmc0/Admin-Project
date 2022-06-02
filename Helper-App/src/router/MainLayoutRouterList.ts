import {IMyRouter} from "@/router/RouterMap";

const MainLayoutRouterList: IMyRouter[] = [
    {
        name: '转换',
        path: '/main/MyConvert',
        element: 'MyConvertMyConvert',
    }
]

export const MainLayoutRouterPathList = MainLayoutRouterList.map((item) => item.path)

export default MainLayoutRouterList

import {IMyRouter} from "@/router/NoLoginRouterList";

const MainLayoutRouterList: IMyRouter[] = [
    {
        name: '转换',
        path: '/main/MyConvert',
        element: 'MyConvertMyConvert',
    }
]

export const MainLayoutRouterPathList = MainLayoutRouterList.map((item) => item.path)

export default MainLayoutRouterList

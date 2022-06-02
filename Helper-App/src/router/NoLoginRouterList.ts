import {IMyRouter} from "@/router/RouterMap";

const NoLoginRouterList: IMyRouter[] = [
    {
        path: '/',
        element: 'Jump',
    },
]

export const NoLoginRouterPathList = NoLoginRouterList.map((item) => item.path)

export default NoLoginRouterList

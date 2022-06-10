import CommonConstant from "@/model/constant/CommonConstant";

interface INoLoginRouterList {
    path: string
    element: string
}

const NoLoginRouterList: INoLoginRouterList[] = [
    {
        path: '/',
        element: 'Jump',
    },
    {
        path: CommonConstant.LOGIN_PATH,
        element: 'LoginLogin',
    },
]

export default NoLoginRouterList

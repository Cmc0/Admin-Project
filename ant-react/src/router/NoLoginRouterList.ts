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
    {
        path: CommonConstant.REGISTER_PATH,
        element: 'RegisterRegister',
    },
]

export default NoLoginRouterList

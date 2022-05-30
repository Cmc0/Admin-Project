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
        path: '/login',
        element: 'LoginLogin',
    },
]

export const NoLoginRouterPathList = NoLoginRouterList.map((item) => item.path)

export default NoLoginRouterList

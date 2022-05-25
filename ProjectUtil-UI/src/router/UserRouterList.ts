interface IUserRouterList {
    path: string
    element: string
}

const UserRouterList: IUserRouterList[] = [
    {
        path: '/main/home',
        element: 'homeHome',
    },
]

export const UserRouterPathList = UserRouterList.map((item) => item.path)

export default UserRouterList

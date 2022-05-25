interface IMainLayoutRouterList {
    path: string
    element: string
}

const MainLayoutRouterList: IMainLayoutRouterList[] = [
    {
        path: '/main/home',
        element: 'homeHome',
    },
]

export const MainLayoutRouterPathList = MainLayoutRouterList.map((item) => item.path)

export default MainLayoutRouterList

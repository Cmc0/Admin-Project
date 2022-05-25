interface IMainLayoutRouterList {
    path: string
    element: string
}

const MainLayoutRouterList: IMainLayoutRouterList[] = [
    {
        path: '/main/dbManage',
        element: 'dbManageDbManage',
    },
]

export const MainLayoutRouterPathList = MainLayoutRouterList.map((item) => item.path)

export default MainLayoutRouterList

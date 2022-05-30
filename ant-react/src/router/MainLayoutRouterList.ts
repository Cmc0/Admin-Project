interface IMainLayoutRouterList {
    path: string
    element: string
}

const MainLayoutRouterList: IMainLayoutRouterList[] = [
    {
        path: '/main/dbManage',
        element: 'DbManageDbManage',
    },
]

export const MainLayoutRouterPathList = MainLayoutRouterList.map((item) => item.path)

export default MainLayoutRouterList

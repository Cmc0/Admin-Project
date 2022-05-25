interface INoLoginRouterList {
    path: string
    element: string
}

const NoLoginRouterList: INoLoginRouterList[] = [
    {
        path: '/',
        element: 'Jump',
    },
]

export const NoLoginRouterPathList = NoLoginRouterList.map((item) => item.path)

export default NoLoginRouterList

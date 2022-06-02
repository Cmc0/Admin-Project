export interface IMyRouter {
    name?: string,
    path: string,
    element?: string
}

const NoLoginRouterList: IMyRouter[] = [
    {
        path: '/',
        element: 'Jump',
    },
]

export const NoLoginRouterPathList = NoLoginRouterList.map((item) => item.path)

export default NoLoginRouterList

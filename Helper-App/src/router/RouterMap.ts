import Jump from "@/componse/Jump/Jump";
import MainLayout from "@/componse/MainLayout/MainLayout";

// 下划线转换驼峰
export function toHump(name: string) {
    return name.replace(/\_(\w)/g, (all, letter) => {
        return letter.toUpperCase()
    })
}

export interface IMyRouter {
    name?: string,
    path: string,
    element?: string
}

interface IRouterMap {
    element: any
}

const RouterMap: Record<string, IRouterMap> = {} // 路由 map

// 自动获取路由 map
const fileObj: Record<string, { [key: string]: any }> = import.meta.globEager(
    '/src/page/**/*.tsx'
)

Object.keys(fileObj).forEach((item: string) => {
    const split = item.split('/');
    if ((split[split.length - 2] + '.tsx') !== split[split.length - 1]) {
        return // 只要：/src/page/Home/Home.tsx
    }
    const fileName = toHump(
        item.split('/src/page/')[1].split('.tsx')[0].replaceAll('/', '_')
    ) // 例如：/src/page/Home/Home -> HomeHome
    RouterMap[fileName] = {
        element: fileObj[item].default,
    }
})

// 手动添加路由
RouterMap['Jump'] = {
    element: Jump,
}
RouterMap['MainLayout'] = {
    element: MainLayout,
}

export const RouterMapKeyList = Object.keys(RouterMap)

export default RouterMap

import MainLayout from "@/componse/MainLayout/MainLayout";
import Jump from "@/componse/Jump/Jump";

// 下划线转换驼峰
export function toHump(name: string) {
    return name.replace(/\_(\w)/g, (all, letter) => {
        return letter.toUpperCase()
    })
}

interface IRouterMapItem {
    element: any
}

const RouterMap: Record<string, IRouterMapItem> = {} // 路由 map

// 自动获取路由 map
const fileObj: Record<string, { [key: string]: any }> = import.meta.globEager(
    '/src/page/**/*.tsx'
)

Object.keys(fileObj).forEach((item: string) => {
    const split = item.split('/');
    if ((split[split.length - 2] + '.ts') !== split[split.length - 1]) {
        return // 只要：/src/page/home/home.ts
    }
    const fileName = toHump(
        item.split('/src/page/')[1].split('.tsx')[0].replaceAll('/', '_')
    ) // 例如：/src/page/home/home -> homeHome
    RouterMap[fileName] = {
        element: fileObj[item].default,
    }
})

// 手动添加路由
RouterMap['MainLayout'] = {
    element: MainLayout,
}
RouterMap['Jump'] = {
    element: Jump,
}

export const RouterMapKeyList = Object.keys(RouterMap)

export default RouterMap

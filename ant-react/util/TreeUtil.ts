interface IFlatTree {
    children?: any[]
}

// 扁平化树结构，备注：hasParentFlag：是否需要父节点，checkFun：return true的节点，才需要添加
export function FlatTree<T extends IFlatTree>(
    data?: T[],
    hasParentFlag: boolean = true,
    checkFun?: (item: T) => boolean
) {
    let resList: T[] = []
    if (!data) {
        return resList
    }
    data.forEach((item) => {
        let flag = true
        if (checkFun) {
            flag = checkFun(item) // 过滤不要的节点
        }
        if (flag) {
            const hasChildren = item.children && item.children.length
            if (hasChildren) {
                if (hasParentFlag) {
                    resList.push(JSON.parse(JSON.stringify({...item, children: null}))) // 添加到返回值里
                }
                resList = resList.concat(
                    FlatTree(item.children, hasParentFlag, checkFun)
                )
            } else {
                resList.push(JSON.parse(JSON.stringify({...item, children: null}))) // 添加到返回值里
            }
        }
    })
    return resList
}

// 获取树结构，所有 有子节点的 id集合
export function GetIdListForHasChildrenNode(data: any[]) {
    let resList: number[] = []
    data.forEach((item) => {
        if (item.children && item.children.length) {
            resList.push(item.id)
            resList = resList.concat(GetIdListForHasChildrenNode(item.children))
        }
    })
    return resList
}

interface IListToTree {
    [x: string]: any
}

// list 转 tree 结构，比原始的递归快
// childrenFlag：true：children始终为 [] false：children为空时，children = ''
export function ListToTree<T extends IListToTree>(
    list: T[],
    childrenFlag: boolean = true,
    pid: string | number = 0,
    childrenName: string = 'children'
) {
    const resList: T[] = [] // 本方法返回值
    const obj: any = {} // 格式：{ id: {...item} }

    list.forEach((item: any) => {
        if (obj[item.id]) {
            // 如果存在 当前元素，则补充其他属性
            item = {...item, ...obj[item.id]}
        } else {
            // 如果不存在 当前元素
            if (!item[childrenName]) {
                // 如果 item不存在 children
                if (childrenFlag) {
                    item[childrenName] = []
                } else {
                    item[childrenName] = ''
                }
            }
            obj[item.id] = item // 赋值到 obj里面
        }

        if (item.parentId === pid) {
            resList.push(item) // 添加到返回值里
            return
        }

        if (obj[item.parentId]) {
            // 如果存在 父级元素
            if (obj[item.parentId][childrenName]) {
                obj[item.parentId][childrenName].push(item)
            } else {
                obj[item.parentId][childrenName] = [item]
            }
        } else {
            // 如果不存在 父级元素
            obj[item.parentId] = {[childrenName]: [item]}
        }
    })

    return resList
}

import {TWebSocketType} from "@/model/constant/LocalStorageKey";
import {ProSchemaValueEnumType} from "@ant-design/pro-components";
import {sysDictPage} from "@/api/SysDictController";
import {sysMenuPage} from "@/api/SysMenuController";
import {ListToTree} from "./TreeUtil";
import {sysDeptPage} from "@/api/SysDeptController";
import {sysJobPage} from "@/api/SysJobController";
import {sysRolePage} from "@/api/SysRoleController";
import {sysAreaPage} from "@/api/SysAreaController";
import DictLongListVO from "@/model/vo/DictLongListVO";
import {sysUserDictList} from "@/api/SysUserController";

export const YesNoDict = new Map<any, ProSchemaValueEnumType>();
YesNoDict.set(true, {text: '是', status: 'success'})
YesNoDict.set(false, {text: '否', status: 'error'})

export const YesNoBaseDict = new Map<any, ProSchemaValueEnumType>();
YesNoBaseDict.set(true, {text: '是'})
YesNoBaseDict.set(false, {text: '否'})

export const WebSocketTypeDict = new Map<TWebSocketType, ProSchemaValueEnumType>();
WebSocketTypeDict.set('1', {text: '在线', status: 'success'})
WebSocketTypeDict.set('2', {text: '隐身', status: 'warning'})

export const BulletinTypeDict = new Map<TWebSocketType, ProSchemaValueEnumType>();
BulletinTypeDict.set('1', {text: '草稿', status: 'warning'})
BulletinTypeDict.set('2', {text: '公示', status: 'processing'})

// 根据list和 value，获取字典的 label值
export function getByValueFromDictList(
    dictList: DictLongListVO [],
    value: number,
    defaultValue: string = '-'
) {
    let res: string | undefined = defaultValue
    dictList.some((item) => {
        if (item.value === value) {
            res = item.label
            return true // 结束当前循环
        }
    })
    return res
}

// 根据list和 valueList，获取字典的 labelList值
export function getByValueFromDictListPro(
    dictList: DictLongListVO [],
    valueList?: number[],
    defaultValue: string = '-',
    separator: string = '，'
) {
    let resList: string[] = []
    if (dictList && valueList && valueList.length) {
        dictList.forEach((item) => {
            if (valueList.includes(item.value)) {
                resList.push(item.label)
            }
        })
    }
    return resList.length ? resList.join(separator) : defaultValue
}

export function RequestGetDictList(dictKey: string) {
    return new Promise<DictLongListVO[]>(async resolve => {
        await sysDictPage({pageSize: -1, type: 2, dictKey}).then(res => {
            let dictList: DictLongListVO[] = []
            if (res.data) {
                dictList = res.data.map(item => ({
                    label: item.name!,
                    value: item.value!,
                }));
            }
            resolve(dictList)
        })
    })
}

export function GetUserDictList(addAdminFlag: boolean = true) {
    return new Promise<DictLongListVO[]>(async resolve => {
        await sysUserDictList({addAdminFlag}).then(res => {
            resolve(res.data || [])
        })
    })
}

export interface IMyTree extends DictLongListVO {
    id: number
    key: number
    label: string // 备注：和 title是一样的值
    title: string
    parentId: number
    orderNo: number
    children?: IMyTree []
}

export function GetMenuDictTreeList(toTreeFlag: boolean = true) {
    return new Promise<IMyTree[]>(async resolve => {
        await sysMenuPage({pageSize: -1}).then(res => {
            let dictList: IMyTree[] = []
            if (res.data) {
                dictList = res.data.map(item => ({
                    id: item.id!,
                    key: item.id!,
                    value: item.id!,
                    label: item.name!,
                    title: item.name!,
                    parentId: item.parentId!,
                    orderNo: item.orderNo!,
                }));
            }
            if (toTreeFlag) {
                resolve(ListToTree(dictList))
            } else {
                resolve(dictList)
            }
        })
    })
}

export function GetDeptDictList(toTreeFlag: boolean = true) {
    return new Promise<IMyTree[]>(async resolve => {
        await sysDeptPage({pageSize: -1}).then(res => {
            let dictList: IMyTree[] = []
            if (res.data) {
                dictList = res.data.map(item => ({
                    id: item.id!,
                    key: item.id!,
                    value: item.id!,
                    label: item.name!,
                    title: item.name!,
                    parentId: item.parentId!,
                    orderNo: item.orderNo!
                }));
            }
            if (toTreeFlag) {
                resolve(ListToTree(dictList))
            } else {
                resolve(dictList)
            }
        })
    })
}

export function GetAreaDictList(toTreeFlag: boolean = true) {
    return new Promise<IMyTree[]>(async resolve => {
        await sysAreaPage({pageSize: -1}).then(res => {
            let dictList: IMyTree[] = []
            if (res.data) {
                dictList = res.data.map(item => ({
                    id: item.id!,
                    key: item.id!,
                    value: item.id!,
                    label: item.name!,
                    title: item.name!,
                    parentId: item.parentId!,
                    orderNo: item.orderNo!
                }));
            }
            if (toTreeFlag) {
                resolve(ListToTree(dictList))
            } else {
                resolve(dictList)
            }
        })
    })
}

export function GetJobDictList(toTreeFlag: boolean = true) {
    return new Promise<IMyTree[]>(async resolve => {
        await sysJobPage({pageSize: -1}).then(res => {
            let dictList: IMyTree[] = []
            if (res.data) {
                dictList = res.data.map(item => ({
                    id: item.id!,
                    key: item.id!,
                    value: item.id!,
                    label: item.name!,
                    title: item.name!,
                    parentId: item.parentId!,
                    orderNo: item.orderNo!
                }));
            }
            if (toTreeFlag) {
                resolve(ListToTree(dictList))
            } else {
                resolve(dictList)
            }
        })
    })
}

export function GetRoleDictList() {
    return new Promise<DictLongListVO[]>(async resolve => {
        await sysRolePage({pageSize: -1}).then(res => {
            let dictList: DictLongListVO[] = []
            if (res.data) {
                dictList = res.data.map(item => ({
                    label: item.name!,
                    value: item.id!,
                }));
            }
            resolve(dictList)
        })
    })
}

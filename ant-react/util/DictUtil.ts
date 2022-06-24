import {TWebSocketType} from "@/model/constant/LocalStorageKey";
import {ProSchemaValueEnumType} from "@ant-design/pro-components";
import {sysDictPage} from "@/api/SysDictController";
import {sysUserPage} from "@/api/SysUserController";
import {sysMenuPage} from "@/api/SysMenuController";
import {ListToTree} from "./TreeUtil";

export const YesNoDict = new Map<any, ProSchemaValueEnumType>();
YesNoDict.set(true, {text: '是', status: 'success'})
YesNoDict.set(false, {text: '否', status: 'error'})

export const YesNoBaseDict = new Map<any, ProSchemaValueEnumType>();
YesNoBaseDict.set(true, {text: '是'})
YesNoBaseDict.set(false, {text: '否'})

export const WebSocketTypeDict = new Map<TWebSocketType, ProSchemaValueEnumType>();
WebSocketTypeDict.set('1', {text: '在线', status: 'success'})
WebSocketTypeDict.set('2', {text: '隐身', status: 'warning'})

export interface IMyOption {
    label?: string
    value?: number | string
}

export function RequestGetDictList(dictKey: string) {
    return new Promise<IMyOption[]>(async resolve => {
        await sysDictPage({pageSize: -1, type: 2, dictKey}).then(res => {
            let dictList: IMyOption[] = []
            if (res.data) {
                dictList = res.data.map(item => ({
                    label: item.name,
                    value: item.value,
                } as IMyOption));
            }
            resolve(dictList)
        })
    })
}

export function GetUserDictList(addAdminFlag: boolean = true) {
    return new Promise<IMyOption[]>(async resolve => {
        await sysUserPage({pageSize: -1, addAdminFlag}).then(res => {
            let dictList: IMyOption[] = []
            if (res.data) {
                dictList = res.data.map(item => ({
                    label: item.nickname,
                    value: item.id,
                } as IMyOption));
            }
            resolve(dictList)
        })
    })
}

export interface IMyTree {
    id?: number
    key?: number
    title?: string
    value?: number
    parentId: number,
    orderNo: number,
    children: IMyTree[]
}

export function GetMenuDictList(addAdminFlag: boolean = true) {
    return new Promise<IMyTree[]>(async resolve => {
        await sysMenuPage({pageSize: -1}).then(res => {
            let dictList: IMyTree[] = []
            if (res.data) {
                dictList = res.data.map(item => ({
                    id: item.id,
                    key: item.id,
                    value: item.id,
                    title: item.name,
                    parentId: item.parentId,
                    orderNo: item.orderNo
                } as IMyTree));
            }
            resolve(ListToTree(dictList))
        })
    })
}

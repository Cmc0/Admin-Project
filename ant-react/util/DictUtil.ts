import {TWebSocketType} from "@/model/constant/LocalStorageKey";
import {ProSchemaValueEnumType} from "@ant-design/pro-components";
import {sysDictPage} from "@/api/SysDictController";
import {sysUserSelectList} from "@/api/SysUserController";
import {sysMenuPage} from "@/api/SysMenuController";
import {ListToTree} from "./TreeUtil";
import {sysDeptPage} from "@/api/SysDeptController";
import {sysJobPage} from "@/api/SysJobController";
import {sysRolePage} from "@/api/SysRoleController";
import {sysAreaPage} from "@/api/SysAreaController";
import DictListVO from "@/model/vo/DictListVO";

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

export function RequestGetDictList(dictKey: string) {
    return new Promise<DictListVO[]>(async resolve => {
        await sysDictPage({pageSize: -1, type: 2, dictKey}).then(res => {
            let dictList: DictListVO[] = []
            if (res.data) {
                dictList = res.data.map(item => ({
                    label: item.name,
                    value: item.value,
                } as DictListVO));
            }
            resolve(dictList)
        })
    })
}

export function GetUserDictList(addAdminFlag: boolean = true) {
    return new Promise<DictListVO[]>(async resolve => {
        await sysUserSelectList({addAdminFlag}).then(res => {
            resolve(res.data || [])
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

export function GetMenuDictList() {
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

export function GetDeptDictList() {
    return new Promise<IMyTree[]>(async resolve => {
        await sysDeptPage({pageSize: -1}).then(res => {
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

export function GetAreaDictList() {
    return new Promise<IMyTree[]>(async resolve => {
        await sysAreaPage({pageSize: -1}).then(res => {
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

export function GetJobDictList() {
    return new Promise<IMyTree[]>(async resolve => {
        await sysJobPage({pageSize: -1}).then(res => {
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

export function GetRoleDictList() {
    return new Promise<DictListVO[]>(async resolve => {
        await sysRolePage({pageSize: -1}).then(res => {
            let dictList: DictListVO[] = []
            if (res.data) {
                dictList = res.data.map(item => ({
                    label: item.name,
                    value: item.id,
                } as DictListVO));
            }
            resolve(dictList)
        })
    })
}

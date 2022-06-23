import {TWebSocketType} from "@/model/constant/LocalStorageKey";
import {ProSchemaValueEnumType} from "@ant-design/pro-components";

export const YesNoDict = new Map<any, ProSchemaValueEnumType>();
YesNoDict.set(true, {text: '是', status: 'success'})
YesNoDict.set(false, {text: '否', status: 'error'})

export const YesNoBaseDict = new Map<any, ProSchemaValueEnumType>();
YesNoBaseDict.set(true, {text: '是'})
YesNoBaseDict.set(false, {text: '否'})

export const WebSocketTypeDict = new Map<TWebSocketType, ProSchemaValueEnumType>();
WebSocketTypeDict.set('1', {text: '在线', status: 'success'})
WebSocketTypeDict.set('2', {text: '隐身', status: 'warning'})

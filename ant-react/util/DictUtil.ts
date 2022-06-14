import {ProSchemaValueEnumType} from "@ant-design/pro-utils/lib/typing";

export const YesNoEnum: Record<any, ProSchemaValueEnumType> = {
    true: {text: '是', status: 'success'},
    false: {text: '否', status: 'error'}
}

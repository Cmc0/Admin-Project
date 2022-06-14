import BaseMenuDO from "@/model/entity/BaseMenuDO";
import {ProColumns, ProTable} from "@ant-design/pro-components";
import {Button, Space} from "antd";
import {MenuPageDTO, menuTree} from "@/api/MenuController";
import {HomeFilled} from "@ant-design/icons/lib";
import MyIcon from "@/componse/MyIcon/MyIcon";
import {YesNoEnum} from "../../../../util/DictUtil";

const columnList: ProColumns<BaseMenuDO>[] = [
    {
        title: '菜单名',
        dataIndex: 'name',
        render: (text, record, index, action) => {
            return (
                <Space className="ai-c">
                    {record.firstFlag && <HomeFilled className="cyan1" title="起始页面"/>}
                    {record.icon && <MyIcon icon={record.icon}/>}
                    {record.parentId + '' === '0' ? <strong>{text}</strong> : text}
                </Space>
            )
        },
    },
    {title: '路径', dataIndex: 'path'},
    {title: '权限', dataIndex: 'auths'},
    {
        title: '路由',
        dataIndex: 'router',
        // valueType: 'select',
        // request: async () => {
        //     return RouterMapKeyList
        // }
        // fieldProps: {
        //     options: RouterMapKeyList
        // },
    },
    {title: '排序号', dataIndex: 'orderNo', hideInSearch: true},
    {
        title: '显示',
        dataIndex: 'showFlag',
        valueEnum: YesNoEnum
    },
    {
        title: '启用',
        dataIndex: 'enableFlag',
        valueEnum: YesNoEnum
    },
];

export default function () {

    return <ProTable<BaseMenuDO, MenuPageDTO>
        rowKey={"id"}
        pagination={{
            showQuickJumper: true,
        }}
        revalidateOnFocus={false}
        rowSelection={{}}
        columns={columnList}
        options={{
            density: false,
            fullScreen: true,
        }}
        request={(params, sort, filter) => {
            return menuTree({...params, sort})
        }}
        tableAlertOptionRender={({selectedRowKeys, selectedRows, onCleanSelected}) => (
            <>
                <Button type="link" onClick={onCleanSelected}>取消选择</Button>
            </>
        )}
    >

    </ProTable>

}

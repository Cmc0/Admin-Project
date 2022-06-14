import BaseMenuDO from "@/model/entity/BaseMenuDO";
import {ColumnsState, ProColumns, ProTable} from "@ant-design/pro-components";
import {Button, Space} from "antd";
import {MenuPageDTO, menuTree} from "@/api/MenuController";
import {HomeFilled} from "@ant-design/icons/lib";
import MyIcon from "@/componse/MyIcon/MyIcon";
import {YesNoEnum} from "../../../../util/DictUtil";
import {useState} from "react";
import {RouterDict} from "@/router/RouterMap";

const columnList: ProColumns<BaseMenuDO>[] = [
    {
        title: '菜单名',
        dataIndex: 'name',
        render: (dom, entity) => {
            return (
                <Space className="ai-c">
                    {entity.firstFlag && <HomeFilled className="cyan1" title="起始页面"/>}
                    {entity.icon && <MyIcon icon={entity.icon}/>}
                    {entity.parentId + '' === '0' ? <strong>{dom}</strong> : dom}
                </Space>
            )
        },
    },
    {title: '路径', dataIndex: 'path'},
    {title: '权限', dataIndex: 'auths'},
    {
        title: '路由',
        dataIndex: 'router',
        valueType: 'select',
        request: async () => {
            return RouterDict
        }
    },
    {title: '排序号', dataIndex: 'orderNo', hideInSearch: true},
    {
        title: '起始页面',
        dataIndex: 'firstFlag',
        valueEnum: YesNoEnum,
    },
    {
        title: '权限菜单',
        dataIndex: 'authFlag',
        valueEnum: YesNoEnum,
    },
    {
        title: '外链',
        dataIndex: 'linkFlag',
        valueEnum: YesNoEnum,
    },
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
    {
        title: '修改时间',
        dataIndex: 'updateTime',
        valueType: 'dateTimeRange',
        render: (dom, entity) => {
            return entity.updateTime
        },
        search: {
            transform: (value: string) => ({startTime: value[0], endTime: value[1]}),
        },
    },
];

export default function () {

    const [columnsStateMap, setColumnsStateMap] = useState<Record<string, ColumnsState>>(
        {
            firstFlag: {show: false,},
            authFlag: {show: false,},
            linkFlag: {show: false,},
        });

    return <ProTable<BaseMenuDO, MenuPageDTO>
        rowKey={"id"}
        pagination={{
            showQuickJumper: true,
        }}
        columnsState={{
            value: columnsStateMap,
            onChange: setColumnsStateMap,
        }}
        revalidateOnFocus={false}
        rowSelection={{}}
        columns={columnList}
        options={{
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

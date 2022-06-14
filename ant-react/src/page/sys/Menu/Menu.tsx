import BaseMenuDO from "@/model/entity/BaseMenuDO";
import {ProColumns, ProTable} from "@ant-design/pro-components";
import {Button} from "antd";
import {MenuPageDTO, menuTree} from "@/api/MenuController";

const columnList: ProColumns<BaseMenuDO>[] = [
    {
        title: '#',
        dataIndex: 'index',
        valueType: 'index',
        width: 48,
    },
    {
        title: '菜单名',
        dataIndex: 'name',
    },
    {
        title: '路径',
        dataIndex: 'path',
    }
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

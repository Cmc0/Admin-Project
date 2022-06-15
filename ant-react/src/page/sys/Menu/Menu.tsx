import BaseMenuDO from "@/model/entity/BaseMenuDO";
import {BetaSchemaForm, ColumnsState, ProTable} from "@ant-design/pro-components";
import {Button, Dropdown, Menu} from "antd";
import {MenuInsertOrUpdateDTO, MenuPageDTO, menuTree} from "@/api/MenuController";
import {ColumnHeightOutlined, EllipsisOutlined, VerticalAlignMiddleOutlined} from "@ant-design/icons/lib";
import React, {useRef, useState} from "react";
import {GetIdListForHasChildrenNode} from "../../../../util/TreeUtil";
import TableColumnList from "@/page/sys/Menu/TableColumnList";
import SchemaFormColumnList from "@/page/sys/Menu/SchemaFormColumnList";


export default function () {

    const [columnsStateMap, setColumnsStateMap] = useState<Record<string, ColumnsState>>(
        {
            firstFlag: {show: false,},
            authFlag: {show: false,},
            linkFlag: {show: false,},
            updateTime: {show: false,},
        });

    const [expandedRowKeys, setExpandedRowKeys] = useState<number[]>([]);

    const hasChildrenIdList = useRef<number[]>([]); // 有子节点的 idList

    const [treeList, setTreeList] = useState<BaseMenuDO[]>([]);

    return <>
        <ProTable<BaseMenuDO, MenuPageDTO>
            rowKey={"id"}
            pagination={{
                showQuickJumper: true,
            }}
            columnEmptyText={false}
            columnsState={{
                value: columnsStateMap,
                onChange: setColumnsStateMap,
            }}
            expandable={{
                expandedRowKeys,
                onExpandedRowsChange: (expandedRows) => {
                    setExpandedRowKeys(expandedRows as number[])
                }
            }}
            revalidateOnFocus={false}
            rowSelection={{}}
            columns={TableColumnList}
            options={{
                fullScreen: true,
            }}
            request={(params, sort, filter) => {
                return menuTree({...params, sort})
            }}
            postData={(data) => {
                setTreeList(data)
                hasChildrenIdList.current = GetIdListForHasChildrenNode(data)
                return data
            }}
            toolbar={{
                title:
                    <Dropdown
                        overlay={<Menu items={[
                            {
                                key: '1',
                                label: <a onClick={() => {
                                    setExpandedRowKeys(hasChildrenIdList.current)
                                }}>
                                    展开全部
                                </a>,
                                icon: <ColumnHeightOutlined/>
                            },
                            {
                                key: '2',
                                label: <a onClick={() => {
                                    setExpandedRowKeys([])
                                }}>
                                    收起全部
                                </a>,
                                icon: <VerticalAlignMiddleOutlined/>
                            },
                        ]}/>}
                    >
                        <Button size={"small"} icon={<EllipsisOutlined/>}/>
                    </Dropdown>,
                actions: [
                    <BetaSchemaForm<MenuInsertOrUpdateDTO>
                        trigger={<Button type="primary">新建菜单</Button>}
                        title={"新建菜单"}
                        layoutType={"ModalForm"}
                        grid
                        rowProps={{
                            gutter: 16,
                        }}
                        colProps={{
                            span: 12
                        }}
                        shouldUpdate={false}
                        isKeyPressSubmit
                        request={async () => {
                            return {}
                        }}
                        columns={SchemaFormColumnList(treeList)}
                        onFinish={async (values) => {
                            console.log(values);
                        }}
                    />
                ],
            }}
            tableAlertOptionRender={({selectedRowKeys, selectedRows, onCleanSelected}) => (
                <>
                    <Button type="link" onClick={onCleanSelected}>取消选择</Button>
                </>
            )}
        >
        </ProTable>
    </>

}

import {ActionType, BetaSchemaForm, ProColumns} from "@ant-design/pro-components";
import BaseMenuDO from "@/model/entity/BaseMenuDO";
import {Dropdown, Menu, Space} from "antd";
import {EllipsisOutlined, HomeFilled} from "@ant-design/icons/lib";
import MyIcon from "@/componse/MyIcon/MyIcon";
import {RouterMapKeyList} from "@/router/RouterMap";
import {YesNoDict} from "../../../../util/DictUtil";
import React from "react";
import {menuDeleteByIdSet, menuInsertOrUpdate, MenuInsertOrUpdateDTO} from "@/api/MenuController";
import {execConfirm, ToastSuccess} from "../../../../util/ToastUtil";
import {CalcOrderNo, defaultOrderNo} from "../../../../util/TreeUtil";
import CommonConstant from "@/model/constant/CommonConstant";

const QuicklyAddAuth = "快速添加权限"

const TableColumnList = (currentForm: React.MutableRefObject<MenuInsertOrUpdateDTO | null>, setFormVisible: React.Dispatch<React.SetStateAction<boolean>>, actionRef: React.RefObject<ActionType>): ProColumns<BaseMenuDO>[] => [
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
        fieldProps: {
            showSearch: true,
            options: RouterMapKeyList,
        }
    },
    {title: '排序号', dataIndex: 'orderNo', hideInSearch: true},
    {
        title: '起始页面',
        dataIndex: 'firstFlag',
        valueEnum: YesNoDict,
    },
    {
        title: '外链',
        dataIndex: 'linkFlag',
        valueEnum: YesNoDict,
    },
    {
        title: '显示',
        dataIndex: 'showFlag',
        valueEnum: YesNoDict
    },
    {
        title: '权限菜单',
        dataIndex: 'authFlag',
        valueEnum: YesNoDict,
    },
    {
        title: '启用',
        dataIndex: 'enableFlag',
        valueEnum: YesNoDict
    },
    {
        title: '修改时间',
        dataIndex: 'updateTime',
        hideInSearch: true
    },
    {
        title: '操作',
        width: 180,
        dataIndex: 'option',
        valueType: 'option',
        render: (dom, entity) => [
            <a key="1" onClick={() => {
                currentForm.current = {id: entity.id}
                setFormVisible(true)
            }}>编辑</a>,
            <a className={"red3"} key="2" onClick={() => {
                execConfirm(() => {
                    return menuDeleteByIdSet({idSet: [entity.id!]}).then(res => {
                        ToastSuccess(res.msg)
                        actionRef.current?.reload()
                    })
                }, undefined, `确定删除【${entity.name}】吗？`)
            }}>删除</a>,
            <Dropdown overlay={<Menu items={[
                {
                    key: '1',
                    label: <a onClick={() => {
                        currentForm.current = {parentId: entity.id}
                        CalcOrderNo(currentForm.current, entity)
                        setFormVisible(true)
                    }}>
                        添加下级
                    </a>,
                },
                {
                    key: '2',
                    label:
                        <BetaSchemaForm<MenuInsertOrUpdateDTO>
                            layoutType={"ModalForm"}
                            modalProps={{
                                maskClosable: false
                            }}
                            isKeyPressSubmit
                            width={450}
                            title={QuicklyAddAuth}
                            trigger={<a>{QuicklyAddAuth}</a>}
                            onFinish={async (form) => {
                                const formTemp: MenuInsertOrUpdateDTO = {
                                    parentId: entity.id,
                                    authFlag: true,
                                    enableFlag: true
                                }
                                menuInsertOrUpdate({
                                    ...formTemp,
                                    name: '新增修改',
                                    auths: form.auths + ":insertOrUpdate",
                                    orderNo: defaultOrderNo
                                })
                                menuInsertOrUpdate({
                                    ...formTemp,
                                    name: '列表查询',
                                    auths: form.auths + ":page",
                                    orderNo: defaultOrderNo - 10
                                })
                                menuInsertOrUpdate({
                                    ...formTemp,
                                    name: '删除',
                                    auths: form.auths + ":deleteByIdSet",
                                    orderNo: defaultOrderNo - 20
                                })
                                await menuInsertOrUpdate({
                                    ...formTemp,
                                    name: '查看详情',
                                    auths: form.auths + ":infoById",
                                    orderNo: defaultOrderNo - 30
                                }).then(res => {
                                    ToastSuccess(res.msg)
                                    setTimeout(() => {
                                        actionRef.current?.reload()
                                    }, CommonConstant.MODAL_ANIM_TIME) // 要等 modal关闭动画完成
                                })
                                return true
                            }}
                            columns={
                                [
                                    {
                                        dataIndex: "auths", title: '权限前缀', formItemProps: {
                                            rules: [
                                                {
                                                    required: true,
                                                },
                                            ],
                                        }
                                    }
                                ]
                            }
                        >
                        </BetaSchemaForm>,
                },
            ]}>
            </Menu>}>
                <a><EllipsisOutlined/></a>
            </Dropdown>,
        ],
    },
];

export default TableColumnList

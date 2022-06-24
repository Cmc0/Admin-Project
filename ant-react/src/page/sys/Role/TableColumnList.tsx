import {ActionType, ModalForm, ProColumns, ProFormText} from "@ant-design/pro-components";
import SysMenuDO from "@/model/entity/SysMenuDO";
import {Dropdown, Menu} from "antd";
import {EllipsisOutlined} from "@ant-design/icons/lib";
import {YesNoDict} from "../../../../util/DictUtil";
import React from "react";
import {execConfirm, ToastSuccess} from "../../../../util/ToastUtil";
import {CalcOrderNo, defaultOrderNo} from "../../../../util/TreeUtil";
import CommonConstant from "@/model/constant/CommonConstant";
import {sysMenuDeleteByIdSet, sysMenuInsertOrUpdate, SysMenuInsertOrUpdateDTO} from "@/api/SysMenuController";

const QuicklyAddAuth = "快速添加权限"

const TableColumnList = (currentForm: React.MutableRefObject<SysMenuInsertOrUpdateDTO | null>, setFormVisible: React.Dispatch<React.SetStateAction<boolean>>, actionRef: React.RefObject<ActionType>): ProColumns<SysMenuDO>[] => [
    {title: '角色名', dataIndex: 'name'},
    {title: '备注', dataIndex: 'remark'},
    {
        title: '启用',
        dataIndex: 'enableFlag',
        valueEnum: YesNoDict
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
            <a key="2" className={"red3"} onClick={() => {
                execConfirm(() => {
                    return sysMenuDeleteByIdSet({idSet: [entity.id!]}).then(res => {
                        ToastSuccess(res.msg)
                        actionRef.current?.reload()
                    })
                }, undefined, `确定删除【${entity.name}】吗？`)
            }}>删除</a>,
            <Dropdown key="3" overlay={<Menu items={[
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
                        <ModalForm<SysMenuInsertOrUpdateDTO>
                            modalProps={{
                                maskClosable: false
                            }}
                            isKeyPressSubmit
                            width={450}
                            title={QuicklyAddAuth}
                            trigger={<a>{QuicklyAddAuth}</a>}
                            onFinish={async (form) => {
                                const formTemp: SysMenuInsertOrUpdateDTO = {
                                    parentId: entity.id,
                                    authFlag: true,
                                    enableFlag: true
                                }
                                sysMenuInsertOrUpdate({
                                    ...formTemp,
                                    name: '新增修改',
                                    auths: form.auths + ":insertOrUpdate",
                                    orderNo: defaultOrderNo
                                })
                                sysMenuInsertOrUpdate({
                                    ...formTemp,
                                    name: '列表查询',
                                    auths: form.auths + ":page",
                                    orderNo: defaultOrderNo - 10
                                })
                                sysMenuInsertOrUpdate({
                                    ...formTemp,
                                    name: '删除',
                                    auths: form.auths + ":deleteByIdSet",
                                    orderNo: defaultOrderNo - 20
                                })
                                await sysMenuInsertOrUpdate({
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
                        >
                            <ProFormText name={"auths"} label={"权限前缀"} rules={[{required: true}]}/>
                        </ModalForm>,
                },
            ]}>
            </Menu>}>
                <a><EllipsisOutlined/></a>
            </Dropdown>,
        ],
    },
];

export default TableColumnList

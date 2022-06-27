import React, {useRef, useState} from "react";
import {ActionType, ProTable} from "@ant-design/pro-components";
import {Button, Form} from "antd";
import {PlusOutlined} from "@ant-design/icons/lib";
import {SysUserInsertOrUpdateDTO, sysUserPage, SysUserPageDTO, SysUserPageVO} from "@/api/SysUserController";
import TableColumnList from "@/page/sys/User/TableColumnList";

export default function () {

    const actionRef = useRef<ActionType>(null)

    const [useForm] = Form.useForm<SysUserInsertOrUpdateDTO>();

    const [formVisible, setFormVisible] = useState<boolean>(false);

    const currentForm = useRef<SysUserInsertOrUpdateDTO>({})

    return (
        <>
            <ProTable<SysUserPageVO, SysUserPageDTO>
                actionRef={actionRef}
                rowKey={"id"}
                pagination={{
                    showQuickJumper: true,
                    showSizeChanger: true,
                }}
                columnEmptyText={false}
                revalidateOnFocus={false}
                columns={TableColumnList(currentForm, setFormVisible, actionRef)}
                options={{
                    fullScreen: true,
                }}
                request={(params, sort, filter) => {
                    return sysUserPage({...params, enableFlag: true, sort})
                }}
                toolbar={{
                    actions: [
                        <Button key={"1"} icon={<PlusOutlined/>} type="primary" onClick={() => {
                            currentForm.current = {}
                            setFormVisible(true)
                        }}>新建</Button>
                    ],
                }}
            >
            </ProTable>
        </>
    )
}

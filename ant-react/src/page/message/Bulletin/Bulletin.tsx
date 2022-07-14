import React from "react";
import {ProTable} from "@ant-design/pro-components";
import {SysBulletinDO, sysBulletinUserSelfPage, SysBulletinUserSelfPageDTO} from "@/api/SysBulletinController";
import TableColumnList from "@/page/message/Bulletin/TableColumnList";

export default function () {
    return (
        <>
            <ProTable<SysBulletinDO, SysBulletinUserSelfPageDTO>
                rowKey={"id"}
                pagination={{
                    showQuickJumper: true,
                    showSizeChanger: true,
                }}
                columnEmptyText={false}
                revalidateOnFocus={false}
                columns={TableColumnList()}
                options={{
                    fullScreen: true,
                }}
                request={(params, sort, filter) => {
                    return sysBulletinUserSelfPage({...params, sort})
                }}
            >
            </ProTable>
        </>
    )
}

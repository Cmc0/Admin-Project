import CodeGeneratePage, {CodeGeneratePageDTO, CodeGeneratePageVO} from "@/api/CodeGeneratePage";
import {ProColumns, ProTable} from "@ant-design/pro-components";
import {useEffect, useState} from "react";

const columns: ProColumns<CodeGeneratePageVO>[] = [
    {
        title: '#',
        dataIndex: 'index',
        valueType: 'index',
        width: 48,
    },
    {
        title: '表名',
        dataIndex: 'tableName',
        copyable: true,
        order: 99
    },
    {
        title: '表描述',
        dataIndex: 'tableComment',
    },
    {
        title: '字段名',
        dataIndex: 'columnName',
    },
    {
        title: '字段类型',
        dataIndex: 'columnType',
    },
    {
        title: '字段描述',
        dataIndex: 'columnComment',
        copyable: true,
        order: 99
    },
];

export default function () {
    const [y, setY] = useState(0)

    useEffect(() => {
        setTimeout(() => {
            const y = (document.querySelector('.ant-pro-basicLayout-content')!.clientHeight - document.querySelector('.ant-pro-page-container-warp')!.clientHeight - 24 - 80 - 16 - 24 - 48 - 24 - 32 - 24 - 47
            )
            setY(y)
        }, 20)
    }, [])

    return <ProTable<CodeGeneratePageVO, CodeGeneratePageDTO>
        rowKey={"id"}
        scroll={{
            y
        }}
        pagination={{
            showQuickJumper: true,
        }}
        revalidateOnFocus={false}
        rowSelection={{}}
        columns={columns}
        options={{
            density: false,
        }}
        request={(params, sort, filter) => {
            return CodeGeneratePage({...params, sort})
        }}>

    </ProTable>
}


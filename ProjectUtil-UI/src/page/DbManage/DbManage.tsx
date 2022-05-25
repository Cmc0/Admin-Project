import {Table} from "antd";
import {useEffect, useRef, useState} from "react";
import CodeGeneratePage, {CodeGeneratePageVO} from "@/api/CodeGeneratePage";

const columns = [
    {
        title: '#',
        dataIndex: 'index',
        render: (text: any, record: CodeGeneratePageVO, index: any) => {
            return index + 1;
        }
    },
    {
        title: '表名',
        dataIndex: 'tableName',
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
        dataIndex: 'dataType',
    },
    {
        title: '字段类型',
        dataIndex: 'columnType',
    },
    {
        title: '字段描述',
        dataIndex: 'columnComment',
    },
];

export default function () {
    const [dataSource, setDataSource] = useState<CodeGeneratePageVO[]>();
    const [total, setTotal] = useState<number>();
    const [loading, setLoading] = useState<boolean>();
    const [tableHeight, setTableHeight] = useState<number>();
    const MainLayout = useRef<HTMLDivElement>(null)
    useEffect(() => {
        setLoading(true)
        CodeGeneratePage({pageSize: -1}).then(res => {
            setTotal(res.total)
            setDataSource(res.records)
            setLoading(false)
        })
        setTimeout(() => {
            setTableHeight(MainLayout.current!.clientHeight - 32 - 32 - 35 - 40)
        }, 20)
    }, [])

    return <div className={"p-20 h100"} id={"MainLayout"} ref={MainLayout}>
        <Table loading={loading} dataSource={dataSource} columns={columns}
               rowKey={record => record.tableName + record.columnName}
               scroll={{
                   y: tableHeight + 'px'
               }}
               pagination={{
                   total,
                   showQuickJumper: true,
                   showTotal: (total, range) => `显示第 ${range[0]} 条-第 ${range[1]} 条，共 ${total} 条`
               }}/>
    </div>
}


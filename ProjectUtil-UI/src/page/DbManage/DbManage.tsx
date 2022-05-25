import CodeGeneratePage, {CodeGeneratePageDTO, CodeGeneratePageVO} from "@/api/CodeGeneratePage";
import {ProColumns, ProTable} from "@ant-design/pro-components";

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
        sorter: true
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
    },
];

export default function () {
    return <ProTable<CodeGeneratePageVO, CodeGeneratePageDTO>
        rowKey={"id"}
        scroll={{
            y: 500 + 'px'
        }}
        pagination={{
            pageSize: 10,
            showQuickJumper: true,
        }}
        revalidateOnFocus={false}
        rowSelection={{}}
        columns={columns}
        request={(params, sort, filter) => {
            return CodeGeneratePage({...params, sort})
        }}>

    </ProTable>
}


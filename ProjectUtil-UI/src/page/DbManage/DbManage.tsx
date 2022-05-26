import {CodeGeneratePageDTO, CodeGeneratePageVO, forSpring, page} from "@/api/CodeGenerate";
import {ProColumns, ProTable} from "@ant-design/pro-components";
import {Button} from "antd";
import {ToastSuccess} from "@/util/ToastUtil";

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
    return <ProTable<CodeGeneratePageVO, CodeGeneratePageDTO>
        rowKey={"id"}
        scroll={{
            y: 450
        }}
        pagination={{
            showQuickJumper: true,
        }}
        revalidateOnFocus={false}
        rowSelection={{}}
        columns={columns}
        options={{
            density: false,
            fullScreen: true,
        }}
        request={(params, sort, filter) => {
            return page({...params, sort})
        }}
        tableAlertOptionRender={({selectedRowKeys, selectedRows, onCleanSelected}) => (
            <>
                <Button type="link">生成前端代码</Button>
                <Button type="link" onClick={() => {
                    codeGenerateForSpringClick(selectedRows)
                }}>生成后台代码</Button>
                <Button type="link" onClick={onCleanSelected}>取消选择</Button>
            </>
        )}
    >

    </ProTable>
}

function codeGenerateForSpringClick(selectedRows: CodeGeneratePageVO[]) {
    forSpring(selectedRows).then(res => {
        ToastSuccess(res.msg)
    })
}


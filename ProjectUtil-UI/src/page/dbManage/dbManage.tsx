import {Table} from "antd";
import {useEffect} from "react";
import CodeGeneratePage from "@/api/CodeGeneratePage";

const dataSource = [
    {
        key: '1',
        name: '胡彦斌',
        age: 32,
        address: '西湖区湖底公园1号',
    },
    {
        key: '2',
        name: '胡彦祖',
        age: 42,
        address: '西湖区湖底公园1号',
    },
];
const columns = [
    {
        title: '姓名',
        dataIndex: 'name',
        key: 'name',
    },
    {
        title: '年龄',
        dataIndex: 'age',
        key: 'age',
    },
    {
        title: '住址',
        dataIndex: 'address',
        key: 'address',
    },
];

export default function () {
    useEffect(() => {
        CodeGeneratePage({}).then(res => {
            console.log(res.records)
        })
    }, [])

    return <div className={"p-20"}>
        <Table dataSource={dataSource} columns={columns}/>
    </div>
}

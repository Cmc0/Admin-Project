import {Button, Input, Space} from "antd";
import {Dispatch, SetStateAction, useState} from "react";
import {forAnt} from "@/api/CodeGenerateController";
import {ToastSuccess} from "@/util/ToastUtil";
import {sqlToJava} from "@/api/SqlToJavaController";

export default function () {

    const [t1, setT1] = useState<string>('');
    const [t2, setT2] = useState<string>('');

    return <div className={"p-24 w100 flex-center"}>

        <div className={"flex1"}>
            <Input.TextArea allowClear rows={30} value={t1} onChange={(e) => {
                setT1(e.target.value)
            }}/>
        </div>

        <div className={"flex-c flex m-l-r-20"}>

            <Button className={"m-b-20"} onClick={() => {
                setT1('')
                setT2('')
            }}>清空</Button>

            <Button onClick={() => {
                convertClick(t1, setT2)
            }} type={"primary"}>转换</Button>

        </div>

        <div className={"flex1"}>
            <Input.TextArea allowClear rows={30} value={t2} onChange={(e) => {
                setT2(e.target.value)
            }}/>
        </div>

    </div>

}

function convertClick(t1: string, setT2: Dispatch<SetStateAction<string>>) {

    sqlToJava({value: t1}).then(res => {
        setT2(res.data)
    })

}

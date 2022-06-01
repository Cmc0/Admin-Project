import {Button, Input} from "antd";
import {Dispatch, SetStateAction, useState} from "react";
import {javaToTs, sqlToJava} from "@/api/JavaConvertController";

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

            <Button onClick={() => {
                setT1('')
                setT2('')
            }}>clear</Button>

            <Button className={"m-t-20"} onClick={() => {
                sqlToJavaClick(t1, setT2)
            }} type={"primary"}>sqlToJava</Button>

            <Button className={"m-t-20"} onClick={() => {
                javaToTsClick(t1, setT2)
            }} type={"primary"}>javaToTs</Button>

        </div>

        <div className={"flex1"}>
            <Input.TextArea allowClear rows={30} value={t2} onChange={(e) => {
                setT2(e.target.value)
            }}/>
        </div>

    </div>

}

function sqlToJavaClick(t1: string, setT2: Dispatch<SetStateAction<string>>) {

    sqlToJava({value: t1}).then(res => {
        setT2(res.data)
    })

}

function javaToTsClick(t1: string, setT2: Dispatch<SetStateAction<string>>) {

    javaToTs({value: t1}).then(res => {
        setT2(res.data)
    })

}

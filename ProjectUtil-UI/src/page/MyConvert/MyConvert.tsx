import {Button, Input} from "antd";
import {Dispatch, SetStateAction, useState} from "react";
import {forAntByTableSql, forSpringByTableSql, javaToTs, sqlAddAs, sqlToJava} from "@/api/JavaConvertController";
import {ToastSuccess} from "@/util/ToastUtil";
import LocalStorageKey from "@/model/constant/LocalStorageKey";

export default function () {

    const [t1, setT1] = useState<string>(localStorage.getItem(LocalStorageKey.MyConvertT1Cache) || '');
    const [t2, setT2] = useState<string>('');

    return <div className={"p-24 w100 flex-center"}>

        <div className={"flex1"}>
            <Input.TextArea allowClear rows={32} value={t1} onChange={(e) => {
                setT1(e.target.value)
                localStorage.setItem(LocalStorageKey.MyConvertT1Cache, e.target.value)
            }}/>
        </div>

        <div className={"flex-c flex m-l-r-20"}>

            <Button onClick={() => {
                setT1('')
                setT2('')
            }}>清空</Button>

            <Button className={"m-t-20"} onClick={() => {
                sqlToJavaClick(t1, setT2)
            }} type={"primary"}>sqlToJava</Button>

            <Button className={"m-t-20"} onClick={() => {
                javaToTsClick(t1, setT2)
            }} type={"primary"}>javaToTs</Button>

            <Button className={"m-t-20"} onClick={() => {
                sqlAddAsClick(t1, setT2)
            }} type={"primary"}>sqlAddAs</Button>

            <Button className={"m-t-20"} onClick={() => {
                forSpringByTableSqlClick(t1)
            }} type={"primary"}>forSpringByTableSql</Button>

            <Button className={"m-t-20"} onClick={() => {
                forAntByTableSqlClick(t1)
            }} type={"primary"}>forAntByTableSql</Button>


        </div>

        <div className={"flex1"}>
            <Input.TextArea allowClear rows={32} value={t2} onChange={(e) => {
                setT2(e.target.value)
            }}/>
        </div>

    </div>

}

function sqlToJavaClick(t1: string, setT2: Dispatch<SetStateAction<string>>) {

    sqlToJava({value: t1}).then(res => {
        setT2(res.data)
        ToastSuccess(res.msg)
    })

}

function javaToTsClick(t1: string, setT2: Dispatch<SetStateAction<string>>) {

    javaToTs({value: t1}).then(res => {
        setT2(res.data)
        ToastSuccess(res.msg)
    })

}

function sqlAddAsClick(t1: string, setT2: Dispatch<SetStateAction<string>>) {

    sqlAddAs({value: t1}).then(res => {
        setT2(res.data)
        ToastSuccess(res.msg)
    })

}

function forSpringByTableSqlClick(t1: string) {

    forSpringByTableSql({value: t1}).then(res => {
        ToastSuccess(res.msg)
    })

}

function forAntByTableSqlClick(t1: string) {

    forAntByTableSql({value: t1}).then(res => {
        ToastSuccess(res.msg)
    })

}

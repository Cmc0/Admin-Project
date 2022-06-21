import {FormInstance} from "antd";
import {execTest} from "../../../util/PageTestUtil";
import {UserRegisterByEmailDTO} from "@/api/UserRegisterController";

export default function (useForm: FormInstance<UserRegisterByEmailDTO>) {
    console.log('RegisterTest')

    execTest({
        name: '全部未输入',
        func: () => {
            useForm.setFieldsValue({
                code: '',
                email: '',
                origPassword: '',
                password: '',
            })

            useForm.submit()
        }
    })

    execTest({
        name: '只输入邮箱',
        func: () => {
            useForm.setFieldsValue({
                code: '',
                email: '123@qq.com',
                origPassword: '',
                password: '',
            })

            useForm.submit()
        }
    })

}

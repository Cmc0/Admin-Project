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
                password: '',
            })

            useForm.submit()
        }
    })

    execTest({
        name: '只输入密码',
        func: () => {
            useForm.setFieldsValue({
                code: '',
                email: '',
                password: 'adminPwd123',
            })

            useForm.submit()
        }
    })

    execTest({
        name: '只输入 code',
        func: () => {
            useForm.setFieldsValue({
                code: '123456',
                email: '',
                password: '',
            })

            useForm.submit()
        }
    })

    execTest({
        name: '不输入 code',
        func: () => {
            useForm.setFieldsValue({
                code: '',
                email: '123@qq.com',
                password: 'adminPwd123',
            })

            useForm.submit()
        }
    })

    execTest({
        name: '全部输入',
        func: () => {
            useForm.setFieldsValue({
                code: '123456',
                email: '123@qq.com',
                password: 'adminPwd123',
            })

            useForm.submit()
        }
    })

}

import {
    sysUserBaseInfo,
    SysUserBaseInfoVO,
    sysUserUpdateBaseInfo,
    SysUserUpdateBaseInfoDTO
} from "@/api/SysUserController";
import {ActionType, ProDescriptions} from "@ant-design/pro-components";
import {DeleteOutlined, EyeOutlined} from "@ant-design/icons/lib";
import CommonConstant from "@/model/constant/CommonConstant";
import {default as React, useRef, useState} from "react";
import {UploadFile} from "antd/es/upload/interface";
import {CheckAvatarFileType, CheckFileSize, GetPublicDownFileUrl, SysFileUploadPro} from "../../../../util/FileUtil";
import {RequestData} from "@ant-design/pro-descriptions/lib/useFetchData";
import {useAppDispatch} from "@/store";
import {Avatar, Form, Image, Space, Upload} from "antd";
import {sysFileUpload} from "@/api/SysFileController";
import {setUserBaseInfo} from "@/store/userSlice";
import {execConfirm, ToastError, ToastSuccess} from "../../../../util/ToastUtil";
import MyIcon from "@/componse/MyIcon/MyIcon";
import ValidatorUtil from "../../../../util/ValidatorUtil";

export default function () {

    const appDispatch = useAppDispatch();

    const [fileList, setFileList] = useState<UploadFile[]>([]);
    const [fileLoading, setFileLoading] = useState<boolean>(false)

    const actionRef = useRef<ActionType>()

    const [useForm] = Form.useForm<SysUserUpdateBaseInfoDTO>();

    const currentForm = useRef<SysUserUpdateBaseInfoDTO>({})

    function doSysUserUpdateBaseInfo(form: SysUserUpdateBaseInfoDTO) {
        return sysUserUpdateBaseInfo(form).then(res => {
            currentForm.current = form
            ToastSuccess(res.msg)
            appDispatch(setUserBaseInfo(form))
            setFileLoading(false)
        })
    }

    return (
        <ProDescriptions<SysUserBaseInfoVO>
            formProps={{
                form: useForm
            }}
            title="个人资料"
            actionRef={actionRef}
            request={() => {
                return new Promise<RequestData>((resolve) => {
                    sysUserBaseInfo().then(res => {
                        currentForm.current = res.data
                        resolve({
                            success: true,
                            data: res.data
                        })
                    })
                })
            }}
            editable={{
                onSave: async (key, record) => {
                    await doSysUserUpdateBaseInfo({...record, avatarUrl: currentForm.current.avatarUrl})
                    return true;
                },
            }}
            column={1}
            columns={[
                {
                    title: '头像',
                    dataIndex: 'avatarUrl',
                    editable: false,
                    render: (dom: React.ReactNode, entity) => {
                        return <Space size={16}>
                            <Avatar src={<Image
                                src={currentForm.current.avatarUrl ? GetPublicDownFileUrl(currentForm.current.avatarUrl) : CommonConstant.RANDOM_AVATAR_URL}
                                height={32} preview={{mask: <EyeOutlined title={"预览"}/>}}/>}/>
                            <Upload
                                disabled={fileLoading}
                                accept={CommonConstant.IMAGE_FILE_ACCEPT_TYPE}
                                fileList={fileList}
                                maxCount={1}
                                showUploadList={false}
                                beforeUpload={(file) => {
                                    if (!CheckAvatarFileType(file.type)) {
                                        ToastError("暂不支持此文件类型：" + file.type + "，请重新选择")
                                        return false
                                    }
                                    if (!CheckFileSize(file.size!, 2097152)) {
                                        ToastError("图片大于 2MB，请重新选择")
                                        return false
                                    }
                                    return true
                                }}
                                customRequest={(options) => {
                                    setFileLoading(true)
                                    SysFileUploadPro(options.file, '1').then(url => {
                                        doSysUserUpdateBaseInfo({...currentForm.current, avatarUrl: url})
                                    }).catch(() => {
                                        setFileLoading(false)
                                    })
                                }}
                                onChange={(info) => {
                                    setFileList(info.fileList)
                                }}
                            >
                                <a> <MyIcon title={fileLoading ? '上传中' : '上传头像'}
                                            icon={fileLoading ? 'LoadingOutlined' : 'UploadOutlined'}/></a>
                            </Upload>
                            {entity.avatarUrl && <a onClick={() => {
                                if (fileLoading) {
                                    return
                                }
                                execConfirm(() => {
                                    return doSysUserUpdateBaseInfo({...currentForm.current, avatarUrl: ''})
                                }, undefined, '确定移除头像吗？')
                            }}><DeleteOutlined title={"移除头像"} className={"red3"}/></a>}
                        </Space>
                    }
                },
                {
                    title: '昵称',
                    dataIndex: 'nickname',
                    formItemProps: {
                        required: true,
                        rules: [
                            {
                                validator: ValidatorUtil.nicknameValidate
                            }
                        ],
                    },
                },
                {
                    title: '个人简介',
                    dataIndex: 'bio',
                    valueType: 'textarea',
                    fieldProps: {
                        showCount: true,
                        maxLength: 100,
                        allowClear: true,
                    },
                    render: (dom) => {
                        return dom
                    }
                },
            ]}
        />
    )
}

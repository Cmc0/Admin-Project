import {ActionType, ProDescriptions} from "@ant-design/pro-components";
import {DeleteOutlined, EyeOutlined} from "@ant-design/icons/lib";
import CommonConstant from "@/model/constant/CommonConstant";
import {default as React, useRef, useState} from "react";
import {UploadFile} from "antd/es/upload/interface";
import {CheckAvatarFileType, CheckFileSize, GetPublicDownFileUrl, SysFileUploadPro} from "../../../../util/FileUtil";
import {RequestData} from "@ant-design/pro-descriptions/lib/useFetchData";
import {useAppDispatch} from "@/store";
import {Avatar, Form, Image, Space, Upload} from "antd";
import {setUserSelfBaseInfo} from "@/store/userSlice";
import {execConfirm, ToastError, ToastSuccess} from "../../../../util/ToastUtil";
import MyIcon from "@/componse/MyIcon/MyIcon";
import {ValidatorUtil} from "../../../../util/ValidatorUtil";
import {USER_CENTER_KEY_ONE} from "@/page/user/Self/Self";
import {
    userSelfBaseInfo,
    UserSelfBaseInfoVO,
    userSelfUpdateBaseInfo,
    UserSelfUpdateBaseInfoDTO
} from "@/api/UserSelfController";

export default function () {

    const appDispatch = useAppDispatch();

    const [fileList, setFileList] = useState<UploadFile[]>([]);
    const [fileLoading, setFileLoading] = useState<boolean>(false)

    const actionRef = useRef<ActionType>()

    const [useForm] = Form.useForm<UserSelfUpdateBaseInfoDTO>();

    const currentForm = useRef<UserSelfUpdateBaseInfoDTO>({})

    function doSysUserSelfUpdateBaseInfo(form: UserSelfUpdateBaseInfoDTO) {
        return userSelfUpdateBaseInfo(form).then(res => {
            currentForm.current = form
            ToastSuccess(res.msg)
            appDispatch(setUserSelfBaseInfo(form))
            setFileLoading(false)
        })
    }

    return (
        <ProDescriptions<UserSelfBaseInfoVO>
            formProps={{
                form: useForm
            }}
            title={USER_CENTER_KEY_ONE}
            actionRef={actionRef}
            request={() => {
                return new Promise<RequestData>((resolve) => {
                    userSelfBaseInfo().then(res => {
                        currentForm.current = res.data
                        resolve({
                            success: true,
                            data: res.data
                        })
                        appDispatch(setUserSelfBaseInfo(res.data))
                    })
                })
            }}
            editable={{
                onSave: async (key, record) => {
                    await doSysUserSelfUpdateBaseInfo({...record, avatarUrl: currentForm.current.avatarUrl})
                    return true;
                },
            }}
            column={1}
            columns={[
                {
                    title: '??????',
                    dataIndex: 'avatarUrl',
                    editable: false,
                    render: (dom: React.ReactNode, entity) => {
                        return <Space size={16}>
                            <Avatar src={<Image
                                src={currentForm.current.avatarUrl ? GetPublicDownFileUrl(currentForm.current.avatarUrl) : CommonConstant.RANDOM_AVATAR_URL}
                                height={32} preview={{mask: <EyeOutlined title={"??????"}/>}}/>}/>
                            <Upload
                                disabled={fileLoading}
                                accept={CommonConstant.IMAGE_FILE_ACCEPT_TYPE}
                                fileList={fileList}
                                maxCount={1}
                                showUploadList={false}
                                beforeUpload={(file) => {
                                    if (!CheckAvatarFileType(file.type)) {
                                        ToastError("??????????????????????????????" + file.type + "??????????????????")
                                        return false
                                    }
                                    if (!CheckFileSize(file.size!, 2097152)) {
                                        ToastError("???????????? 2MB??????????????????")
                                        return false
                                    }
                                    return true
                                }}
                                customRequest={(options) => {
                                    setFileLoading(true)
                                    SysFileUploadPro(options.file, 'AVATAR').then(url => {
                                        doSysUserSelfUpdateBaseInfo({...currentForm.current, avatarUrl: url})
                                    }).catch(() => {
                                        setFileLoading(false)
                                    })
                                }}
                                onChange={(info) => {
                                    setFileList(info.fileList)
                                }}
                            >
                                <a> <MyIcon title={fileLoading ? '?????????' : '????????????'}
                                            icon={fileLoading ? 'LoadingOutlined' : 'UploadOutlined'}/></a>
                            </Upload>
                            {entity.avatarUrl && <a onClick={() => {
                                if (fileLoading) {
                                    return
                                }
                                execConfirm(() => {
                                    return doSysUserSelfUpdateBaseInfo({...currentForm.current, avatarUrl: ''})
                                }, undefined, '????????????????????????')
                            }}><DeleteOutlined title={"????????????"} className={"red3"}/></a>}
                        </Space>
                    }
                },
                {
                    title: '??????',
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
                    title: '????????????',
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

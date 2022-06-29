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
import {CheckImageFileSize, CheckImageFileType, GetPublicDownFileUrl} from "../../../../util/FileUtil";
import {RequestData} from "@ant-design/pro-descriptions/lib/useFetchData";
import {useAppDispatch} from "@/store";
import {Image, Space, Upload} from "antd";
import {sysFileUpload} from "@/api/SysFileController";
import {setUserBaseInfo} from "@/store/userSlice";
import {execConfirm, ToastError, ToastSuccess} from "../../../../util/ToastUtil";
import MyIcon from "@/componse/MyIcon/MyIcon";

export default function () {

    const appDispatch = useAppDispatch();

    const [fileList, setFileList] = useState<UploadFile[]>([]);
    const [fileLoading, setFileLoading] = useState<boolean>(false)

    const actionRef = useRef<ActionType>()

    const currentForm = useRef<SysUserUpdateBaseInfoDTO>({})

    function doSysUserUpdateBaseInfo(form: SysUserUpdateBaseInfoDTO) {
        return sysUserUpdateBaseInfo(form).then(res => {
            ToastSuccess(res.msg)
            setFileLoading(false)
            appDispatch(setUserBaseInfo(form))
            actionRef.current?.reload()
        })
    }

    return (
        <ProDescriptions<SysUserBaseInfoVO>
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
                onSave: async (key, record, originRow) => {
                    console.log(key, record, originRow);
                    return true;
                },
            }}
            column={1}
            columns={[
                {
                    title: '头像',
                    dataIndex: 'avatarUrl',
                    valueType: 'image',
                    editable: false,
                    renderText: (text) => {
                        return text ? GetPublicDownFileUrl(text) : CommonConstant.RANDOM_AVATAR_URL
                    },
                    render: (dom: React.ReactNode, entity) => {
                        return <Space size={16}>
                            <Image src={dom as string} height={32} preview={{mask: <EyeOutlined title={"预览"}/>}}/>
                            <Upload
                                disabled={fileLoading}
                                accept={CommonConstant.IMAGE_FILE_ACCEPT_TYPE}
                                fileList={fileList}
                                maxCount={1}
                                showUploadList={false}
                                beforeUpload={(file) => {
                                    if (!CheckImageFileType(file.type)) {
                                        ToastError("暂不支持此文件类型：" + file.type + "，请重新选择")
                                        return false
                                    }
                                    if (!CheckImageFileSize(file.size!, 2097152)) {
                                        ToastError("图片大于 2MB，请重新选择")
                                        return false
                                    }
                                    return true
                                }}
                                customRequest={(options) => {
                                    setFileLoading(true)
                                    const formData = new FormData()
                                    formData.append('file', options.file)
                                    formData.append('bucketName', 'public')
                                    formData.append('folderName', 'avatar')
                                    sysFileUpload(formData, {headers: {'Content-Type': 'multipart/form-data'}}).then(res => {
                                        doSysUserUpdateBaseInfo({...currentForm.current, avatarUrl: res.data})
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
                                execConfirm(() => {
                                    return doSysUserUpdateBaseInfo({...currentForm.current, avatarUrl: ''})
                                }, undefined, '确定移除头像吗？')
                            }}><DeleteOutlined title={"移除头像"} className={"red3"}/></a>}
                        </Space>
                    }
                },
                {
                    title: '昵称',
                    dataIndex: 'nickname'
                },
            ]}
        />
    )
}

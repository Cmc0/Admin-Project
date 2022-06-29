import {
    sysUserBaseInfo,
    SysUserBaseInfoVO,
    sysUserUpdateBaseInfo,
    SysUserUpdateBaseInfoDTO
} from "@/api/SysUserController";
import {ActionType, ProDescriptions} from "@ant-design/pro-components";
import {EyeOutlined, UploadOutlined} from "@ant-design/icons/lib";
import CommonConstant from "@/model/constant/CommonConstant";
import {default as React, useRef, useState} from "react";
import {UploadFile} from "antd/es/upload/interface";
import {Button, Upload} from "antd";
import {sysFileUpload} from "@/api/SysFileController";
import {CheckImageFileSize, CheckImageFileType} from "../../../../util/FileUtil";
import {ToastError, ToastSuccess} from "../../../../util/ToastUtil";
import {RcFile} from "antd/lib/upload";
import {RequestData} from "@ant-design/pro-descriptions/lib/useFetchData";
import {useAppDispatch} from "@/store";
import {setUserBaseInfo} from "@/store/userSlice";

export default function () {

    const appDispatch = useAppDispatch();

    const [fileList, setFileList] = useState<UploadFile[]>([]);

    const actionRef = useRef<ActionType>()

    const currentForm = useRef<SysUserUpdateBaseInfoDTO>({})

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
                    if (key === 'avatarUrl') {
                        // @ts-ignore
                        const file: RcFile = record.avatarUrl.file;
                        let avatarUrl = ''
                        if (file) {
                            const formData = new FormData()
                            formData.append('file', file)
                            formData.append('bucketName', 'public')
                            formData.append('folderName', 'avatar')
                            await sysFileUpload(formData, {headers: {'Content-Type': 'multipart/form-data'}}).then(res => {
                                avatarUrl = res.data
                            })
                        }
                        let formTemp = {...currentForm.current, avatarUrl}
                        await sysUserUpdateBaseInfo(formTemp).then(res => {
                            ToastSuccess(res.msg)
                            appDispatch(setUserBaseInfo(formTemp))
                            actionRef.current?.reload()
                        })
                    }
                    return true;
                },
            }}
            column={1}
            columns={[
                {
                    title: '头像',
                    dataIndex: 'avatarUrl',
                    valueType: 'image',
                    fieldProps: {
                        preview: {
                            mask: <EyeOutlined title={"预览"}/>
                        },
                    },
                    renderText: (text) => {
                        return text ? text : CommonConstant.RANDOM_AVATAR_URL
                    },
                    renderFormItem: () =>
                        <Upload
                            accept={CommonConstant.IMAGE_FILE_ACCEPT_TYPE}
                            fileList={fileList}
                            maxCount={1}
                            // customRequest={(options) => {
                            //     const formData = new FormData()
                            //     formData.append('file', options.file)
                            //     formData.append('bucketName', 'public')
                            //     formData.append('folderName', 'avatar')
                            //     sysFileUpload(formData, {headers: {'Content-Type': 'multipart/form-data'}}).then(res => {
                            //         ToastSuccess(res.msg)
                            //     })
                            // }}
                            listType={"picture"}
                            beforeUpload={() => {
                                return false // 手动上传
                            }}
                            onChange={(info) => {
                                console.log(info)
                                if (!CheckImageFileType(info.file.type!)) {
                                    ToastError("暂不支持此文件类型：" + info.file.type + "，请重新选择")
                                    return
                                }
                                if (!CheckImageFileSize(info.file.size!, 2097152)) {
                                    ToastError("图片大于 2MB，请重新选择")
                                    return
                                }
                                setFileList(info.fileList)
                            }}
                        >
                            <Button icon={<UploadOutlined/>}>选择文件</Button>
                        </Upload>
                },
                {
                    title: '昵称',
                    dataIndex: 'nickname'
                },
            ]}
        />
    )
}

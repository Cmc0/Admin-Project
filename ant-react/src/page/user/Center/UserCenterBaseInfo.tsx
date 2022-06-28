import {ProDescriptions} from "@ant-design/pro-components";
import {SysUserBaseInfoVO} from "@/api/SysUserController";
import LocalStorageKey from "@/model/constant/LocalStorageKey";
import CommonConstant from "@/model/constant/CommonConstant";
import {EyeOutlined} from "@ant-design/icons/lib";

export default function () {
    return (
        <ProDescriptions<SysUserBaseInfoVO>
            title="个人资料"
            request={() => {
                return Promise.resolve({
                    success: true,
                    data: JSON.parse(localStorage.getItem(LocalStorageKey.USER_BASE_INFO) || '{}'),
                });
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
                    fieldProps: {
                        preview: {
                            mask: <EyeOutlined title={"预览"}/>
                        }
                    },
                    renderText: (text) => {
                        return text ? text : CommonConstant.RANDOM_AVATAR_URL
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

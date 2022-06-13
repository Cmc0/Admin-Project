// 通过 openApi，覆盖 /src/api 下的文件
import $http from "./HttpUtil";
import CommonConstant from "@/model/constant/CommonConstant";

export default function () {
    $http.get(CommonConstant.OPEN_API_URL).then(({data}) => {
        console.log(data)
    })
}

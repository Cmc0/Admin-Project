import $http from "../../util/HttpUtil";

// 用户-管理 退出登录
export function userLogout() {
    return $http.myPost("/user/logout")
}

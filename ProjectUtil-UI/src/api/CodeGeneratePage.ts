import $http from "@/util/HttpUtil";
import MyPageDTO from "@/model/dto/MyPageDTO";

export interface CodeGeneratePageDTO extends MyPageDTO {

}

// 代码生成控制器：分页排序查询
export default function (form: CodeGeneratePageDTO) {
    return $http.myPagePost('/codeGenerate/page', form)
}

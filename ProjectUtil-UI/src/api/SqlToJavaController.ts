import $http from "@/util/HttpUtil";
import {CodeGeneratePageVO} from "@/api/CodeGenerateController";
import NotBlankStrDTO from "@/model/dto/NotBlankStrDTO";

// sql转java
export function sqlToJava(form: NotBlankStrDTO) {
    return $http.myPost<string, NotBlankStrDTO>('/sqlToJava', form)
}

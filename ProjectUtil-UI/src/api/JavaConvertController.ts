import $http from "@/util/HttpUtil";
import NotBlankStrDTO from "@/model/dto/NotBlankStrDTO";

// sql转java
export function sqlToJava(form: NotBlankStrDTO) {
    return $http.myPost<string, NotBlankStrDTO>('/convert/sqlToJava', form)
}

// java转ts
export function javaToTs(form: NotBlankStrDTO) {
    return $http.myPost<string, NotBlankStrDTO>('/convert/javaToTs', form)
}

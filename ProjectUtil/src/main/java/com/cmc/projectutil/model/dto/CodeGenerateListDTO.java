package com.cmc.projectutil.model.dto;

import com.cmc.projectutil.model.vo.CodeGeneratePageVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CodeGenerateListDTO extends CodeGeneratePageVO {

    @ApiModelProperty(value = "字段名（驼峰）", hidden = true)
    private String columnNameCamelCase;

    @ApiModelProperty(value = "字段类型，对应的 java类型", hidden = true)
    private String columnJavaType;

    @ApiModelProperty(value = "字段类型，对应的 ts类型", hidden = true)
    private String columnTsType;

}

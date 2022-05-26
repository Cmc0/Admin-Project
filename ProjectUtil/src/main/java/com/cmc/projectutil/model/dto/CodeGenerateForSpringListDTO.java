package com.cmc.projectutil.model.dto;

import com.cmc.projectutil.model.vo.CodeGeneratePageVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CodeGenerateForSpringListDTO extends CodeGeneratePageVO {

    @ApiModelProperty(value = "字段名（驼峰）", hidden = true)
    private String columnNameCamelCase;

}

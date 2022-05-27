package com.cmc.projectutil.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * {@link com.cmc.projectutil.model.vo.CodeGeneratePageVO}
 */
@Data
public class CodeGenerateForSpringDTO {

    @ApiModelProperty(value = "表名")
    private String tableName;

    @ApiModelProperty(value = "表描述")
    private String tableComment;

    @ApiModelProperty(value = "表名（驼峰）")
    private String tableNameCamelCase;

    @ApiModelProperty(value = "表名（驼峰并首字母大写）")
    private String tableNameCamelCaseUpperFirst;

    @ApiModelProperty(value = "字段 list")
    private List<CodeGenerateForSpringListDTO> columnList;

    @ApiModelProperty(value = "没有父类字段 list")
    private List<CodeGenerateForSpringListDTO> noSupperClassColumnList;

    @ApiModelProperty(value = "父类类名（simpleName）")
    private String supperClassName;

}

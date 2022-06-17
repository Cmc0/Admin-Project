package com.admin.common.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SyncEntityFromDbVO {

    @ApiModelProperty(value = "表名")
    private String tableName;

    @ApiModelProperty(value = "表描述")
    private String tableComment;

    @ApiModelProperty(value = "字段名")
    private String columnName;

    @ApiModelProperty(value = "字段类型，如：tinyint(1) varchar(300)")
    private String columnType;

    @ApiModelProperty(value = "字段描述")
    private String columnComment;

}

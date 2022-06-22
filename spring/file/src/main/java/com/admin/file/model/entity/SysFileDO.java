package com.admin.file.model.entity;

import com.admin.common.model.entity.BaseEntityThree;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_file")
@Data
@ApiModel(description = "文件上传记录主表")
public class SysFileDO extends BaseEntityThree {

    @ApiModelProperty(value = "文件 url（包含文件类型）")
    private String url;

    @ApiModelProperty(value = "文件原始名（包含文件类型）")
    private String fileName;

    @ApiModelProperty(value = "额外信息（json格式）")
    private String extraJson;

}

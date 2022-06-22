package com.admin.file.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Data
public class SysFileRemoveDTO {

    @NotEmpty
    @ApiModelProperty(value = "文件路径（包含文件名） set")
    Set<String> urlSet;

}

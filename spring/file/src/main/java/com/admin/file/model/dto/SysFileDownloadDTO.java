package com.admin.file.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysFileDownloadDTO {

    @NotBlank
    @ApiModelProperty(value = "文件路径（包含文件名），例如：/bucketName/userId/folderName/fileName.xxx")
    String url;

}

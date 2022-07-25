package com.admin.im.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * {@link com.admin.im.model.document.ImGroupDocument}
 */
@Data
public class ImInsertOrUpdateGroupDTO {

    @Size(min = 32, max = 32)
    @ApiModelProperty(value = "elasticsearch id，备注：uuid")
    private String id;

    @NotBlank
    @ApiModelProperty(value = "群组名称")
    private String name;

    @ApiModelProperty(value = "头像url")
    private String avatarUrl;

}

package com.admin.im.model.dto;

import com.admin.common.model.dto.MyPageDTO;
import com.admin.im.model.enums.ImToTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@EqualsAndHashCode(callSuper = true)
@Data
public class ImMessagePageDTO extends MyPageDTO {

    @Size(max = 32)
    @NotBlank
    @ApiModelProperty(value = "目标对象 id")
    private String toId;

    @NotNull
    @ApiModelProperty(value = "目标对象类型：1 好友 2 群组")
    private ImToTypeEnum toType;

}

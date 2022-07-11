package com.admin.bulletin.model.dto;

import com.admin.common.model.dto.BaseInsertOrUpdateDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * {@link com.admin.bulletin.model.entity.SysBulletinDO}
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysBulletinInsertOrUpdateDTO extends BaseInsertOrUpdateDTO {

    @NotNull
    @ApiModelProperty(value = "公告类型（字典值）")
    private Byte type;

    @NotBlank
    @ApiModelProperty(value = "公告内容（富文本）")
    private String content;

    @NotBlank
    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "描述/备注")
    private String remark;

    @Future // 限制必须是一个将来的日期
    @NotNull
    @ApiModelProperty(value = "发布时间")
    private Date publishTime;

}

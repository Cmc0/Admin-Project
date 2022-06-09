package com.admin.common.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_notify")
@Data
@ApiModel(description = "通知主表")
public class BaseNotifyDO extends BaseEntityThree {

    @ApiModelProperty(value = "通知类型（字典值）")
    private Byte type;

    @ApiModelProperty(value = "通知内容（富文本）")
    private String content;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "额外信息（json格式）")
    private String extraJson;

}

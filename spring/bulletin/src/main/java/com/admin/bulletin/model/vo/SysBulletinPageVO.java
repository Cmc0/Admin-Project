package com.admin.bulletin.model.vo;

import com.admin.bulletin.model.enums.SysBulletinStatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * {@link com.admin.bulletin.model.entity.SysBulletinDO}
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysBulletinPageVO extends SysBulletinUserSelfPageVO {

    @ApiModelProperty(value = "公告状态")
    private SysBulletinStatusEnum status;

    @ApiModelProperty(value = "xxlJobId")
    private Long xxlJobId;

    @ApiModelProperty(value = "描述/备注")
    private String remark;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

}

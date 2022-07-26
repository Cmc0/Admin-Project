package com.admin.im.model.document;

import com.admin.im.model.enums.ImGroupJoinRoleEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class ImGroupJoinDocument {

    @ApiModelProperty(value = "elasticsearch id，备注：gj_createId_gid")
    private String id;

    @ApiModelProperty(value = "加入的群组归属用户 id")
    private Long createId;

    @ApiModelProperty(value = "加入群组的时间")
    private Date createTime;

    @ApiModelProperty(value = "群组主表 id，uuid")
    private String gid;

    @ApiModelProperty(value = "备注名称")
    private String remark;

    @ApiModelProperty(value = "在群组里的角色：1 创建人 2 管理员 3 普通用户")
    private ImGroupJoinRoleEnum role;

    @ApiModelProperty(value = "是否退出群组")
    private Boolean outFlag;

    @ApiModelProperty(value = "退出群组的时间，默认为：入群时间")
    private Date outTime;

}

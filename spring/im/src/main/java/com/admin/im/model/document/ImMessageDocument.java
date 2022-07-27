package com.admin.im.model.document;

import com.admin.im.model.enums.ImContentTypeEnum;
import com.admin.im.model.enums.ImMessageCreateTypeEnum;
import com.admin.im.model.enums.ImToTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.Set;

@Data
public class ImMessageDocument {

    @ApiModelProperty(value = "elasticsearch id，备注：uuid")
    private String id;

    @ApiModelProperty(value = "创建人 id")
    private Long createId;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "内容类型：1 文字 2 图片 3 文件 4 表情 5 链接")
    private ImContentTypeEnum contentType;

    @ApiModelProperty(value = "内容，最大长度 2000")
    private String content;

    @ApiModelProperty(value = "目标对象 id")
    private String toId;

    @ApiModelProperty(value = "目标对象类型：1 好友 2 群组")
    private ImToTypeEnum toType;

    @ApiModelProperty(value = "消息创建来源：1 用户 2 通过验证 3 创建完成")
    private ImMessageCreateTypeEnum createType;

    @ApiModelProperty(value = "已读的用户 idSet ，备注：不包含 createId")
    private Set<Long> ridSet;

    @ApiModelProperty(value = "对这些用户不可见，也不能检索")
    private Set<Long> hidSet;

}

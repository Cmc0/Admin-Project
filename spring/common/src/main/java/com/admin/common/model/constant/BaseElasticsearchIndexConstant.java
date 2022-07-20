package com.admin.common.model.constant;

/**
 * ElasticsearchIndex的常量类
 */
public interface BaseElasticsearchIndexConstant {

    String IM_BASE_INDEX_ = "IM_BASE_INDEX_"; // 即时通讯功能的 基础index，备注：需要在该字符串后面加 userId

    String IM_MSG_INDEX_ = "IM_MSG_INDEX_"; // 即时通讯功能的 消息index，备注：需要在该字符串后面加 userId

}

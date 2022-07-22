package com.admin.common.model.constant;

/**
 * ElasticsearchIndex的常量类
 */
public interface BaseElasticsearchIndexConstant {

    String IM_BASE_INDEX = "im_base_index"; // 即时通讯功能的 基础index

    String IM_MSG_INDEX_ = "im_msg_index_"; // 即时通讯功能的 消息index，备注：需要在该字符串后面加 userId

    String IM_FRIEND_REQUEST_INDEX = "im_friend_request_index"; // 即时通讯功能的 好友请求index

}

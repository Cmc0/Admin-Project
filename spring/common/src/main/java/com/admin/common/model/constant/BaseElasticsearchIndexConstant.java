package com.admin.common.model.constant;

/**
 * ElasticsearchIndex的常量类
 */
public interface BaseElasticsearchIndexConstant {

    String IM_MESSAGE_INDEX = "im_message_index"; // 即时通讯功能的 全部消息index

    String IM_FRIEND_INDEX = "im_friend_index"; // 即时通讯功能的 好友index

    String IM_FRIEND_REQUEST_INDEX = "im_friend_request_index"; // 即时通讯功能的 好友申请index

    String IM_GROUP_INDEX = "im_group_index"; // 即时通讯功能的 群组index

    String IM_GROUP_JOIN_INDEX = "im_group_join_index"; // 即时通讯功能的 加入的群组index

    String IM_GROUP_REQUEST_INDEX = "im_group_request_index"; // 即时通讯功能的 群组申请index

    String IM_SESSION_INDEX = "im_session_index"; // 即时通讯功能的 会话index

}

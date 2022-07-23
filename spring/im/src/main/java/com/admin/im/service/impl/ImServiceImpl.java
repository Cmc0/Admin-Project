package com.admin.im.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.admin.common.mapper.SysUserMapper;
import com.admin.im.model.dto.ImFriendRequestDTO;
import com.admin.im.service.ImService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ImServiceImpl implements ImService {

    @Resource
    ElasticsearchClient elasticsearchClient;
    @Resource
    SysUserMapper sysUserMapper;

    /**
     * 发送好友申请
     */
    @Override
    public String friendRequest(ImFriendRequestDTO dto) {

        return null;
    }

}

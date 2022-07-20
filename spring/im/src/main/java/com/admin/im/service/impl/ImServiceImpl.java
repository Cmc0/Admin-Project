package com.admin.im.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.GetIndexResponse;
import com.admin.im.model.dto.ImPageDTO;
import com.admin.im.model.dto.ImSendDTO;
import com.admin.im.model.vo.ImPageVO;
import com.admin.im.service.ImService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ImServiceImpl implements ImService {

    @Resource
    ElasticsearchClient elasticsearchClient;

    /**
     * 发送消息
     */
    @SneakyThrows
    @Override
    public String send(ImSendDTO dto) {

        GetIndexResponse getIndexResponse = elasticsearchClient.indices().get(i -> i.index("IM_BASE_INDEX"));

        return null;
    }

    /**
     * 分页排序查询
     */
    @Override
    public Page<ImPageVO> myPage(ImPageDTO dto) {
        return null;
    }

}

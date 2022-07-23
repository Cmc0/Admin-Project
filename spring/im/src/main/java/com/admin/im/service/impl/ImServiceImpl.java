package com.admin.im.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch.core.GetRequest;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.util.ObjectBuilder;
import com.admin.common.mapper.SysUserMapper;
import com.admin.im.model.dto.ImFriendRequestDTO;
import com.admin.im.service.ImService;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.util.function.Function;

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

    /**
     * 如果执行失败，则创建 index之后，再执行一次
     */
    @SneakyThrows
    @Nonnull
    private <TDocument> GetResponse<TDocument> autoCreateIndexAndGet(String index,
        Function<GetRequest.Builder, ObjectBuilder<GetRequest>> fn, Class<TDocument> tDocumentClass) {

        try {
            return elasticsearchClient.get(fn, tDocumentClass);
        } catch (ElasticsearchException e) {
            if ("index_not_found_exception".equals(e.error().type())) {
                elasticsearchClient.indices().create(c -> c.index(index));
                return elasticsearchClient.get(fn, tDocumentClass);
            }
            throw e;
        }
    }

    /**
     * 如果执行失败，则创建 index之后，再执行一次
     */
    @SneakyThrows
    @Nullable
    private <TDocument> SearchResponse<TDocument> autoCreateIndexAndSearch(String index,
        Function<SearchRequest.Builder, ObjectBuilder<SearchRequest>> fn, Class<TDocument> tDocumentClass) {

        try {
            return elasticsearchClient.search(fn, tDocumentClass);
        } catch (ElasticsearchException e1) {
            if ("index_not_found_exception".equals(e1.error().type())) {
                elasticsearchClient.indices().create(c -> c.index(index));
                try {
                    return elasticsearchClient.search(fn, tDocumentClass);
                } catch (ElasticsearchException e2) {
                    if ("search_phase_execution_exception".equals(e2.error().type())) {
                        return null;
                    }
                }

            }
            e1.printStackTrace();
            return null;
        }
    }

}

package com.admin.common.util;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.util.ObjectBuilder;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.util.function.Function;

@Component
public class ElasticsearchUtil {

    private static final String INDEX_NOT_FOUND_EXCEPTION = "index_not_found_exception"; // index未找到异常
    // 搜索阶段执行异常，主要是存在 index，但是不存在 document时，会报出这个错误
    private static final String SEARCH_PHASE_EXECUTION_EXCEPTION = "search_phase_execution_exception";
    // mget，index 不存在时，抛出的异常
    private static final String NO_DOCUMENTS_TO_GET_EXCEPTION = "Validation Failed: 1: no documents to get;";

    private static ElasticsearchClient elasticsearchClient;

    @Resource
    private void setElasticsearchClient(ElasticsearchClient value) {
        elasticsearchClient = value;
    }

    /**
     * 如果执行失败，则创建 index之后，再执行一次 mget
     */
    @SneakyThrows
    @Nullable
    public static <TDocument> MgetResponse<TDocument> autoCreateIndexAndMget(String index,
        Function<MgetRequest.Builder, ObjectBuilder<MgetRequest>> fn, Class<TDocument> tDocumentClass) {

        try {
            return elasticsearchClient.mget(fn, tDocumentClass);
        } catch (ElasticsearchException e) {
            if (NO_DOCUMENTS_TO_GET_EXCEPTION.equals(e.error().reason())) {
                return null;
            }
            throw e;
        }
    }

    /**
     * 如果执行失败，则创建 index之后，再执行一次 get
     */
    @SneakyThrows
    @Nonnull
    public static <TDocument> GetResponse<TDocument> autoCreateIndexAndGet(String index,
        Function<GetRequest.Builder, ObjectBuilder<GetRequest>> fn, Class<TDocument> tDocumentClass) {

        try {
            return elasticsearchClient.get(fn, tDocumentClass);
        } catch (ElasticsearchException e) {
            if (INDEX_NOT_FOUND_EXCEPTION.equals(e.error().type())) {
                elasticsearchClient.indices().create(c -> c.index(index));
                return elasticsearchClient.get(fn, tDocumentClass);
            }
            throw e;
        }
    }

    /**
     * 如果执行失败，则创建 index之后，再执行一次 search
     */
    @SneakyThrows
    @Nullable
    public static <TDocument> SearchResponse<TDocument> autoCreateIndexAndSearch(String index,
        Function<SearchRequest.Builder, ObjectBuilder<SearchRequest>> fn, Class<TDocument> tDocumentClass) {

        try {
            return elasticsearchClient.search(fn, tDocumentClass);
        } catch (ElasticsearchException e1) {
            if (INDEX_NOT_FOUND_EXCEPTION.equals(e1.error().type())) {
                elasticsearchClient.indices().create(c -> c.index(index));
                try {
                    return elasticsearchClient.search(fn, tDocumentClass);
                } catch (ElasticsearchException e2) {
                    if (SEARCH_PHASE_EXECUTION_EXCEPTION.equals(e2.error().type())) {
                        return null;
                    }
                }

            }
            e1.printStackTrace();
            return null;
        }
    }

    /**
     * 获取：search的 total
     */
    public static long searchTotal(SearchResponse<?> searchResponse) {
        if (searchResponse != null && searchResponse.hits().total() != null) {
            return searchResponse.hits().total().value();
        } else {
            return 0L;
        }
    }

    /**
     * 如果执行失败，则创建 index之后，再执行一次 search，并返回 search的 total
     */
    public static <TDocument> long autoCreateIndexAndGetSearchTotal(String index,
        Function<SearchRequest.Builder, ObjectBuilder<SearchRequest>> fn) {
        return searchTotal(autoCreateIndexAndSearch(index, fn, Object.class));
    }

    /**
     * 获取：search 第一个元素的 source
     */
    @Nullable
    public static <T> Hit<T> searchGetSourceHit(SearchResponse<T> searchResponse) {
        if (searchResponse != null && searchResponse.hits().total() != null
            && searchResponse.hits().total().value() != 0) {
            return searchResponse.hits().hits().get(0);
        } else {
            return null;
        }
    }

}

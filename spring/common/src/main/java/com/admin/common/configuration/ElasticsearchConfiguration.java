package com.admin.common.configuration;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.List;

@Configuration
public class ElasticsearchConfiguration {

    @Resource
    ElasticsearchProperties elasticsearchProperties;

    @Bean
    public ElasticsearchClient elasticsearchClient() {

        List<String> uriList = elasticsearchProperties.getUris();

        HttpHost[] httpHostArr = new HttpHost[uriList.size()];

        int i = 0;
        for (String item : uriList) {
            httpHostArr[i] = HttpHost.create(item);
            i++;
        }

        RestClient restClient = RestClient.builder(httpHostArr).build();

        ElasticsearchTransport elasticsearchTransport = new RestClientTransport(restClient, new JacksonJsonpMapper());

        return new ElasticsearchClient(elasticsearchTransport);
    }

}

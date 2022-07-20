package com.admin.common.configuration;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import lombok.SneakyThrows;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.ssl.SSLContextBuilder;
import org.elasticsearch.client.RestClient;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.net.ssl.SSLContext;
import java.util.List;

@Configuration
public class ElasticsearchConfiguration {

    @Resource
    ElasticsearchProperties elasticsearchProperties;

    @SneakyThrows
    @Bean
    public ElasticsearchClient elasticsearchClient() {

        List<String> uriList = elasticsearchProperties.getUris();

        HttpHost[] httpHostArr = new HttpHost[uriList.size()];

        int i = 0;
        for (String item : uriList) {
            httpHostArr[i] = HttpHost.create(item);
            i++;
        }

        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
            new UsernamePasswordCredentials(elasticsearchProperties.getUsername(),
                elasticsearchProperties.getPassword()));

        // 信任所有 SSL证书
        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial((chain, authType) -> true).build();

        RestClient restClient = RestClient.builder(httpHostArr).setHttpClientConfigCallback(
            httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).setSSLContext(sslContext)).build();

        return new ElasticsearchClient(new RestClientTransport(restClient, new JacksonJsonpMapper()));
    }

}

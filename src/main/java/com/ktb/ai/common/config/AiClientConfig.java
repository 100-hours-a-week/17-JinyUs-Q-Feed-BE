package com.ktb.ai.common.config;

import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class AiClientConfig {

    private static final Duration DEFAULT_READ_TIMEOUT = Duration.ofSeconds(300);

    @Bean
    public RestClient aiRestClient() {
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory();
        requestFactory.setReadTimeout(DEFAULT_READ_TIMEOUT);

        return RestClient.builder()
                .requestFactory(requestFactory)
                .build();
    }
}

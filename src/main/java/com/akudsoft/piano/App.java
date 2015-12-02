package com.akudsoft.piano;

import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = {
        "com.akudsoft.piano.*"
})
public class App extends WebMvcConfigurerAdapter {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Bean
    public ExecutorService taskExecutor() {
        return Executors.newFixedThreadPool(10);
    }

    @Bean
    public RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory =
                new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create().build());
        final RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);

        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return response.getStatusCode() != HttpStatus.OK;
            }

            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                log.error("External error [{}]: {}", response.getStatusCode(), response.getStatusText());
                throw new IOException("Error response: " + response.getStatusCode());
            }
        });

        return restTemplate;
    }

    @Bean
    public ClientHttpRequestFactory getClientRequestFactory() {
        final HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setReadTimeout(120000);
        factory.setConnectTimeout(10000); // 10 sec
        return factory;
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(App.class, args);
    }
}

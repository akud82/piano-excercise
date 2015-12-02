package com.akudsoft.piano.gateway;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.net.UrlEscapers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class StackOverflowService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RestTemplate restTemplate;

    private static final Cache<String, String> CACHE;

    static {
        CACHE = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterAccess(60, TimeUnit.MINUTES)
                .build();
    }

    public String search(String text) {
        final String encoded = UrlEscapers.urlPathSegmentEscaper().escape(text);
        final String url = String.format("" +
                "http://api.stackexchange.com/2.2/search?" +
                "page=1&pagesize=10&order=desc&sort=activity" +
                "&intitle=%s&site=stackoverflow", encoded);

        try {
            return CACHE.get(encoded, () -> {
                HttpHeaders headers = new HttpHeaders();
                headers.set("X-ESA-API-Key", "ROBOT");
                headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
                headers.set(HttpHeaders.ACCEPT_ENCODING, "GZIP");

                HttpEntity<String> entity = new HttpEntity<>(headers);
                // gzip support
                restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
                ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
                log.debug("body --> {}", result);
                return result.getBody();
            });

        } catch (ExecutionException e) {
            log.error("Cache error", e);
            return "{\"error\": \"cache error\"}";
        }
    }
}

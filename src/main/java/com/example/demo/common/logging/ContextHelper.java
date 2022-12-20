package com.example.demo.common.logging;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ContextHelper {

    public static final List<String> HEADERS = Arrays.asList("correlationId", "appLabel");

    public static final String CONTEXT_MAP = "context-map";

    public static Context addRequestHeadersToContext(final ServerHttpRequest request, final Context context) {

        final Map<String, String> contextMap = request
                .getHeaders().toSingleValueMap().entrySet()
                .stream()
                .filter(x -> HEADERS.contains(x.getKey()))
                .collect(Collectors.toMap(v -> v.getKey(), Map.Entry::getValue));

        return context.put(CONTEXT_MAP, contextMap);
    }

    public static Mono<Void> addContextToHttpResponseHeaders(final ServerHttpResponse response) {
        return Mono.deferContextual(Mono::just)
                .doOnNext(contextView -> {
                    if(!contextView.hasKey(CONTEXT_MAP))
                        return;

                    final HttpHeaders headers = response.getHeaders();
                    contextView.<Map<String, String>>get(CONTEXT_MAP).forEach(
                            (k, v) -> headers.add(k, v)
                    );
                }).then();
    }
}

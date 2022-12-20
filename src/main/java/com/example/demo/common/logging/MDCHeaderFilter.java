package com.example.demo.common.logging;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class MDCHeaderFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        exchange
                .getResponse()
                .beforeCommit(() -> ContextHelper.addContextToHttpResponseHeaders(exchange.getResponse()));
        return chain
                .filter(exchange)
                .contextWrite(context -> ContextHelper.addRequestHeadersToContext(exchange.getRequest(), context));
    }
}

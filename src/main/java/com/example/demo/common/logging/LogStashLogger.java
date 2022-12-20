package com.example.demo.common.logging;

import org.slf4j.MDC;
import reactor.core.publisher.Signal;

import java.net.InetAddress;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class LogStashLogger {

    private static String hostname;
    private static String hostIp;

    public static final String CONTEXT_MAP = "context-map";

    static {
        try {
            InetAddress address = InetAddress.getLocalHost();
            hostname = address.getHostName();
            hostIp = address.getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T>Consumer<Signal<T>> logOnNext(Consumer<T> log) {
        return signal -> {

            if(signal.isOnNext()) {

                Optional<Map<String, String>> contextMap = signal.getContextView().getOrEmpty(CONTEXT_MAP);

                if(contextMap.isEmpty()) {
                    log.accept(signal.get());
                } else {
                    Map<String, String> contextMap1 = contextMap.get();
                    contextMap1.put("hostIp", hostIp);
                    contextMap1.put("hostName", hostname);
                    MDC.setContextMap(contextMap1);
                    try {
                        log.accept(signal.get());
                    } finally {
                        MDC.clear();
                    }
                }
            }
            return;
        };
    }

    public static <T>Consumer<Signal<T>> logOnError(Consumer<Throwable> log) {
        return signal -> {

            if(!signal.isOnError()) return;

                Optional<Map<String, String>> contextMap = signal.getContextView().getOrEmpty(CONTEXT_MAP);

                if(contextMap.isEmpty()) {
                    log.accept(signal.getThrowable().getCause());
                } else {
                    Map<String, String> contextMap1 = contextMap.get();
                    contextMap1.put("hostIp", hostIp);
                    contextMap1.put("hostName", hostname);
                    MDC.setContextMap(contextMap1);
                    try {
                        log.accept(signal.getThrowable());
                    } finally {
                        MDC.clear();
                    }
                }
        };
    }

}

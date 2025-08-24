package com.sattvabite.order.config;

import brave.Tracing;
import brave.handler.SpanHandler;
import brave.propagation.B3Propagation;
import brave.sampler.Sampler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Sender;
import zipkin2.reporter.okhttp3.OkHttpSender;
import zipkin2.Span;

import java.util.Collections;

@Configuration
public class TracingConfig {

    @Value("${spring.zipkin.base-url:http://localhost:9411}")
    private String zipkinBaseUrl;

    @Bean
    public Sampler sampler() {
        return Sampler.ALWAYS_SAMPLE;
    }

    @Bean
    public Sender sender() {
        return OkHttpSender.newBuilder()
                .endpoint(zipkinBaseUrl + "/api/v2/spans")
                .build();
    }

    @Bean
    public AsyncReporter<Span> spanReporter(Sender sender) {
        return AsyncReporter.create(sender);
    }

    @Bean
    public Tracing tracing(Sampler sampler, AsyncReporter<Span> spanReporter) {
        return Tracing.newBuilder()
                .localServiceName("order-service")
                .spanReporter(spanReporter)
                .sampler(sampler)
                .propagationFactory(B3Propagation.newFactoryBuilder()
                        .injectFormat(B3Propagation.Format.MULTI)
                        .build())
                .traceId128Bit(true)
                .supportsJoin(true)
                .build();
    }

    @Bean
    public brave.Tracer braveTracer(Tracing tracing) {
        return tracing.tracer();
    }

    @Bean
    public io.micrometer.tracing.Tracer tracer(brave.Tracer braveTracer) {
        return new io.micrometer.tracing.brave.bridge.BraveTracer(
            braveTracer,
            new io.micrometer.tracing.brave.bridge.BraveCurrentTraceContext(brave.propagation.CurrentTraceContext.Default.create())
        );
    }
}

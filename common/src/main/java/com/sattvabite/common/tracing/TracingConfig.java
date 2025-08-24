package com.sattvabite.common.tracing;

import brave.Tracing;
import brave.handler.SpanHandler;
import brave.propagation.CurrentTraceContext;
import brave.sampler.Sampler;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.brave.bridge.BraveBaggageManager;
import io.micrometer.tracing.brave.bridge.BraveCurrentTraceContext;
import io.micrometer.tracing.brave.bridge.BraveTracer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Sender;
import zipkin2.reporter.brave.ZipkinSpanHandler;
import zipkin2.reporter.urlconnection.URLConnectionSender;

@Configuration
public class TracingConfig {

    @Value("${spring.application.name:unknown}")
    private String applicationName;

    @Value("${zipkin.base-url:http://localhost:9411}")
    private String zipkinBaseUrl;

    @Value("${tracing.sampling.rate:1.0}")
    private float samplingRate;

    @Bean
    @ConditionalOnMissingBean
    public Tracer tracer(Tracing tracing) {
        return new BraveTracer(
            tracing.tracer(),
            new BraveCurrentTraceContext(CurrentTraceContext.Default.create()),
            new BraveBaggageManager()
        );
    }
    
    @Bean
    public Tracing tracing() {
        return Tracing.newBuilder()
            .localServiceName(applicationName)
            .currentTraceContext(CurrentTraceContext.Default.create())
            .traceId128Bit(true)
            .supportsJoin(true)
            .sampler(Sampler.create(samplingRate))
            .addSpanHandler(spanHandler())
            .build();
    }
    
    @Bean
    public SpanHandler spanHandler() {
        return ZipkinSpanHandler.newBuilder(spanReporter()).build();
    }

    @Bean
    public Sender sender() {
        return URLConnectionSender.create(zipkinBaseUrl + "/api/v2/spans");
    }

    @Bean
    public AsyncReporter<zipkin2.Span> spanReporter() {
        return AsyncReporter.create(sender());
    }
}

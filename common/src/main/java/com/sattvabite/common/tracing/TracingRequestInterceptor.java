package com.sattvabite.common.tracing;

import brave.Span;
import brave.Tracer;
import brave.propagation.TraceContext;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TracingRequestInterceptor implements ClientHttpRequestInterceptor {

    private static final String TRACE_ID = "X-B3-TraceId";
    private static final String SPAN_ID = "X-B3-SpanId";
    private static final String PARENT_SPAN_ID = "X-B3-ParentSpanId";
    private static final String SAMPLED = "X-B3-Sampled";
    
    private final Tracer tracer;

    @Autowired
    public TracingRequestInterceptor(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public ClientHttpResponse intercept(
            HttpRequest request, 
            byte[] body, 
            ClientHttpRequestExecution execution) throws IOException {
        
        Span currentSpan = tracer.currentSpan();
        if (currentSpan != null) {
            TraceContext context = currentSpan.context();
            
            // Add tracing headers to the request
            request.getHeaders().add(TRACE_ID, context.traceIdString());
            request.getHeaders().add(SPAN_ID, context.spanIdString());
            
            if (context.parentId() != null) {
                request.getHeaders().add(PARENT_SPAN_ID, context.parentIdString());
            }
            
            request.getHeaders().add(SAMPLED, context.sampled() ? "1" : "0");
            
            // Also add to MDC for logging
            MDC.put("traceId", context.traceIdString());
            MDC.put("spanId", context.spanIdString());
        }
        
        return execution.execute(request, body);
    }
}

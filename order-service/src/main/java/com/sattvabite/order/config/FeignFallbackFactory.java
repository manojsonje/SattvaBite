package com.sattvabite.order.config;

import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Factory for creating fallback implementations of Feign clients.
 * This is used to provide fallback behavior when Feign client calls fail.
 *
 * @param <T> The Feign client type
 */
public class FeignFallbackFactory<T> implements Function<Throwable, T> {

    private static final Logger logger = LoggerFactory.getLogger(FeignFallbackFactory.class);
    private final Class<T> type;
    private final Map<Class<? extends Throwable>, T> fallbacks = new HashMap<>();

    /**
     * Creates a new FeignFallbackFactory for the specified type.
     *
     * @param type the Feign client interface class
     */
    public FeignFallbackFactory(Class<T> type) {
        this.type = type;
    }

    /**
     * Create a fallback instance for the Feign client.
     *
     * @param cause the cause of the failure
     * @return a fallback implementation of the Feign client
     */
    @Override
    @SuppressWarnings("unchecked")
    public T apply(Throwable cause) {
        logger.error("Fallback for " + type.getSimpleName() + ", reason: " + cause.getMessage(), cause);
        
        // Check for registered fallbacks first
        for (Map.Entry<Class<? extends Throwable>, T> entry : fallbacks.entrySet()) {
            if (entry.getKey().isInstance(cause)) {
                return entry.getValue();
            }
        }
        
        // Create a proxy that returns default values for primitive types or null for objects
        return (T) Proxy.newProxyInstance(
            type.getClassLoader(),
            new Class<?>[] { type },
            new DefaultFallbackHandler(cause)
        );
    }

    /**
     * Register a custom fallback implementation for a specific exception type.
     *
     * @param <E> the exception type
     * @param exceptionType the exception class
     * @param fallback the fallback implementation
     */
    public <E extends Throwable> void registerFallback(Class<E> exceptionType, T fallback) {
        fallbacks.put(exceptionType, fallback);
    }

    /**
     * Default fallback handler that returns default values
     */
    @SuppressWarnings("unchecked")
    private class DefaultFallbackHandler implements InvocationHandler {
        private final Throwable cause;
        private final Map<Class<? extends Throwable>, T> fallbacks = new HashMap<>();

        public DefaultFallbackHandler(Throwable cause) {
            this.cause = cause;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // Check if there's a custom fallback for this exception type
            for (Map.Entry<Class<? extends Throwable>, T> entry : fallbacks.entrySet()) {
                if (entry.getKey().isInstance(cause)) {
                    return method.invoke(entry.getValue(), args);
                }
            }
            
            // Default behavior for unhandled exceptions
            logger.warn("No fallback for method: " + method.getName() + ", throwing original exception");
            
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else if (cause instanceof Error) {
                throw (Error) cause;
            } else {
                throw new RuntimeException(cause);
            }
        }
    }

    /**
     * Creates a new fallback factory for the given type
     */
    public static <T> FeignFallbackFactory<T> of(Class<T> type) {
        return new FeignFallbackFactory<>(type);
    }
}

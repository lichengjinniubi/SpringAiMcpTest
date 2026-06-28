package com.ai.mcpclient.config;

import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.mcp.customizer.McpClientCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

/**
 * DEBUG ONLY.
 * <p>
 * Enables "trust all certificates" + disables hostname verification for the underlying JDK HttpClient
 * used by Spring AI MCP HttpClient transports.
 * <p>
 * Guarded behind {@code app.mcp.insecure-ssl=true}. Do NOT enable in production.
 */
@Configuration
@ConditionalOnProperty(name = "app.mcp.insecure-ssl", havingValue = "true")
public class InsecureSslMcpClientConfig {

    private static final Logger log = LoggerFactory.getLogger(InsecureSslMcpClientConfig.class);

    private static final SSLContext INSECURE_SSL_CONTEXT = createInsecureSslContext();
    private static final SSLParameters INSECURE_SSL_PARAMETERS = createInsecureSslParameters();

    public InsecureSslMcpClientConfig() {
        log.warn("app.mcp.insecure-ssl=true: MCP Client will trust ALL HTTPS certificates (DEBUG ONLY).");
    }

    @Bean
    public McpClientCustomizer<HttpClientSseClientTransport.Builder> insecureSslForSseTransport() {
        return (clientName, builder) -> builder.customizeClient(httpClientBuilder -> {
            httpClientBuilder.sslContext(INSECURE_SSL_CONTEXT);
            httpClientBuilder.sslParameters(INSECURE_SSL_PARAMETERS);
        });
    }

    @Bean
    public McpClientCustomizer<HttpClientStreamableHttpTransport.Builder> insecureSslForStreamableTransport() {
        return (clientName, builder) -> builder.customizeClient(httpClientBuilder -> {
            httpClientBuilder.sslContext(INSECURE_SSL_CONTEXT);
            httpClientBuilder.sslParameters(INSECURE_SSL_PARAMETERS);
        });
    }

    private static SSLContext createInsecureSslContext() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            return sslContext;
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to initialize insecure SSLContext for MCP client", ex);
        }
    }

    private static SSLParameters createInsecureSslParameters() {
        SSLParameters sslParameters = new SSLParameters();
        // Disable HTTPS endpoint identification (hostname verification)
        sslParameters.setEndpointIdentificationAlgorithm(null);
        return sslParameters;
    }
}


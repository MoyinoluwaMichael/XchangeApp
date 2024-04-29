package org.example.xchange.configuration;

import io.netty.channel.ChannelOption;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.resources.LoopResources;
import reactor.netty.tcp.SslProvider;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

import javax.net.ssl.SSLException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS;

@Configuration
@RequiredArgsConstructor
public class WebClientConfiguration {
    private final XchangeProperty xchangeProperty;
    
    @Bean(name = "XchangeWebClient")
    public WebClient webClient() {
        HttpClient httpClient = getHttpClient();

        ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
        return WebClient
                .builder()
                .clientConnector(connector)
                .build();
    }


    //https://projectreactor.io/docs/netty/release/reference/index.html#http-server
    private HttpClient getHttpClient() {
        int connectTimeout = xchangeProperty.getConnectTimeoutMs() == 0 ? 3_000 : xchangeProperty.getConnectTimeoutMs();
        long readTimeout = xchangeProperty.getReadTimeoutMs() == 0 ? 20_000 : xchangeProperty.getReadTimeoutMs();
        long writeTimeout = xchangeProperty.getWriteTimeoutMs() == 0 ? 20_000 : xchangeProperty.getWriteTimeoutMs();
        LogLevel logLevel = xchangeProperty.getLogLevel() == null ? LogLevel.INFO : xchangeProperty.getLogLevel();

        ConnectionProvider connectionProvider = getConnectionProvider();

        HttpClient httpClient = HttpClient
                .create(connectionProvider)
                .keepAlive(true)
                .baseUrl(xchangeProperty.getLithuaniaBaseUrl())
                .secure()
                .protocol(HttpProtocol.H2)
                .compress(true)
                .doOnConnected(conn ->
                        conn
                                .addHandlerFirst(new ReadTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(writeTimeout, TimeUnit.MILLISECONDS))
                )
                .option(CONNECT_TIMEOUT_MILLIS, connectTimeout)
                .option(ChannelOption.SO_KEEPALIVE, true)
                //.option(ChannelOption.TCP_NODELAY, true) //Disable buffering
                .runOn(loopResources())
                .wiretap("reactor.netty.http.client.HttpClient", logLevel, AdvancedByteBufFormat.TEXTUAL, StandardCharsets.UTF_8)
                .followRedirect(true)
                .responseTimeout(Duration.ofMillis(readTimeout));

        //eager_initialization
        httpClient.warmup().block();

        return httpClient;
    }

    private static ConnectionProvider getConnectionProvider() {
        return ConnectionProvider.builder("xchange-webclient-conn-pool")// I optimally set the configuration for this instead of externalizing
                .maxConnections(100)
                .maxIdleTime(Duration.ofSeconds(60))
                .maxLifeTime(Duration.ofSeconds(700))
                .evictInBackground(Duration.ofSeconds(700))
                .build();
    }

    private LoopResources loopResources() {
        int noOfWorkers = (int) (Runtime.getRuntime().availableProcessors() * 1.5);
        return LoopResources.create("rr-web-client-event-loop", 1, noOfWorkers, true);
    }

    private static void setSSLContext(SslProvider.SslContextSpec sslContextSpec) {
        try {
            sslContextSpec.sslContext(
                    SslContextBuilder.forClient()
                            .trustManager(InsecureTrustManagerFactory.INSTANCE)
                            .build());
        } catch (SSLException e) {
            throw new RuntimeException(e);
        }
    }
}

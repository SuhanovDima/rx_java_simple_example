package suhanov.pattern.example;

import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.Single;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.asynchttpclient.Dsl;
import org.asynchttpclient.Response;
import suhanov.pattern.example.model.SimpleResponse;

@Slf4j
@Builder
@Data
public class SimpleClient {
    private String baseUrl = "https://jsonplaceholder.typicode.com/todos/1";
    DefaultAsyncHttpClientConfig config;
    @Builder.Default
    private final Integer maxConnections = 20;
    @Builder.Default
    private final Integer connectTimeout = 1000;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public static SimpleClientBuilder builder() {
        return new SimpleClientBuilder() {
            @Override
            public SimpleClient build() {
                SimpleClient client = super.build();
                buildConfig(client);
                return client;
            }

            private void buildConfig(SimpleClient client) {
                DefaultAsyncHttpClientConfig.Builder config = Dsl.config()
                        .setMaxConnections(client.maxConnections)
                        .setConnectTimeout(client.connectTimeout);
                client.config = config.build();
            }
        };
    }

    public Single<SimpleResponse> schedule() {
        return Single.create(subscriber -> {
            try (AsyncHttpClient client = Dsl.asyncHttpClient(config)) {
                CompletableFuture<Response> whenResponse = client
                        .prepareGet(baseUrl)
                        .execute()
                        .toCompletableFuture()
                        .exceptionally(t -> {
                            log.error(t.getMessage());
                            return null;
                        })
                        .thenApply(httpResponse -> {
                            if (httpResponse != null) {
                                String content = httpResponse.getResponseBody(Charset.forName("utf-8"));
                                SimpleResponse response = SimpleResponse.builder().build();
                                try {
                                    response = objectMapper.readValue(content, SimpleResponse.class);
                                } catch (Exception e) {
                                    log.error("Error in parse", e);
                                }
                                subscriber.onSuccess(response);
                            }
                            return httpResponse;
                        });
                whenResponse.get();
            } catch (Exception e) {
                log.error("Error in SimpleClient", e);

            }
        });
    }
}

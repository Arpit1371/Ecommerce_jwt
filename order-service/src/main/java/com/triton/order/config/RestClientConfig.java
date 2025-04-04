package com.triton.order.config;

import com.triton.order.client.InventoryClient;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class RestClientConfig {

    @Value("${inventory.url}")
    private String inventoryURL;

    private final ObservationRegistry observationRegistry;

    @Bean
    public InventoryClient inventoryClient() {

        RestClient restClient = RestClient.builder()
                .baseUrl(inventoryURL)
                .requestFactory(getClientRequestFactory())
                .observationRegistry(observationRegistry)
                .build();
        RestClientAdapter restClientAdapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory.builderFor(restClientAdapter).build();
        InventoryClient inventoryClient = httpServiceProxyFactory.createClient(InventoryClient.class);
        return inventoryClient;
    }
    private ClientHttpRequestFactory getClientRequestFactory() {
        ClientHttpRequestFactorySettings clientHttpRequestFactorySettings = ClientHttpRequestFactorySettings.defaults()
                .withConnectTimeout(Duration.ofSeconds(3))
                .withReadTimeout((Duration.ofSeconds(3)));
        return ClientHttpRequestFactoryBuilder.simple().build(clientHttpRequestFactorySettings);
    }
}

package com.kizilaslan.recoverAiBackend.config;

import com.niamedtech.expo.exposerversdk.ExpoPushNotificationClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationConfig {

    @Bean
    public ExpoPushNotificationClient expoPushNotificationClient() {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        return ExpoPushNotificationClient.builder().setHttpClient(httpClient).build();
    }
}

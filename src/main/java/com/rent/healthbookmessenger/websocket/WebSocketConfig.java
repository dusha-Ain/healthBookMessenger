package com.rent.healthbookmessenger.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Префиксы для отправки сообщений от сервера клиентам
        config.enableSimpleBroker("/topic", "/queue");

        // Префикс для сообщений от клиентов к серверу
        config.setApplicationDestinationPrefixes("/app");

        // Префикс для личных сообщений (/user/queue/...)
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Эндпоинт для подключения WebSocket
        registry.addEndpoint("/ws-chat")
                .setAllowedOriginPatterns("*")  // разрешаем все origins
                .withSockJS();  // поддержка SockJS
    }
}

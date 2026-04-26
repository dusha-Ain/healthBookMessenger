package com.rent.healthbookmessenger.profile.service;


import com.rent.healthbookmessenger.profile.dto.Profile;
import com.rent.healthbookmessenger.web.kafka.dto.UserValidationRequest;
import com.rent.healthbookmessenger.web.kafka.dto.UserValidationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class ProfileService {

    private static final Logger log = LoggerFactory.getLogger(ProfileService.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    // Хранилище ожидающих ответов
    private final Map<String, CompletableFuture<UserValidationResponse>> pendingRequests = new ConcurrentHashMap<>();

    @Autowired
    public ProfileService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Проверить, существует ли пользователь
     */
    public boolean userExists(UUID userId) {
        log.debug("Проверка существования пользователя: {}", userId);
        UserValidationResponse response = sendRequest(userId, "CHECK_EXISTS");
        boolean exists = response != null && response.isExists();
        log.debug("Пользователь {} существует: {}", userId, exists);
        return exists;
    }

    /**
     * Получить полный профиль пользователя по ID
     */
    public Profile getProfile(UUID userId) {
        log.debug("Получение профиля по ID: {}", userId);
        UserValidationResponse response = sendRequest(userId, "GET_PROFILE");
        if (response != null && response.isExists() && response.getProfile() != null) {
            // Конвертируем Profile из ответа в наш DTO
            Profile profile = new Profile();
            profile.setId(response.getProfile().getId());
            profile.setName(response.getProfile().getName());
            profile.setRole(response.getProfile().getRole());
            return profile;
        }
        return null;
    }

    /**
     * Отправить запрос в основное приложение
     */
    private UserValidationResponse sendRequest(UUID userId, String action) {
        String requestId = UUID.randomUUID().toString();
        CompletableFuture<UserValidationResponse> future = new CompletableFuture<>();
        pendingRequests.put(requestId, future);

        // Создаем объект, а не строку!
        UserValidationRequest request = new UserValidationRequest();
        request.setIdRequest(requestId);
        request.setIdUser(userId);
        request.setAction(action);

        log.info("Отправлен запрос {} для пользователя {} с действием {}", requestId, userId, action);

        // Отправляем объект, Kafka сама сериализует в JSON
        kafkaTemplate.send("user-validation-requests", request);

        try {
            return future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Таймаут ожидания ответа для запроса {}: {}", requestId, e.getMessage());
            return null;
        }
    }

    /**
     * Слушаем ответы от основного приложения (принимаем как String)
     */
    @KafkaListener(topics = "user-validation-responses", groupId = "chat-microservice")
    public void handleValidationResponse(String message) {  // ← Принимаем String
        log.debug("📨 Получен ответ от основного приложения: {}", message);

        try {
            // Десериализуем JSON в объект
            UserValidationResponse response = objectMapper.readValue(message, UserValidationResponse.class);
            log.debug("✅ Десериализован ответ для запроса {}: exists={}",
                    response.getIdRequest(), response.isExists());

            CompletableFuture<UserValidationResponse> future = pendingRequests.remove(response.getIdRequest());
            if (future != null) {
                future.complete(response);
            } else {
                log.warn("⚠️ Получен ответ для неизвестного запроса: {}", response.getIdRequest());
            }

        } catch (Exception e) {
            log.error("❌ Ошибка десериализации ответа: {}", e.getMessage(), e);
        }
    }
}
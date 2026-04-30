package com.rent.healthbookmessenger.websocket;

import com.rent.healthbookmessenger.message.dto.Message;
import com.rent.healthbookmessenger.message.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Controller
public class WebSocketController {

    private static final Logger log = LoggerFactory.getLogger(WebSocketController.class);

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketController(MessageService messageService,
                               SimpMessagingTemplate messagingTemplate) {
        this.messageService = messageService;
        this.messagingTemplate = messagingTemplate;
    }


    /**
     * Отправка сообщения через WebSocket
     * Клиент отправляет на /app/chat.send
     */
    @MessageMapping("/chat.send")
    public Message sendMessage(@Payload Message request) {
        log.info("WebSocket: сообщение от {} в чат {}", request.idSender(), request.idChat());
        return messageService.sendMessage(request);
    }

    /**
     * Подключение к чату
     * Клиент отправляет на /app/chat.join
     */
    @MessageMapping("/chat.join")
    public void joinChat(@Payload Map<String, Object> payload,
                         SimpMessageHeaderAccessor headerAccessor) {
        Long chatId = Long.valueOf(payload.get("chatId").toString());
        UUID userId = UUID.fromString(payload.get("userId").toString());

        log.info("Пользователь {} подключился к чату {}", userId, chatId);

        // Сохраняем информацию в сессии WebSocket
        Objects.requireNonNull(headerAccessor.getSessionAttributes()).put("chatId", chatId);
        headerAccessor.getSessionAttributes().put("userId", userId);
    }

    /**
     * Статус печатания
     * Клиент отправляет на /app/chat.typing
     */
    @MessageMapping("/chat.typing")
    public void typing(@Payload Map<String, Object> payload) {
        Long chatId = Long.valueOf(payload.get("chatId").toString());
        UUID userId = UUID.fromString(payload.get("userId").toString());
        Boolean isTyping = Boolean.valueOf(payload.get("typing").toString());

        log.debug("Пользователь {} печатает в чате {}: {}", userId, chatId, isTyping);
        // Отправляем статус печатания всем участникам чата (кроме отправителя)
        TypingStatus typingStatus = new TypingStatus(
                chatId,
                userId,
                isTyping,
                System.currentTimeMillis()
        );

        // Отправляем в топик статусов конкретного чата
        messagingTemplate.convertAndSend("/topic/chat." + chatId + ".typing", typingStatus);
    }


}

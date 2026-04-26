package com.rent.healthbookmessenger.message.service;

import com.rent.healthbookmessenger.chat.service.ChatService;
import com.rent.healthbookmessenger.message.dao.MessageDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    private static final Logger log = LoggerFactory.getLogger(MessageService.class);

    private final MessageDao messageDao;
    private final ChatService chatService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
//    private final SimpMessagingTemplate webSocketTemplate;

    @Value("${kafka.topics.chat-events:chat-events}")
    private String chatEventsTopic;
    public MessageService(MessageDao messageDao,
                          ChatService chatService,
                          KafkaTemplate<String, Object> kafkaTemplate) {
        this.messageDao = messageDao;
        this.chatService = chatService;
        this.kafkaTemplate = kafkaTemplate;
    }


//    /**
//     * Отправить сообщение
//     */
//    public MessageResponse sendMessage(SendMessageRequest request) {
//        log.info("Отправка сообщения в чат {} от {}", request.getChatId(), request.getSenderId());
//
//        // 1. Проверяем чат и доступ
//        ChatEntity chat = chatService.getChatEntity(request.getChatId());
//
//        boolean isParticipant = chat.getUser1Id().equals(request.getSenderId()) ||
//                (chat.getUser2Id() != null && chat.getUser2Id().equals(request.getSenderId()));
//
//        if (!isParticipant) {
//            throw new ChatException("Пользователь не является участником чата");
//        }
//
//        // 2. Проверяем, активен ли чат
//        if (!"ACTIVE".equals(chat.getStatus())) {
//            throw new ChatException("Чат закрыт, нельзя отправить сообщение");
//        }
//
//        // 3. Создаем сообщение
//        MessageEntity message = new MessageEntity();
//        message.setChatId(request.getChatId());
//        message.setSenderId(request.getSenderId());
//        message.setContent(request.getText());
//        message.setSentAt(LocalDateTime.now());
//        message.setEdited(false);
//        message.setDeleted(false);
//        message.setReadAt(null);
//
//        MessageEntity savedMessage = messageDao.create(message);
//        log.info("Сообщение сохранено с ID: {}", savedMessage.getId());
//
//        // 4. Обновляем время последнего сообщения в чате
//        chatService.updateLastMessageTime(request.getChatId(), LocalDateTime.now());
//
//        // 5. Определяем получателя и отправляем через WebSocket
//        UUID recipientId = getRecipientId(chat, request.getSenderId());
//        if (recipientId != null) {
//            webSocketTemplate.convertAndSendToUser(
//                    recipientId.toString(),
//                    "/queue/messages",
//                    convertToResponse(savedMessage)
//            );
//            log.debug("Сообщение отправлено получателю {}", recipientId);
//        }
//
//        // 6. Отправляем событие в Kafka
//        ChatEvent event = new ChatEvent(
//                "MESSAGE_SENT",
//                request.getChatId(),
//                request.getSenderId(),
//                savedMessage,
//                LocalDateTime.now()
//        );
//        kafkaTemplate.send(chatEventsTopic, event);
//
//        return convertToResponse(savedMessage);
//    }
//
//    /**
//     * Получить сообщения чата
//     */
//    public List<MessageResponse> getMessages(Long chatId, UUID userId) {
//        log.info("Получение сообщений чата {} для пользователя {}", chatId, userId);
//
//        // Проверяем доступ
//        if (!chatService.validateChatAccess(chatId, userId)) {
//            throw new ChatException("Нет доступа к этому чату");
//        }
//
//        // Получаем сообщения
//        List<MessageEntity> messages = messageDao.findByChatId(chatId);
//
//        // Отмечаем как прочитанные (сообщения от других)
//        messageDao.markMessagesAsRead(chatId, userId, LocalDateTime.now());
//
//        return messages.stream()
//                .map(this::convertToResponse)
//                .toList();
//    }
//
//    /**
//     * Редактировать сообщение
//     */
//    public MessageResponse editMessage(Long messageId, Long chatId, UUID userId, String newContent) {
//        log.info("Редактирование сообщения {} пользователем {}", messageId, userId);
//
//        // Находим сообщение
//        MessageEntity message = messageDao.findById(messageId)
//                .orElseThrow(() -> new ChatException("Сообщение не найдено"));
//
//        // Проверяем, что отправитель тот же
//        if (!message.getSenderId().equals(userId)) {
//            throw new ChatException("Нельзя редактировать чужие сообщения");
//        }
//
//        // Редактируем
//        messageDao.editMessage(messageId, newContent);
//
//        // Получаем обновленное сообщение
//        MessageEntity updatedMessage = messageDao.findById(messageId).get();
//
//        // Отправляем обновление через WebSocket
//        ChatEntity chat = chatService.getChatEntity(chatId);
//        UUID recipientId = getRecipientId(chat, userId);
//
//        if (recipientId != null) {
//            webSocketTemplate.convertAndSendToUser(
//                    recipientId.toString(),
//                    "/queue/message-updated",
//                    convertToResponse(updatedMessage)
//            );
//        }
//
//        return convertToResponse(updatedMessage);
//    }
//
//    /**
//     * Удалить сообщение
//     */
//    public void deleteMessage(Long messageId, Long chatId, UUID userId) {
//        log.info("Удаление сообщения {} пользователем {}", messageId, userId);
//
//        MessageEntity message = messageDao.findById(messageId)
//                .orElseThrow(() -> new ChatException("Сообщение не найдено"));
//
//        if (!message.getSenderId().equals(userId)) {
//            throw new ChatException("Нельзя удалять чужие сообщения");
//        }
//
//        messageDao.softDeleteMessage(messageId);
//
//        // Уведомляем через WebSocket
//        ChatEntity chat = chatService.getChatEntity(chatId);
//        UUID recipientId = getRecipientId(chat, userId);
//
//        if (recipientId != null) {
//            webSocketTemplate.convertAndSendToUser(
//                    recipientId.toString(),
//                    "/queue/message-deleted",
//                    messageId
//            );
//        }
//    }
//
//    /**
//     * Получить ID получателя (не отправителя)
//     */
//    private UUID getRecipientId(ChatEntity chat, UUID senderId) {
//        if (chat.getUser1Id().equals(senderId)) {
//            return chat.getUser2Id();
//        } else {
//            return chat.getUser1Id();
//        }
//    }
//
//    private MessageResponse convertToResponse(MessageEntity entity) {
//        return new MessageResponse(
//                entity.getId(),
//                entity.getChatId(),
//                entity.getSenderId(),
//                entity.getContent(),
//                entity.getSentAt(),
//                entity.getEdited(),
//                entity.getDeleted()
//        );
//    }
}

package com.rent.healthbookmessenger.message.service;

import com.rent.healthbookmessenger.chat.dao.enums.ChatStatus;
import com.rent.healthbookmessenger.chat.dto.enums.ChatAction;
import com.rent.healthbookmessenger.chat.entity.ChatEntity;
import com.rent.healthbookmessenger.chat.service.ChatService;
import com.rent.healthbookmessenger.message.dao.MessageDao;
import com.rent.healthbookmessenger.message.dto.IdMessage;
import com.rent.healthbookmessenger.message.dto.Message;
import com.rent.healthbookmessenger.message.dto.Messages;
import com.rent.healthbookmessenger.message.entity.MessageEntity;
import com.rent.healthbookmessenger.web.exception.EntityNotFoundException;
import com.rent.healthbookmessenger.web.exception.ValidationException;
import com.rent.healthbookmessenger.web.kafka.dto.ChatEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class MessageService {

    private static final Logger log = LoggerFactory.getLogger(MessageService.class);

    private final MessageDao messageDao;
    private final ChatService chatService;
    private final MessageMapper mapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final SimpMessagingTemplate webSocketTemplate;

    @Value("${kafka.topics.chat-events:chat-events}")
    private String chatEventsTopic;
    public MessageService(MessageDao messageDao,
                          ChatService chatService, MessageMapper mapper,
                          KafkaTemplate<String, Object> kafkaTemplate,
                          SimpMessagingTemplate webSocketTemplate) {
        this.messageDao = messageDao;
        this.chatService = chatService;
        this.mapper = mapper;
        this.kafkaTemplate = kafkaTemplate;
        this.webSocketTemplate = webSocketTemplate;
    }

    @Transactional
    public Message sendMessage(Message request) {
        // 1. Проверяем чат и доступ
        ChatEntity chat = chatService.getChatEntity(request.idChat());
        boolean isParticipant = chat.getIdProfile().equals(request.idSender()) ||
                (chat.getIdConsultant() != null && chat.getIdConsultant().equals(request.idSender()));
        if (!isParticipant) {
            throw new ValidationException("Пользователь не является участником чата");
        }
        // 2. Проверяем, активен ли чат
        if (!ChatStatus.ACTIVE.name().equals(chat.getStatus())) {
            throw new ValidationException("Чат закрыт, нельзя отправить сообщение");
        }

        // 3. Создаем сообщение
        MessageEntity message = new MessageEntity();
        message.setIdChat(request.idChat());
        message.setIdSender(request.idSender());
        message.setContent(request.content());
        message.setSentAt(LocalDateTime.now());
        MessageEntity savedMessage = messageDao.create(message);
        log.info("Сообщение сохранено с ID: {}", savedMessage.getId());
        // 4. Обновляем время последнего сообщения в чате
//        chatService.(request.idChat(), LocalDateTime.now());

        // 5. Определяем получателя и отправляем через WebSocket
        UUID recipientId = getRecipientId(chat, request.idSender());
        if (recipientId != null) {
            webSocketTemplate.convertAndSendToUser(
                    recipientId.toString(),
                    "/queue/messages",
                    mapper.toDomainMessage(savedMessage)
            );
            log.debug("Сообщение отправлено получателю {}", recipientId);
        }

        // 6. Отправляем событие в Kafka
        ChatEvent event = new ChatEvent(
                ChatAction.MESSAGE_SENT.name(),
                request.idChat(),
                request.idSender(),
                savedMessage,
                LocalDateTime.now()
        );
        kafkaTemplate.send(chatEventsTopic, event);

        return mapper.toDomainMessage(savedMessage);
    }

    public Messages getMessages(Long idChat, UUID idProfile) {
        log.info("Получение сообщений чата {} для пользователя {}", idChat, idProfile);
        // Проверяем доступ
        if (!chatService.validateChatAccess(idChat, idProfile)) {
            throw new ValidationException("Нет доступа к этому чату");
        }
        // Получаем сообщения
        var messages = messageDao.getByIdChat(idChat);
        return new Messages(messages.list.stream()
                .map(mapper::toDomainMessage).toList());
    }

    @Transactional
    public Message editMessage(IdMessage id, String newContent) {
        MessageEntity message = messageDao.getById(id.idMessage())
                .orElseThrow(() -> new ValidationException("Сообщение не найдено"));
        // Проверяем, что отправитель тот же
        if (!message.getIdSender().equals(id.idProfile())) {
            throw new ValidationException("Нельзя редактировать чужие сообщения");
        }
        // Редактируем
        messageDao.editMessage(id.idMessage(), newContent);
        // Получаем обновленное сообщение
        MessageEntity updatedMessage = messageDao.getById(id.idMessage())
                .orElseThrow(() -> new ValidationException("Сообщение не найдено"));
        // Отправляем обновление через WebSocket
        ChatEntity chat = chatService.getChatEntity(id.idChat());
        UUID recipientId = getRecipientId(chat, id.idProfile());
        if (recipientId != null) {
            webSocketTemplate.convertAndSendToUser(
                    recipientId.toString(),
                    "/queue/message-updated",
                    mapper.toDomainMessage(updatedMessage)
            );
        }
        return  mapper.toDomainMessage(updatedMessage);
    }

    @Transactional
    public void deleteMessage(IdMessage id) {
        MessageEntity message = messageDao.getById(id.idMessage())
                .orElseThrow(() -> new EntityNotFoundException("Сообщение не найдено"));
        if (!message.getIdSender().equals(id.idProfile())) {
            throw new ValidationException("Нельзя удалять чужие сообщения");
        }
        // Уведомляем через WebSocket
        ChatEntity chat = chatService.getChatEntity(id.idChat());
        UUID recipientId = getRecipientId(chat, id.idProfile());
        if (recipientId != null) {
            webSocketTemplate.convertAndSendToUser(
                    recipientId.toString(),
                    "/queue/message-deleted",
                    id.idMessage()
            );
        }
        if (!messageDao.deleteMessage(id.idMessage())) {
            throw new ValidationException(
                    String.format("Error in delete message by id = %s", id.idMessage()));
        }
    }

    /**
     * Получить ID получателя (не отправителя)
     */
    private UUID getRecipientId(ChatEntity chat, UUID senderId) {
        if (chat.getIdProfile().equals(senderId)) {
            return chat.getIdConsultant();
        } else {
            return chat.getIdProfile();
        }
    }
}

package com.rent.healthbookmessenger.chat.service;


import com.rent.healthbookmessenger.chat.dao.ChatDao;
import com.rent.healthbookmessenger.chat.dao.enums.ChatStatus;
import com.rent.healthbookmessenger.chat.dto.Chat;
import com.rent.healthbookmessenger.chat.dto.Chats;
import com.rent.healthbookmessenger.chat.dto.enums.ChatAction;
import com.rent.healthbookmessenger.chat.entity.ChatEntity;
import com.rent.healthbookmessenger.profile.dto.Profile;
import com.rent.healthbookmessenger.profile.dto.enums.ProfileRole;
import com.rent.healthbookmessenger.profile.service.ProfileService;
import com.rent.healthbookmessenger.web.exception.EntityNotFoundException;
import com.rent.healthbookmessenger.web.exception.ValidationException;
import com.rent.healthbookmessenger.web.kafka.dto.ChatEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ChatService {
    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    private final ChatDao chatDao;
    private final ProfileService profileService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ChatMapper mapper;
    private final SimpMessagingTemplate webSocketTemplate;

    @Value("${kafka.topics.chat-events:chat-events}")
    private String chatEventsTopic;

    @Autowired
    public ChatService(ChatDao chatDao,
                       ProfileService profileService,
                       KafkaTemplate<String, Object> kafkaTemplate,
                       ChatMapper mapper,
                       SimpMessagingTemplate webSocketTemplate) {
        this.chatDao = chatDao;
        this.profileService = profileService;
        this.kafkaTemplate = kafkaTemplate;
        this.mapper = mapper;
        this.webSocketTemplate = webSocketTemplate;
    }

    public Chats getAvailableChats() {
        var chats = chatDao.getByStatus(ChatStatus.WAITING);
        return new Chats(chats.list.stream()
                .map(mapper::toDomainChat).toList());
    }

    public Chats getConsultantActiveChats(UUID idConsultant) {
        log.info("Получение активных чатов консультанта {}", idConsultant);
        var chats = chatDao.getActiveChatsByIdConsultant(idConsultant);
        return new Chats(chats.list.stream()
                .map(mapper::toDomainChat).toList());
    }

    public Chat getProfileChat(UUID idProfile) {
        log.info("Получение активного чата пользователя {}", idProfile);
        ChatEntity chat = chatDao.getChatByIdProfile(idProfile)
                .orElseThrow(() -> new EntityNotFoundException("Активный чат не найден"));
        return mapper.toDomainChat(chat);
    }

    /**
     * Получить все чаты пользователя
     */
    public Chats getProfileChats(UUID idProfile) {
        log.info("Получение всех чатов пользователя {}", idProfile);
        var chatsEntity = chatDao.getAllChatsByIdProfile(idProfile);
        return new Chats(chatsEntity.list.stream().map(mapper::toDomainChat)
                .toList());
    }

    /**
     * Проверить существование чата и доступ пользователя
     */
    public boolean validateChatAccess(Long idChat, UUID idProfile) {
        var chat = chatDao.getById(idChat)
                .orElseThrow(() -> new EntityNotFoundException("Чат не найден"));
        return chat.getIdProfile().equals(idProfile) ||
                (chat.getIdConsultant() != null && chat.getIdConsultant().equals(idProfile));
    }

    /**
     * Получить чат по ID (для внутреннего использования)
     */
    public ChatEntity getChatEntity(Long idChat) {
        return chatDao.getById(idChat)
                .orElseThrow(() -> new EntityNotFoundException("Чат не найден"));
    }


    @Transactional
    public void closeChat(Long idChat, UUID idProfile) {
        ChatEntity chat = chatDao.getById(idChat)
                .orElseThrow(() -> new EntityNotFoundException("Чат не найден"));
        // Проверяем права (закрыть может либо создатель, либо консультант)
        boolean canClose = chat.getIdProfile().equals(idProfile) ||
                (chat.getIdConsultant() != null && chat.getIdConsultant().equals(idProfile));
        if (!canClose) {
            throw new RuntimeException("Нет прав на закрытие чата");
        }
        chatDao.closeChat(idChat);
        ChatEvent event = new ChatEvent(
                ChatAction.CHAT_CLOSED.name(),
                idChat,
                idProfile,
                null,
                LocalDateTime.now()
        );
        kafkaTemplate.send(chatEventsTopic, event);

        // Уведомляем второго участника через WebSocket
        UUID otherUserId = chat.getIdProfile().equals(idProfile) ? chat.getIdConsultant() : chat.getIdProfile();
        if (otherUserId != null) {
            webSocketTemplate.convertAndSendToUser(
                    otherUserId.toString(),
                    "/queue/chat-status",
                    "Чат закрыт"
            );
        }
    }


    @Transactional
    public Chat createChat(UUID idProfile) {
        // 1. Проверяем, существует ли пользователь (через Kafka)
        boolean userExists = profileService.userExists(idProfile);
        if (!userExists) {
            throw new EntityNotFoundException("Пользователь с ID " + idProfile + " не найден");
        }
        // 2. Проверяем, нет ли уже активного чата
        if (chatDao.getChatByIdProfile(idProfile).isPresent()) {
            throw new ValidationException("У пользователя уже есть активный чат");
        }
        // 3. Создаем чат
        ChatEntity chat = new ChatEntity();
        chat.setIdProfile(idProfile);
        chat.setIdConsultant(null);
        chat.setStatus(ChatStatus.WAITING.name());
        chat.setCreatedAt(LocalDateTime.now());
        chat.setLastMessageAt(null);
        ChatEntity savedChat = chatDao.create(chat);
        // 4. Отправляем событие в Kafka
        ChatEvent event = new ChatEvent(
                ChatAction.CHAT_CREATED.name(),
                savedChat.getId(),
                idProfile,
                savedChat,
                LocalDateTime.now()
        );
        kafkaTemplate.send(chatEventsTopic, event);
        return mapper.toDomainChat(savedChat);
    }

    @Transactional
    public Chat takeChat(Long idChat, UUID idConsultant) {
        // 1. Проверяем, что консультант существует и имеет роль CONSULTANT
        Profile consultant = profileService.getProfile(idConsultant);
        if (consultant == null) {
            throw new EntityNotFoundException("Консультант не найден");
        }
        if (!ProfileRole.CONSULTANT.name().equals(consultant.getRole()) &&
                !ProfileRole.ADMIN.name().equals(consultant.getRole())) {
            throw new ValidationException(
                    String.format("Пользователь не имеет права быть консультантом. Роль: %s",
                            consultant.getRole()));
        }
        // 2. Находим чат
        ChatEntity chat = chatDao.getById(idChat)
                .orElseThrow(() -> new EntityNotFoundException("Чат не найден"));
        // 3. Проверяем статус
        if (!ChatStatus.WAITING.name().equals(chat.getStatus())) {
            throw new ValidationException(
                    String.format("Чат уже не доступен для взятия. Текущий статус: %s",
                            chat.getStatus()));
        }
        // 4. Присваиваем консультанта
        chat.setIdConsultant(idConsultant);
        chat.setStatus(ChatStatus.ACTIVE.name());
        chatDao.update(chat);
        // 5. Отправляем событие в Kafka
        ChatEvent event = new ChatEvent(
                ChatAction.CONSULTANT_JOINED.name(),
                idChat,
                idConsultant,
                chat,
                LocalDateTime.now()
        );
        kafkaTemplate.send(chatEventsTopic, event);
        // 6. Уведомляем пользователя через WebSocket
        webSocketTemplate.convertAndSendToUser(
                chat.getIdProfile().toString(),
                "/queue/chat-status",
                "Консультант " + consultant.getName() + " присоединился к чату"
        );
        return mapper.toDomainChat(chat);
    }


}

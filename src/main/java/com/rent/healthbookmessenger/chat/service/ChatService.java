package com.rent.healthbookmessenger.chat.service;


import com.rent.healthbookmessenger.chat.dao.ChatDao;
import com.rent.healthbookmessenger.chat.dao.enums.ChatStatus;
import com.rent.healthbookmessenger.chat.dto.Chats;
import com.rent.healthbookmessenger.profile.service.ProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ChatService {
    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    private final ChatDao chatDao;
    private final ProfileService profileService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ChatMapper mapper;
//    private final SimpMessagingTemplate webSocketTemplate;

    @Value("${kafka.topics.chat-events:chat-events}")
    private String chatEventsTopic;

    @Autowired
    public ChatService(ChatDao chatDao,
                       ProfileService profileService,
                       KafkaTemplate<String, Object> kafkaTemplate, ChatMapper mapper) {
        this.chatDao = chatDao;
        this.profileService = profileService;
        this.kafkaTemplate = kafkaTemplate;
//        this.webSocketTemplate = webSocketTemplate;
        this.mapper = mapper;
    }

    public Chats getAvailableChats() {
        log.info("Получение доступных чатов");
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

//    public ChatResponse getUserActiveChat(UUID userId) {
//        log.info("Получение активного чата пользователя {}", userId);
//        ChatEntity chat = chatDao.findActiveChatByUserId(userId)
//                .orElseThrow(() -> new ChatException("Активный чат не найден"));
//        return convertToResponse(chat);
//    }
//
//    /**
//     * Получить все чаты пользователя
//     */
//    public List<ChatResponse> getUserChats(UUID userId) {
//        log.info("Получение всех чатов пользователя {}", userId);
//        List<ChatEntity> chats = chatDao.findAllChatsByUserId(userId);
//        return chats.stream()
//                .map(this::convertToResponse)
//                .toList();
//    }
//
//    /**
//     * Проверить существование чата и доступ пользователя
//     */
//    public boolean validateChatAccess(Long chatId, UUID userId) {
//        return chatDao.findById(chatId)
//                .map(chat -> chat.getUser1Id().equals(userId) ||
//                        (chat.getUser2Id() != null && chat.getUser2Id().equals(userId)))
//                .orElse(false);
//    }
//
//    /**
//     * Получить чат по ID (для внутреннего использования)
//     */
//    public ChatEntity getChatEntity(Long chatId) {
//        return chatDao.findById(chatId)
//                .orElseThrow(() -> new ChatException("Чат не найден"));
//    }
//
//    private ChatResponse convertToResponse(ChatEntity entity) {
//        return new ChatResponse(
//                entity.getId(),
//                entity.getUser1Id(),
//                entity.getUser2Id(),
//                entity.getStatus(),
//                entity.getCreatedAt(),
//                entity.getLastMessageAt()
//        );
//    }


//    public void closeChat(Long chatId, UUID userId) {
//        log.info("Закрытие чата {} пользователем {}", chatId, userId);
//
//        ChatEntity chat = chatDao.findById(chatId)
//                .orElseThrow(() -> new ChatException("Чат не найден"));
//
//        // Проверяем права (закрыть может либо создатель, либо консультант)
//        boolean canClose = chat.getUser1Id().equals(userId) ||
//                (chat.getUser2Id() != null && chat.getUser2Id().equals(userId));
//
//        if (!canClose) {
//            throw new ChatException("Нет прав на закрытие чата");
//        }
//
//        chatDao.closeChat(chatId);
//
//        // Отправляем событие в Kafka
//        ChatEvent event = new ChatEvent(
//                "CHAT_CLOSED",
//                chatId,
//                userId,
//                null,
//                LocalDateTime.now()
//        );
//        kafkaTemplate.send(chatEventsTopic, event);
//
//        // Уведомляем второго участника через WebSocket
//        UUID otherUserId = chat.getUser1Id().equals(userId) ? chat.getUser2Id() : chat.getUser1Id();
//        if (otherUserId != null) {
//            webSocketTemplate.convertAndSendToUser(
//                    otherUserId.toString(),
//                    "/queue/chat-status",
//                    "Чат закрыт"
//            );
//        }
//    }


//    /**
//     * Создать новый чат
//     */
//    public ChatResponse createChat(UUID userId) {
//        log.info("Создание чата для пользователя: {}", userId);
//
//        // 1. Проверяем, существует ли пользователь (через Kafka)
//        boolean userExists = profileValidationService.userExists(userId);
//        if (!userExists) {
//            throw new ChatException("Пользователь с ID " + userId + " не найден");
//        }
//
//        // 2. Проверяем, нет ли уже активного чата
//        if (chatDao.findActiveChatByUserId(userId).isPresent()) {
//            throw new ChatException("У пользователя уже есть активный чат");
//        }
//
//        // 3. Создаем чат
//        ChatEntity chat = new ChatEntity();
//        chat.setUser1Id(userId);
//        chat.setUser2Id(null);
//        chat.setStatus("WAITING_FOR_CONSULTANT");
//        chat.setCreatedAt(LocalDateTime.now());
//        chat.setLastMessageAt(null);
//
//        ChatEntity savedChat = chatDao.create(chat);
//        log.info("Чат создан с ID: {}", savedChat.getId());
//
//        // 4. Отправляем событие в Kafka
//        ChatEvent event = new ChatEvent(
//                "CHAT_CREATED",
//                savedChat.getId(),
//                userId,
//                savedChat,
//                LocalDateTime.now()
//        );
//        kafkaTemplate.send(chatEventsTopic, event);
//
//        return convertToResponse(savedChat);
//    }
//
//    /**
//     * Взять чат в работу (консультант)
//     */
//    public ChatResponse takeChat(Long chatId, UUID consultantId) {
//        log.info("Консультант {} берет чат {}", consultantId, chatId);
//
//        // 1. Проверяем, что консультант существует и имеет роль CONSULTANT
//        Profile consultant = profileValidationService.getProfile(consultantId);
//        if (consultant == null) {
//            throw new ChatException("Консультант не найден");
//        }
//
//        if (!"CONSULTANT".equals(consultant.getRole()) && !"ADMIN".equals(consultant.getRole())) {
//            throw new ChatException("Пользователь не имеет права быть консультантом. Роль: " + consultant.getRole());
//        }
//
//        // 2. Находим чат
//        ChatEntity chat = chatDao.findById(chatId)
//                .orElseThrow(() -> new ChatException("Чат не найден"));
//
//        // 3. Проверяем статус
//        if (!"WAITING_FOR_CONSULTANT".equals(chat.getStatus())) {
//            throw new ChatException("Чат уже не доступен для взятия. Текущий статус: " + chat.getStatus());
//        }
//
//        // 4. Присваиваем консультанта
//        chat.setUser2Id(consultantId);
//        chat.setStatus("ACTIVE");
//        chatDao.update(chat);
//
//        // 5. Отправляем событие в Kafka
//        ChatEvent event = new ChatEvent(
//                "CONSULTANT_JOINED",
//                chatId,
//                consultantId,
//                chat,
//                LocalDateTime.now()
//        );
//        kafkaTemplate.send(chatEventsTopic, event);
//
//        // 6. Уведомляем пользователя через WebSocket
//        webSocketTemplate.convertAndSendToUser(
//                chat.getUser1Id().toString(),
//                "/queue/chat-status",
//                "Консультант " + consultant.getName() + " присоединился к чату"
//        );
//
//        return convertToResponse(chat);
//    }


}

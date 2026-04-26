package com.rent.healthbookmessenger.chat.controller;


import com.rent.healthbookmessenger.chat.dto.Chat;
import com.rent.healthbookmessenger.chat.dto.Chats;
import com.rent.healthbookmessenger.chat.service.ChatService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/chats")
@Tag(name = "Чаты", description = "Управление чатами")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

//    @PostMapping("/create")
//    public ResponseEntity<Chat> createChat(@RequestParam @NotNull UUID idUser) {
//        Chat chat = chatService.createChat(idUser);
//        return ResponseEntity.ok(chat);
//    }

    /**
     * Получить доступные чаты (для консультанта)
     */
    @GetMapping("/available")
    public ResponseEntity<Chats> getAvailableChats() {
        Chats chats = chatService.getAvailableChats();
        return ResponseEntity.ok(chats);
    }

//    /**
//     * Взять чат в работу (консультант)
//     */
//    @PutMapping("/{chatId}/take")
//    public ResponseEntity<Chat> takeChat(
//            @PathVariable Long chatId,
//            @RequestParam @NotNull UUID consultantId) {
//        Chat chat = chatService.takeChat(chatId, consultantId);
//        return ResponseEntity.ok(chat);
//    }

//    @PutMapping("/{chatId}/close")
//    public ResponseEntity<Void> closeChat(
//            @PathVariable Long chatId,
//            @RequestParam @NotNull UUID userId) {
//        chatService.closeChat(chatId, userId);
//        return ResponseEntity.ok().build();
//    }

    /**
     * Получить активные чаты консультанта
     */
    @GetMapping("/consultant/{consultantId}/active")
    public ResponseEntity<Chats> getConsultantActiveChats(
            @PathVariable UUID consultantId) {
        Chats chats = chatService.getConsultantActiveChats(consultantId);
        return ResponseEntity.ok(chats);
    }

//    /**
//     * Получить активный чат пользователя
//     */
//    @GetMapping("/user/{userId}/active")
//    public ResponseEntity<Chat> getUserActiveChat(@PathVariable UUID userId) {
//        Chat chat = chatService.getActiveChat(userId);
//        return ResponseEntity.ok(chat);
//    }

//    /**
//     * Получить все чаты пользователя
//     */
//    @GetMapping("/user/{userId}/all")
//    public ResponseEntity<Chats> getUserChats(@PathVariable UUID userId) {
//        Chats chats = chatService.getUserChats(userId);
//        return ResponseEntity.ok(chats);
//    }
}

package com.rent.healthbookmessenger.chat.controller;


import com.rent.healthbookmessenger.chat.dto.Chat;
import com.rent.healthbookmessenger.chat.dto.Chats;
import com.rent.healthbookmessenger.chat.service.ChatService;
import com.rent.healthbookmessenger.web.security.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/chats")
@Tag(name = "Чаты", description = "Управление чатами")
public class ChatController {

    Logger logger = LoggerFactory.getLogger(ChatController.class);
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @Operation(summary = "Создать чат")
    @PostMapping("/create")
    public ResponseEntity<Chat> createChat(){
        UUID idProfile = SecurityUtils.getCurrentProfileId();
        logger.info("Called createChat by idProfile = {}", idProfile);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(chatService.createChat(idProfile));
    }

    @Operation(summary = "Получить доступные чаты (для консультанта)")
    @GetMapping("/available")
    public ResponseEntity<Chats> getAvailableChats() {
        logger.info("Called getAvailableChats");
        return ResponseEntity.status(HttpStatus.OK)
                .body(chatService.getAvailableChats());
    }


    @Operation(summary = "Взять чат в рботу (консультант)")
    @PutMapping("/{idChat}/take")
    @PreAuthorize("hasAnyRole('CONSULTANT', 'ADMIN')")
    public ResponseEntity<Chat> takeChat(
            @Parameter(description = "Идентификатор чата")
            @PathVariable Long idChat) {
        UUID idConsultant = SecurityUtils.getCurrentProfileId();
        logger.info("Called takeChat by id = {}, consultant = {}", idChat, idConsultant);
        return ResponseEntity.status(HttpStatus.OK)
                .body(chatService.takeChat(idChat, idConsultant));
    }


    @Operation(summary = "Закрыть чат")
    @PutMapping("/{idChat}/close")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> closeChat(
            @Parameter(description = "Идентификатор чата")
            @PathVariable Long idChat) {
        UUID idProfile = SecurityUtils.getCurrentProfileId();
        logger.info("Called closeChat by id = {}", idChat);
        chatService.closeChat(idChat, idProfile);
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "Получить активные чаты консультанта")
    @GetMapping("/consultant/active")
    @PreAuthorize("hasAnyRole('CONSULTANT', 'ADMIN')")
    public ResponseEntity<Chats> getConsultantActiveChats() {
        UUID idConsultant = SecurityUtils.getCurrentProfileId();
        logger.info("Called getConsultantActiveChats by id = {}", idConsultant);
        return ResponseEntity.status(HttpStatus.OK)
                .body(chatService.getConsultantActiveChats(idConsultant));
    }


    @Operation(summary = "Получить активный чат пользователя")
    @GetMapping("/user/active")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Chat> getProfileChat() {
        UUID idProfile = SecurityUtils.getCurrentProfileId();
        logger.info("Called getProfileActiveChat by id = {}", idProfile);
        return ResponseEntity.status(HttpStatus.OK)
                .body(chatService.getProfileChat(idProfile));
    }


    @Operation(summary = "Получить все чаты пользователя")
    @GetMapping("/user/all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Chats> getProfileChats() {
        UUID idProfile = SecurityUtils.getCurrentProfileId();
        logger.info("Called getProfileChats by idProfile = {}", idProfile);
        return ResponseEntity.status(HttpStatus.OK)
                .body(chatService.getProfileChats(idProfile));
    }
}

package com.rent.healthbookmessenger.message.controller;

import com.rent.healthbookmessenger.message.dto.IdMessage;
import com.rent.healthbookmessenger.message.dto.Message;
import com.rent.healthbookmessenger.message.dto.Messages;
import com.rent.healthbookmessenger.message.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
@Tag(name = "Сообщения", description = "Управление сообщениями")
public class MessageController {

    Logger logger = LoggerFactory.getLogger(MessageController.class);
    private final MessageService service;

    public MessageController(MessageService service) {
        this.service = service;
    }


    @Operation(summary = "Отправить сообщение")
    @PostMapping("/send")
    public ResponseEntity<Message> sendMessage(
            @Parameter(description = "Данные сообщения")
            @Valid @RequestBody Message request) {
        logger.info("Called sendMessage by idChat = {}", request.idChat());
        return ResponseEntity.status(HttpStatus.OK)
                .body(service.sendMessage(request));
    }

    @Operation(summary = "Получить сообщение чата")
    @GetMapping("/chat/{idChat}")
    public ResponseEntity<Messages> getMessages(
            @Parameter(description = "Идентфиикатор чата")
            @PathVariable Long idChat,
            @Parameter(description = "Идентификатор профиля")
            @RequestParam UUID idProfile) {
        logger.info("Called getMessage by idProfile = {} in chat = {}", idProfile, idChat);
        return ResponseEntity.status(HttpStatus.OK)
                .body(service.getMessages(idChat, idProfile));
    }

    @Operation(summary = "Редактировать сообщение")
    @PutMapping("/{idMessage}")
    public ResponseEntity<Message> editMessage(
            @Parameter(description = "Идентфиикатор чата")
            @RequestParam Long idChat,
            @Parameter(description = "Идентфиикатор сообщения")
            @PathVariable Long idMessage,
            @Parameter(description = "Идентфиикатор профиля")
            @RequestParam UUID idProfile,
            @Parameter(description = "Текстовое сообщение")
            @RequestBody String content) {
        logger.info("Called editMessage by id = {}, profile = {}, in chat = {}",
                idMessage, idProfile, idChat);
        var id = new IdMessage(idChat, idMessage, idProfile);
        return ResponseEntity.status(HttpStatus.OK)
                .body(service.editMessage(id, content));
    }

    @Operation(summary = "Удалить сообщение")
    @DeleteMapping("/{idMessage}")
    public ResponseEntity<Void> deleteMessage(
            @Parameter(description = "Идентфиикатор чата")
            @RequestParam Long idChat,
            @Parameter(description = "Идентфиикатор сообщения")
            @PathVariable Long idMessage,
            @Parameter(description = "Идентфиикатор профиля")
            @RequestParam UUID idProfile) {
        logger.info("Called deleteMessage by id = {}, profile = {}, in chat = {}",
                idMessage, idProfile, idChat);
        var id = new IdMessage(idChat, idMessage, idProfile);
        service.deleteMessage(id);
        return ResponseEntity.ok().build();
    }
}

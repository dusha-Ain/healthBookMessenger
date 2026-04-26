package com.rent.healthbookmessenger.message.controller;

import com.rent.healthbookmessenger.message.service.MessageService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
public class MessageController {

    private final MessageService service;

    public MessageController(MessageService service) {
        this.service = service;
    }


//    /**
//     * Отправить сообщение
//     */
//    @PostMapping("/send")
//    public ResponseEntity<MessageResponse> sendMessage(@Valid @RequestBody SendMessageRequest request) {
//        MessageResponse message = messageService.sendMessage(request);
//        return ResponseEntity.ok(message);
//    }
//
//    /**
//     * Получить сообщения чата
//     */
//    @GetMapping("/chat/{chatId}")
//    public ResponseEntity<List<MessageResponse>> getMessages(
//            @PathVariable Long chatId,
//            @RequestParam UUID userId) {
//        List<MessageResponse> messages = messageService.getMessages(chatId, userId);
//        return ResponseEntity.ok(messages);
//    }
//
//    /**
//     * Редактировать сообщение
//     */
//    @PutMapping("/{messageId}")
//    public ResponseEntity<MessageResponse> editMessage(
//            @PathVariable Long messageId,
//            @RequestParam Long chatId,
//            @RequestParam UUID userId,
//            @RequestBody Map<String, String> body) {
//        String newContent = body.get("content");
//        MessageResponse message = messageService.editMessage(messageId, chatId, userId, newContent);
//        return ResponseEntity.ok(message);
//    }
//
//    /**
//     * Удалить сообщение
//     */
//    @DeleteMapping("/{messageId}")
//    public ResponseEntity<Void> deleteMessage(
//            @PathVariable Long messageId,
//            @RequestParam Long chatId,
//            @RequestParam UUID userId) {
//        messageService.deleteMessage(messageId, chatId, userId);
//        return ResponseEntity.ok().build();
//    }
}

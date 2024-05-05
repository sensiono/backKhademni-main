package tn.esprit.pi.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.pi.entities.ChatMessage;
import tn.esprit.pi.services.ChatBotService; // Service that handles chatbot logic

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/chatbot")
public class ChatBotController {

    private final ChatBotService chatBotService;

    public ChatBotController(ChatBotService chatBotService) {
        this.chatBotService = chatBotService;
    }

    @PostMapping("/respond")
    public CompletableFuture<ResponseEntity<ChatMessage>> respond(@RequestBody ChatMessage message) {
        // Generate a response asynchronously with a 2-second delay
        return chatBotService.generateResponse(message)
                .thenApply(response -> ResponseEntity.ok(response)); // Wrap the response in ResponseEntity
    }
}

package tn.esprit.pi.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.pi.entities.ChatMessage;
import tn.esprit.pi.entities.User;
import tn.esprit.pi.services.ChatBotService; // Service that handles chatbot logic
import tn.esprit.pi.services.UserServiceImp;

import java.security.Principal;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/chatbot")
public class ChatBotController {

    private final ChatBotService chatBotService;
    private final UserServiceImp userService;

    public ChatBotController(ChatBotService chatBotService, UserServiceImp userService) {
        this.chatBotService = chatBotService;
        this.userService = userService;
    }

    @PostMapping("/respond")
    public CompletableFuture<ResponseEntity<ChatMessage>> respond(@RequestBody ChatMessage message, Principal principal) {
        User currentUser = null;

        if (principal != null) {
            try {
                currentUser = userService.getCurrentUser(principal);
            } catch (Exception e) {
                System.out.println("Error retrieving current user: " + e.getMessage());
            }
        }

        // Generate response based on the user
        return chatBotService.generateResponse(message, currentUser)
                .thenApply(response -> ResponseEntity.ok(response)); // Return response
    }
}

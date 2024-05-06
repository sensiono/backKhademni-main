package tn.esprit.pi.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tn.esprit.pi.entities.ChatMessage;
import tn.esprit.pi.entities.Role;
import tn.esprit.pi.entities.User;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class ChatBotService {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final RestTemplate restTemplate = new RestTemplate();

    public CompletableFuture<ChatMessage> generateResponse(ChatMessage message, User currentUser) {
        return CompletableFuture.supplyAsync(() -> {
            String lowerCaseText = message.getText().toLowerCase(); // Convert to lowercase for easier matching

            // Check if the message contains "claim support"
            if (lowerCaseText.contains("claim support")) {
                if (currentUser == null) {
                    return new ChatMessage("For assistance with claims, you can contact our support team at support@kahadmni.com.", "bot");
                } else if (currentUser.getRole() == Role.Etudiant) {
                    return new ChatMessage("For assistance with claims, you can contact our support team at supportEtudiant@kahadmni.com.", "bot");
                } else if (currentUser.getRole() == Role.Entreprise) {
                    return new ChatMessage("For assistance with claims, you can contact our support team at supportEntreprise@kahadmni.com.", "bot");
                }
            }

            // If "claim support" is not mentioned, proceed with the ML-based response
            String flaskServiceUrl = "http://localhost:5000/predict"; // URL of your Flask ML service
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("question", lowerCaseText); // Send the text to the ML model
            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> responseEntity = restTemplate.exchange(flaskServiceUrl, HttpMethod.POST, request, Map.class);

            String responseText = (String) responseEntity.getBody().get("response"); // Get the predicted response from the Flask service

            // Use the ML-based response if it's valid and there's no static match
            if (responseText != null && !responseText.isEmpty()) {
                return new ChatMessage(responseText, "bot");
            }

            // Default response if no other logic matches
            return new ChatMessage("I'm sorry, I didn't understand that. Can you please rephrase?", "bot");
        }, scheduler).thenApplyAsync(response -> {
            try {
                TimeUnit.SECONDS.sleep(2); // Delay before returning the response
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interrupted state
            }
            return response;
        });
    }
}


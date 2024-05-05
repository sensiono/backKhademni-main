package tn.esprit.pi.services;

import org.springframework.stereotype.Service;
import tn.esprit.pi.entities.ChatMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class ChatBotService {

    // Executor service to manage delayed responses
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private static final Map<String, String> keywordResponses = new HashMap<>();

    static {
        keywordResponses.put("hello", "Hello! How can I assist you today?");
        keywordResponses.put("help", "I am here to help. What do you need assistance with?");
        keywordResponses.put("reclamation", "Please let me know what you would like to reclaim.");

        // New responses for claims
        keywordResponses.put("claim status", "To check your claim status, please provide your claim ID.");
        keywordResponses.put("file a claim", "To file a claim, visit our claims page or contact support.");
        keywordResponses.put("make a claim", "To file a claim, visit our claims page or contact support.");
        keywordResponses.put("claim process", "The claim process includes verification, documentation, and approval.");
        keywordResponses.put("claim support", "For assistance with claims, you can contact our support team at support@kahadmni.com.");

        // New responses for freelance marketplaces
        keywordResponses.put("freelance", "Our freelance marketplace connects freelancers and clients for projects.");
        keywordResponses.put("marketplace", "You can find freelancers for various skills, including writing, design, and programming.");
        keywordResponses.put("hire freelancer", "To hire a freelancer, create a project on our platform and post a job description.");
        keywordResponses.put("post a job", "To post a job, go to the 'Jobs' section and click 'Post a Job'.");
        keywordResponses.put("become a freelancer", "To become a freelancer, create an account and complete your profile.");
        keywordResponses.put("freelance rates", "Freelance rates vary by skill, experience, and project complexity. Contact freelancers for more information.");
    }

    public CompletableFuture<ChatMessage> generateResponse(ChatMessage message) {
        return CompletableFuture.supplyAsync(() -> {
            String lowerCaseText = message.getText().toLowerCase(); // Convert to lowercase for easier matching

            // Check for matching keywords and return the corresponding response
            for (Map.Entry<String, String> entry : keywordResponses.entrySet()) {
                if (lowerCaseText.contains(entry.getKey())) {
                    return new ChatMessage(entry.getValue(), "bot");
                }
            }

            // Default response if no keywords match
            return new ChatMessage("I'm sorry, I didn't understand that. Can you please rephrase?", "bot");
        }, scheduler).thenApplyAsync(response -> {
            try {
                // Introduce a 2-second delay before returning the response
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interrupted state
            }
            return response;
        });
    }
}

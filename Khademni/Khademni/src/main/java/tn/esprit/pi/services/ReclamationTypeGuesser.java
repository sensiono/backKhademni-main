package tn.esprit.pi.services;

import tn.esprit.pi.entities.TypeReclamation;

import java.util.HashMap;
import java.util.Map;

public class ReclamationTypeGuesser {
    private static final Map<String, TypeReclamation> keywordToType = new HashMap<>();

    static {
        // Define keywords for each reclamation type
        keywordToType.put("bug", TypeReclamation.WEBSITE_BUG);
        keywordToType.put("error", TypeReclamation.WEBSITE_BUG);
        keywordToType.put("crash", TypeReclamation.WEBSITE_BUG);
        keywordToType.put("glitch", TypeReclamation.WEBSITE_BUG);
        keywordToType.put("issue", TypeReclamation.WEBSITE_BUG);

        keywordToType.put("payment", TypeReclamation.PAYMENT_PROBLEM);
        keywordToType.put("transaction", TypeReclamation.PAYMENT_PROBLEM);
        keywordToType.put("refund", TypeReclamation.PAYMENT_PROBLEM);
        keywordToType.put("billing", TypeReclamation.PAYMENT_PROBLEM);
        keywordToType.put("charge", TypeReclamation.PAYMENT_PROBLEM);

        keywordToType.put("purchase", TypeReclamation.PURCHASE_PROBLEM);
        keywordToType.put("order", TypeReclamation.PURCHASE_PROBLEM);
        keywordToType.put("delivery", TypeReclamation.PURCHASE_PROBLEM);
        keywordToType.put("shipment", TypeReclamation.PURCHASE_PROBLEM);
        keywordToType.put("item", TypeReclamation.PURCHASE_PROBLEM);
        keywordToType.put("return", TypeReclamation.PURCHASE_PROBLEM);
    }

    public static TypeReclamation guessType(String description) {
        if (description == null) {
            return TypeReclamation.OTHER; // Default fallback
        }

        for (Map.Entry<String, TypeReclamation> entry : keywordToType.entrySet()) {
            if (description.toLowerCase().contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        return TypeReclamation.OTHER;
    }
}

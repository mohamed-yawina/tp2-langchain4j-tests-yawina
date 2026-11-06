
package ma.emsi.yawina;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.model.output.TokenUsage;

public class Test2Couts {

    public static void main(String[] args) {

        // 1. Récupérer la clé API Gemini
        String apiKey = System.getenv("GEMINI_KEY");

        if (apiKey == null || apiKey.isBlank()) {
            System.err.println("Erreur : variable GEMINI_KEY introuvable.");
            return;
        }

        // 2. Configurer le modèle Gemini
        GoogleGenAiChatModel model = GoogleGenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gemini-3.5-flash-lite")
                .temperature(0.7)
                .build();

        // 3. Envoyer une question
        String question = "Explique en trois phrases ce qu'est un LLM.";

        ChatResponse response = model.chat(
                UserMessage.from(question)
        );

        // 4. Afficher la question et la réponse
        System.out.println("Question : " + question);
        System.out.println("Réponse : " + response.aiMessage().text());

        // 5. Récupérer les statistiques des tokens
        TokenUsage usage = response.tokenUsage();

        System.out.println("\n--- Consommation de tokens ---");

        if (usage != null) {

            int tokensEntree = usage.inputTokenCount();
            int tokensSortie = usage.outputTokenCount();
            int totalTokens = usage.totalTokenCount();

            System.out.println("Tokens d'entrée : " + tokensEntree);
            System.out.println("Tokens de sortie : " + tokensSortie);
            System.out.println("Total : " + totalTokens);

            // 6. Tarifs standards estimatifs en USD
            // Pour 1 million de tokens
            double prixEntree = 0.30;
            double prixSortie = 2.50;

            // 7. Calculer le coût estimé
            double coutEntree =
                    (tokensEntree * prixEntree) / 1_000_000.0;

            double coutSortie =
                    (tokensSortie * prixSortie) / 1_000_000.0;

            double coutTotal = coutEntree + coutSortie;

            // 8. Afficher les coûts
            System.out.println("\n--- Estimation du coût ---");

            System.out.printf("Coût entrée : %.8f USD%n", coutEntree);
            System.out.printf("Coût sortie : %.8f USD%n", coutSortie);
            System.out.printf("Coût total : %.8f USD%n", coutTotal);

        } else {
            System.out.println("Statistiques de tokens indisponibles.");
        }
    }
}

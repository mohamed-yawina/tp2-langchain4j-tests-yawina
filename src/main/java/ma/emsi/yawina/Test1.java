
package ma.emsi.yawina;

import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;

public class Test1 {

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

        // 3. Première question : donner son nom
        String question1 = "Bonjour, je m'appelle Mohamed Yawina.";

        String reponse1 = model.chat(question1);

        System.out.println("Question 1 : " + question1);
        System.out.println("Réponse 1 : " + reponse1);

        System.out.println("--------------------------------");

        // 4. Deuxième question : tester la mémoire
        String question2 = "Comment je m'appelle ?";

        String reponse2 = model.chat(question2);

        System.out.println("Question 2 : " + question2);
        System.out.println("Réponse 2 : " + reponse2);
    }
}

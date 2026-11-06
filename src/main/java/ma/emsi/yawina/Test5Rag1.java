
package ma.emsi.yawina;

import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Test5Rag1 {

    public static void main(String[] args) throws Exception {

        // 1. Récupérer la clé API Gemini
        String apiKey = System.getenv("GEMINI_KEY");

        if (apiKey == null || apiKey.isBlank()) {
            System.err.println("Erreur : GEMINI_KEY introuvable.");
            return;
        }

        // 2. Initialiser le modèle Gemini
        GoogleGenAiChatModel model = GoogleGenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gemini-3.5-flash-lite")
                .temperature(0.0)
                .build();

        // 3. Charger le fichier infos.txt
        InputStream inputStream = Test5Rag1.class
                .getClassLoader()
                .getResourceAsStream("infos.txt");

        if (inputStream == null) {
            System.err.println("Erreur : fichier infos.txt introuvable.");
            return;
        }

        String document;
        try (inputStream) {
            document = new String(
                    inputStream.readAllBytes(),
                    StandardCharsets.UTF_8
            );
        }

        System.out.println("=== DOCUMENT CHARGÉ ===");
        System.out.println(document);

        // 4. Poser une question sans document
        String question = "Quelle est la capitale de la France ?";

        System.out.println("\n=== SANS DOCUMENT ===");
        String reponseSansDocument = model.chat(question);
        System.out.println("Question : " + question);
        System.out.println("Réponse : " + reponseSansDocument);

        // 5. Poser la même question avec le document
        String promptAvecDocument = """
                Tu dois répondre uniquement à partir du document suivant.
                Ne corrige pas les informations du document avec
                tes connaissances personnelles.

                DOCUMENT :
                %s

                QUESTION :
                %s

                Réponds brièvement à la question.
                """.formatted(document, question);

        System.out.println("\n=== AVEC DOCUMENT ===");
        String reponseAvecDocument = model.chat(promptAvecDocument);

        System.out.println("Question : " + question);
        System.out.println("Réponse : " + reponseAvecDocument);

        System.out.println("\n=== FIN DU TEST 5 ===");
    }
}

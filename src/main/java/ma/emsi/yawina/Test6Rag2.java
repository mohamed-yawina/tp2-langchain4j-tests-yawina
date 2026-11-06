
package ma.emsi.yawina;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.service.AiServices;

import java.io.InputStream;

public class Test6Rag2 {

    interface Assistant {
        String chat(String question);
    }

    public static void main(String[] args) throws Exception {

        String apiKey = System.getenv("GEMINI_KEY");

        if (apiKey == null || apiKey.isBlank()) {
            System.err.println("GEMINI_KEY introuvable.");
            return;
        }

        // 1. Charger le PDF
        String fichier = "projet_neurodriveai_tp6.pdf";

        InputStream input = Test6Rag2.class
                .getClassLoader()
                .getResourceAsStream(fichier);

        if (input == null) {
            System.err.println("PDF introuvable : " + fichier);
            return;
        }

        Document document;
        try (input) {
            document = new ApachePdfBoxDocumentParser()
                    .parse(input);
        }

        String contenu = document.text();

        System.out.println("=== PDF CHARGÉ ===");
        System.out.println(contenu);

        // 2. Initialiser Gemini
        GoogleGenAiChatModel model =
                GoogleGenAiChatModel.builder()
                        .apiKey(apiKey)
                        .modelName("gemini-3.5-flash-lite")
                        .temperature(0.0)
                        .build();

        // 3. Assistant avec mémoire conversationnelle
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .chatMemory(
                        MessageWindowChatMemory.withMaxMessages(10)
                )
                .build();

        // 4. Fournir le contenu du PDF
        String question1 = """
                Réponds uniquement avec les informations
                du document suivant.

                DOCUMENT :
                %s

                QUESTION :
                Quel est le nom du projet et quel langage utilise-t-il ?
                """.formatted(contenu);

        System.out.println("\n=== QUESTION 1 ===");
        System.out.println(assistant.chat(question1));

        // 5. Question de suivi : utilise la mémoire
        System.out.println("\n=== QUESTION 2 ===");
        System.out.println(assistant.chat(
                "Quel moteur graphique utilise ce projet ?"
        ));

        // 6. Question de suivi supplémentaire
        System.out.println("\n=== QUESTION 3 ===");
        System.out.println(assistant.chat(
                "Qui est son responsable et quelle est sa version ?"
        ));

        System.out.println("\n=== FIN DU TEST 6 ===");
    }
}

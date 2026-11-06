
package ma.emsi.yawina;

import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.input.Prompt;

import java.util.Map;

public class Test3Traducteur {

    public static void main(String[] args) {

        // 1. Récupérer la clé API
        String apiKey = System.getenv("GEMINI_KEY");

        if (apiKey == null || apiKey.isBlank()) {
            System.err.println("Erreur : GEMINI_KEY introuvable.");
            return;
        }

        // 2. Configurer Gemini
        GoogleGenAiChatModel model = GoogleGenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gemini-3.5-flash-lite")
                .temperature(0.7)
                .build();

        // TEST A : PromptTemplate avec une variable
        System.out.println("=== TEST A : UNE VARIABLE ===");

        PromptTemplate template1 = PromptTemplate.from(
                "Traduis le texte suivant en anglais. " +
                        "Réponds uniquement avec la traduction : {{texte}}"
        );

        Prompt prompt1 = template1.apply(
                Map.of("texte", "Bonjour, je suis étudiant en informatique.")
        );

        String reponse1 = model.chat(prompt1.text());

        System.out.println("Prompt : " + prompt1.text());
        System.out.println("Traduction : " + reponse1);

        // TEST B : PromptTemplate avec deux variables
        System.out.println("\n=== TEST B : DEUX VARIABLES ===");

        PromptTemplate template2 = PromptTemplate.from(
                "Traduis le texte suivant en {{langue}}. " +
                        "Réponds uniquement avec la traduction : {{texte}}"
        );

        Prompt prompt2 = template2.apply(
                Map.of(
                        "langue", "espagnol",
                        "texte", "Je développe une application Java."
                )
        );

        String reponse2 = model.chat(prompt2.text());

        System.out.println("Prompt : " + prompt2.text());
        System.out.println("Traduction : " + reponse2);
    }
}

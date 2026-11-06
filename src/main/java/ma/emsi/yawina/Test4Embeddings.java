
package ma.emsi.yawina;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15.BgeSmallEnV15EmbeddingModel;
import dev.langchain4j.model.google.genai.GoogleGenAiEmbeddingModel;
import dev.langchain4j.store.embedding.CosineSimilarity;

public class Test4Embeddings {

    public static void main(String[] args) {

        String apiKey = System.getenv("GEMINI_KEY");

        if (apiKey == null || apiKey.isBlank()) {
            System.err.println("Erreur : GEMINI_KEY introuvable.");
            return;
        }

        // 1. Initialiser les deux modèles d'embeddings
        EmbeddingModel modeleLocal = new BgeSmallEnV15EmbeddingModel();

        EmbeddingModel modeleGoogle = GoogleGenAiEmbeddingModel.builder()
                .apiKey(apiKey)
                .modelName("gemini-embedding-001")
                .build();

        // 2. Six paires de phrases en anglais
        String[][] paires = {
                {"The cat is sleeping.", "A kitten is resting."},
                {"I love programming in Java.", "I enjoy writing Java code."},
                {"The weather is sunny today.", "It is raining heavily outside."},
                {"Paris is the capital of France.", "France has its capital in Paris."},
                {"The car is very fast.", "The vehicle moves quickly."},
                {"I am learning artificial intelligence.", "The kitchen table is made of wood."}
        };

        System.out.println("=== TEST 4 : EMBEDDINGS ===");
        System.out.println("Modèle local : BGE Small EN v1.5");
        System.out.println("Modèle Google : gemini-embedding-001");

        // 3. Comparer chaque paire
        for (int i = 0; i < paires.length; i++) {

            String phrase1 = paires[i][0];
            String phrase2 = paires[i][1];

            System.out.println("\n--- Paire " + (i + 1) + " ---");
            System.out.println("Phrase 1 : " + phrase1);
            System.out.println("Phrase 2 : " + phrase2);

            // Embeddings avec BGE local
            Embedding local1 = modeleLocal.embed(phrase1).content();
            Embedding local2 = modeleLocal.embed(phrase2).content();

            double similariteLocale =
                    CosineSimilarity.between(local1, local2);

            // Embeddings avec Google
            try {
                Embedding google1 = modeleGoogle.embed(phrase1).content();
                Embedding google2 = modeleGoogle.embed(phrase2).content();

                double similariteGoogle =
                        CosineSimilarity.between(google1, google2);

                System.out.printf("BGE local : %.4f%n", similariteLocale);
                System.out.printf("Google    : %.4f%n", similariteGoogle);

            } catch (Exception e) {
                System.out.printf("BGE local : %.4f%n", similariteLocale);
                System.out.println("Google : erreur - " + e.getMessage());
            }
        }

        System.out.println("\n=== FIN DU TEST 4 ===");
    }
}

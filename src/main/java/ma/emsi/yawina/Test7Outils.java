
package ma.emsi.yawina;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.tool.ToolExecution;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class Test7Outils {

    interface Assistant {
        Result<String> chat(String question);
    }

    public static class OutilsMeteo {

        private final HttpClient client = HttpClient.newHttpClient();
        private final ObjectMapper mapper = new ObjectMapper();

        private String appelerApi(String url) throws Exception {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() != 200) {
                throw new RuntimeException(
                        "Erreur API : " + response.statusCode()
                );
            }

            return response.body();
        }

        @Tool("Rechercher les coordonnées GPS d'une ville. " +
                "À utiliser avant obtenirMeteo.")
        public String rechercherVille(String ville) throws Exception {

            String nom = URLEncoder.encode(
                    ville, StandardCharsets.UTF_8
            );

            String url = "https://geocoding-api.open-meteo.com/v1/search"
                    + "?name=" + nom
                    + "&count=1&language=fr&format=json";

            JsonNode resultat = mapper.readTree(appelerApi(url))
                    .path("results");

            if (!resultat.isArray() || resultat.isEmpty()) {
                return "{\"erreur\":\"Ville introuvable\"}";
            }

            JsonNode villeTrouvee = resultat.get(0);

            return mapper.createObjectNode()
                    .put("ville", villeTrouvee.path("name").asText())
                    .put("latitude", villeTrouvee.path("latitude").asDouble())
                    .put("longitude", villeTrouvee.path("longitude").asDouble())
                    .toString();
        }

        @Tool("Obtenir la météo actuelle à partir de la latitude " +
                "et de la longitude. Utiliser les coordonnées " +
                "retournées par rechercherVille.")
        public String obtenirMeteo(
                double latitude,
                double longitude
        ) throws Exception {

            String url = "https://api.open-meteo.com/v1/forecast"
                    + "?latitude=" + latitude
                    + "&longitude=" + longitude
                    + "&current=temperature_2m,relative_humidity_2m,"
                    + "wind_speed_10m"
                    + "&timezone=auto";

            JsonNode meteo = mapper.readTree(appelerApi(url))
                    .path("current");

            if (meteo.isMissingNode()) {
                return "{\"erreur\":\"Météo indisponible\"}";
            }

            return mapper.createObjectNode()
                    .put("temperature", meteo.path("temperature_2m").asDouble())
                    .put("humidite", meteo.path("relative_humidity_2m").asDouble())
                    .put("vent", meteo.path("wind_speed_10m").asDouble())
                    .put("unite_temperature", "°C")
                    .put("unite_vent", "km/h")
                    .toString();
        }
    }

    public static void main(String[] args) {

        String apiKey = System.getenv("GEMINI_KEY");

        if (apiKey == null || apiKey.isBlank()) {
            System.err.println("GEMINI_KEY introuvable.");
            return;
        }

        GoogleGenAiChatModel model =
                GoogleGenAiChatModel.builder()
                        .apiKey(apiKey)
                        .modelName("gemini-3.5-flash-lite")
                        .temperature(0.0)
                        .build();

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .tools(new OutilsMeteo())
                .build();

        String question = """
                Quelle est la météo actuelle à Casablanca, au Maroc ?
                Utilise obligatoirement rechercherVille pour trouver
                les coordonnées, puis obtenirMeteo.
                Donne la température, l'humidité et le vent.
                """;

        System.out.println("=== TEST 7 : FUNCTION CALLING ===");
        System.out.println("Question : " + question);

        try {
            Result<String> resultat = assistant.chat(question);

            System.out.println("\n=== APPELS DES OUTILS ===");

            for (ToolExecution execution : resultat.toolExecutions()) {
                System.out.println("\nOutil : "
                        + execution.request().name());

                System.out.println("Arguments JSON : "
                        + execution.request().arguments());

                System.out.println("Résultat JSON : "
                        + execution.result());
            }

            System.out.println("\n=== RÉPONSE GEMINI ===");
            System.out.println(resultat.content());

            System.out.println("\n=== FIN DU TEST 7 ===");

        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}

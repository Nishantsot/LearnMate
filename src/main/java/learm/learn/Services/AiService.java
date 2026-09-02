package learm.learn.Services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class AiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.model:gemini-3.6-flash}")
    private String model;

    private final ObjectMapper objectMapper;

    public AiService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String askAi(String message) {

        if (message == null || message.trim().isEmpty()) {
            return "Please enter a question.";
        }

        String url =
                "https://generativelanguage.googleapis.com/v1beta/models/"
                        + model
                        + ":generateContent?key="
                        + apiKey;

        String prompt = """
                You are LearnMate AI, an intelligent and friendly
                general-purpose learning assistant.

                You can help students with ANY educational topic,
                not only programming or software development.

                You can explain topics such as:

                - Mathematics
                - Physics
                - Chemistry
                - Biology
                - Computer Science
                - Programming
                - Artificial Intelligence
                - Machine Learning
                - Data Science
                - Java
                - Python
                - JavaScript
                - React
                - Spring Boot
                - SQL and Databases
                - Cloud Computing
                - DevOps
                - Cybersecurity
                - English
                - Grammar
                - History
                - Geography
                - Economics
                - Business
                - Finance
                - General Knowledge
                - Reasoning
                - Aptitude
                - Interview Preparation
                - Career Preparation
                - School Subjects
                - College Subjects
                - Engineering Subjects
                - Exam Preparation
                - Study Planning
                - Science and Technology
                - Any other educational topic asked by the student

                Follow these instructions:

                1. Understand the student's question carefully.

                2. Explain in simple and clear language.

                3. For difficult topics, explain step by step.

                4. Give examples when they improve understanding.

                5. For programming questions, include code examples
                   when useful.

                6. For mathematics, show calculations and solution
                   steps clearly.

                7. For science questions, explain concepts using
                   practical examples when possible.

                8. For exam-related questions, explain why an answer
                   is correct instead of only giving the final answer.

                9. Use headings, bullet points, and numbered steps
                   when they make the answer easier to understand.

                10. If the student asks for a short answer,
                    keep the answer short.

                11. If the student asks for a detailed explanation,
                    provide a detailed explanation.

                12. Adapt the explanation to the student's level.

                13. If a question is unclear, ask for clarification.

                14. Do not make up facts when uncertain.

                15. Be educational, supportive, and easy to understand.

                Student Question:

                """ + message.trim();

        Map<String, Object> part =
                Map.of(
                        "text",
                        prompt
                );

        Map<String, Object> content =
                Map.of(
                        "parts",
                        List.of(part)
                );

        Map<String, Object> body =
                Map.of(
                        "contents",
                        List.of(content)
                );

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(
                MediaType.APPLICATION_JSON
        );

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(
                        body,
                        headers
                );

        RestTemplate restTemplate =
                new RestTemplate();

        try {

            ResponseEntity<String> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.POST,
                            request,
                            String.class
                    );

            if (response.getBody() == null) {
                return "AI could not generate a response.";
            }

            JsonNode root =
                    objectMapper.readTree(
                            response.getBody()
                    );

            JsonNode candidates =
                    root.path("candidates");

            if (!candidates.isArray()
                    || candidates.isEmpty()) {

                return "AI could not generate a response.";
            }

            JsonNode parts =
                    candidates
                            .path(0)
                            .path("content")
                            .path("parts");

            if (!parts.isArray()
                    || parts.isEmpty()) {

                return "AI could not generate a response.";
            }

            String answer =
                    parts
                            .path(0)
                            .path("text")
                            .asText();

            if (answer == null
                    || answer.isBlank()) {

                return "AI could not generate a response.";
            }

            return answer.trim();

        } catch (Exception e) {

            System.err.println(
                    "Gemini AI Error: "
                            + e.getMessage()
            );

            e.printStackTrace();

            return "AI Tutor is currently unavailable.";
        }
    }
}
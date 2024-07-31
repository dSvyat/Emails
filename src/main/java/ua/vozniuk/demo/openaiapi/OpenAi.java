package ua.vozniuk.demo.openaiapi;

import com.theokanning.openai.service.OpenAiService;

/**
 * Provides a simple interface to interact with the OpenAI GPT-4o API through the OpenAiService.
 *
 * @see OpenAiService
 */
public class OpenAi {
    final OpenAiService service;
    /**
     * Constructs an instance of the OpenAIApi using the provided API token.
     *
     * @param token The API token for authentication with the OpenAI service.
     */
    public OpenAi(String token) {
        service = new OpenAiService(token);
    }
}
package ua.vozniuk.demo;

import com.theokanning.openai.OpenAiResponse;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.chat.*;
import com.theokanning.openai.messages.Message;
import com.theokanning.openai.messages.MessageRequest;
import com.theokanning.openai.runs.Run;
import com.theokanning.openai.runs.RunCreateRequest;
import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.threads.Thread;
import com.theokanning.openai.threads.ThreadRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Provides a simple interface to interact with the OpenAI GPT-3 API through the OpenAiService.
 *
 * @see OpenAiService
 */
public class OpenAIApi{
    final OpenAiService service;

    /**
     * Constructs an instance of the OpenAIApi using the provided API token.
     *
     * @param token The API token for authentication with the OpenAI service.
     */
    public OpenAIApi(String token) {
        service = new OpenAiService(token);
    }
}

/**
 * Represents a specific implementation for interacting with the OpenAI GPT-3 API for text completion.
 * Extends the OpenAIApi class and implements the Requestable interface.
 *
 * @see OpenAIApi
 * @see Requestable
 */
class Completion extends OpenAIApi implements Requestable{
    /**
     * Constructs an instance of the Completion class using the provided API token.
     *
     * @param token The API token for authentication with the OpenAI service.
     */
    public Completion(String token){
        super(token);
    }

    /**
     * Makes a request to the OpenAI GPT-3 API for text completion.
     *
     * @param excelData Data from your excel file in Column:x, Row:y, Value:z format.
     * @param emailText Reply to your job application.
     * @param prompt    Prompt for chatGPT. Recommended to use the default prompt.
     * @return Answer of completion endpoint.
     */
    @Override
    public String makeRequest(String excelData, String emailText, String prompt) {
        String fullPrompt = prompt + "Here is the table data: " + excelData + "\n" + "Here is the email text: " + emailText;
        CompletionRequest completionRequest = CompletionRequest.builder()
                .prompt(fullPrompt)
                .model("gpt-3.5-turbo-instruct")
                .echo(true)
                .maxTokens(500)
                .build();
        return service.createCompletion(completionRequest).getChoices().get(0).getText();
    }
}

/**
 * Represents an implementation of an assistant using the OpenAI GPT-3 API for generating replies.
 * Extends the OpenAIApi class and implements the Requestable interface.
 * @see OpenAIApi
 * @see Requestable
 */
class Assistant extends OpenAIApi implements Requestable{
    /**
     * Constructs an instance of the Assistant class using the provided API token.
     * @param token The API token for authentication with the OpenAI service.
     */
    public Assistant(String token) {
        super(token);
    }

    /**
     * Method to get a reply from the assistant. If desired, you can create and use your own assistant or use the default one.
     *
     * @param excelData   Data from your Excel file in Column:x, Row:y, Value:z format.
     * @param reply       Reply to your job application.
     * @param assistantId Unique id of the personal assistant.
     * @return Answer of the assistant.
     */
    @Override
    public String makeRequest(String excelData, String reply, String assistantId) {
        String prompt = "Here is the table data: " + excelData + "\n" + "Here is the email text: " + reply;
        MessageRequest messageRequest = MessageRequest.builder()
                .content(prompt)
                .build();
        ThreadRequest threadRequest = ThreadRequest.builder()
                .messages(Collections.singletonList(messageRequest))
                .build();
        Thread thread = super.service.createThread(threadRequest);
        RunCreateRequest runCreateRequest = RunCreateRequest.builder()
                .assistantId(assistantId)
                .build();
        Run run = service.createRun(thread.getId(), runCreateRequest);
        Run retrievedRun = service.retrieveRun(thread.getId(), run.getId());
        while (!(retrievedRun.getStatus().equals("completed"))
                && !(retrievedRun.getStatus().equals("failed"))
                && !(retrievedRun.getStatus().equals("requires_action"))) {
            retrievedRun = service.retrieveRun(thread.getId(), run.getId());
        }
        OpenAiResponse<Message> response = service.listMessages(thread.getId());
        List<Message> messages = response.getData();
        return messages.get(0).getContent().get(0).getText().getValue();
    }
}

/**
 * Represents an implementation of a chat-based interaction with the OpenAI GPT-3 API for generating replies.
 * Extends the OpenAIApi class and implements the Requestable interface.
 *
 * @see OpenAIApi
 * @see Requestable
 */
class ChatGPT extends OpenAIApi implements Requestable{
    public ChatGPT(String token){
        super(token);
    }
    /**
     * Constructs an instance of the ChatGPT class using the provided API token.
     *
     * @param token The API token for authentication with the OpenAI service.
     */
    private static List<ChatMessage> messages = new ArrayList<>();

    /**
     * Makes a request to the OpenAI GPT-3 API for generating a response in a chat-based model.
     *
     * @param excelData   Data from your Excel file in Column:x, Row:y, Value:z format.
     * @param emailText   Reply to your job application.
     * @param prompt      Prompt for ChatGPT. Recommended to use the default prompt.
     * @return            The generated response from the chat-based model.
     */
    @Override
    public String makeRequest(String excelData, String emailText, String prompt) {
        String fullPrompt = prompt + "Here is the table data: " + excelData + "\n" + "Here is the email text: " + emailText;
        ChatMessage chatMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), fullPrompt);
        messages.add(chatMessage);
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                .builder()
                .model("gpt-3.5-turbo")
                .messages(messages)
                .n(1)
                .maxTokens(500)
                .logitBias(new HashMap<>())
                .build();
        ChatMessage responseMessage = service.createChatCompletion(chatCompletionRequest).getChoices().get(0).getMessage();
        messages.clear();
        return responseMessage.getContent();
    }
}

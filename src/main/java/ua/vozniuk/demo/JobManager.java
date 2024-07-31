package ua.vozniuk.demo;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.vozniuk.demo.googleapi.GmailMessages;
import ua.vozniuk.demo.googleapi.GoogleDrive;
import ua.vozniuk.demo.openaiapi.Completion;
import ua.vozniuk.demo.openaiapi.*;
import ua.vozniuk.demo.openaiapi.Requestable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JobManager {
    private long chatId;
    private String filepath;
    private String openAiToken;
    private String assistantPrompt;
    private String telegramToken;
    private String email;

    public JobManager(JobManagerBuilder builder){
        this.chatId = builder.getChatId();
        this.filepath = builder.getFilepath();
        this.openAiToken = builder.getOpenAiToken();
        this.assistantPrompt = builder.getAssistantPrompt();
        this.telegramToken = builder.getTelegramToken();
        this.email = builder.getEmail();
    }

    public static JobManagerBuilder builder(){
        return new JobManagerBuilder();
    }

    /**
     * Executes a continuous loop to monitor and process updates from Gmail messages using the specified Requestable endpoint.
     * The method reads messages from Gmail, makes requests to the given Requestable instance, and handles the responses accordingly.
     * Additionally, if the response requires additional information, sends a message via Telegram using the provided TelegramBot instance.
     *
     * @param openAiEndpoint An object implementing the Requestable interface, representing the endpoint for making requests.
     *                       It can be an instance of the Assistant or any other class that extends Requestable.
     * @param timeout        The time, in seconds, to sleep between iterations of the loop.
     * @param <T>            The generic type parameter that extends the Requestable interface.
     * @throws IOException                If an I/O exception occurs while reading Gmail messages or processing Excel data.
     * @throws GeneralSecurityException   If a security-related exception occurs during execution.
     * @throws InterruptedException        If the execution is interrupted while sleeping.
     *
     * @see Requestable
     * @see ua.vozniuk.demo.openaiapi.OpenAi
     * @see TelegramBot
     */
    public <T extends Requestable> void run(T openAiEndpoint, int timeout) throws IOException, GeneralSecurityException, InterruptedException {
        Excel excel = new Excel(filepath);
        while (true) {
            if (GmailMessages.ReadMessages.checkForUpdates()) {
                String answer = openAiEndpoint.makeRequest(excel.getExcelData(), GmailMessages.ReadMessages.readLastMessage(), assistantPrompt);
                System.out.println(answer);
                if (checkAnswer(answer)) {
                    GoogleDrive.downloadFile(GoogleDrive.getFileByName(filepath));
                    excel.changeSheet(answer);
                    if (excel.checkForAdditionalInfo(answer)) {
                        try {
                            TelegramBot telegramBot = new TelegramBot(telegramToken);
                            telegramBot.register().sendMessage(chatId, excel.getAdditionalInfo(answer));
                        } catch (TelegramApiException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
            TimeUnit.SECONDS.sleep(timeout);
        }
    }

    /**
     * Executes a continuous loop to monitor and process updates from Gmail messages using the specified Requestable endpoint.
     * The method reads messages from Gmail and makes requests to the given Requestable instance, handling the responses accordingly.
     *
     * @param openAiEndpoint An object implementing the {@code Requestable} interface, representing the endpoint for making requests.
     *                       It can be an instance of the Assistant or any other class that extends Requestable.
     * @param <T>            The generic type parameter that extends the Requestable interface.
     * @throws IOException                If an I/O exception occurs while reading Gmail messages or processing Excel data.
     * @throws GeneralSecurityException   If a security-related exception occurs during execution.
     * @throws InterruptedException        If the execution is interrupted while sleeping.
     *
     * @see ua.vozniuk.demo.openaiapi.OpenAi
     * @see Assistant
     */
    public <T extends Requestable> void runWithoutBot(T openAiEndpoint) throws IOException, GeneralSecurityException, InterruptedException {
        Excel excel = new Excel(filepath);
        while (true) {
            if (GmailMessages.ReadMessages.checkForUpdates()) {
                String answer = openAiEndpoint.makeRequest(excel.getExcelData(), GmailMessages.ReadMessages.readLastMessage(), assistantPrompt);
                System.out.println(answer);
                if (checkAnswer(answer)) {
                    excel.changeSheet(answer);
                }
            }
            TimeUnit.SECONDS.sleep(10);
        }
    }

    /**
     * Checks and processes updates for the specified email using the provided Requestable endpoint.
     * Reads data from an Excel file, makes a request to the given Requestable instance, and handles the response accordingly.
     *
     * @param openAiEndpoint An object implementing the Requestable interface, representing the endpoint for making requests.
     *                       It can be an instance of the Assistant or any other class that extends Requestable.
     * @param <T>            The generic type parameter that extends the Requestable interface.
     * @return               A string containing the response received from the Requestable endpoint.
     * @throws IOException  If an I/O exception occurs while reading Excel data or making the request.
     *
     * @see ua.vozniuk.demo.openaiapi.OpenAi
     * @see Assistant
     */
    public <T extends Requestable> String checkWithoutBotWithEmail(T openAiEndpoint) throws IOException {
        Excel excel = new Excel(filepath);
        String answer = openAiEndpoint.makeRequest(excel.getExcelData(), email, assistantPrompt);
        if (checkAnswer(answer)) {
            excel.changeSheet(answer);
        }
        return answer;
    }
    /**
     * Checks and processes updates for the specified email using the provided Requestable endpoint.
     * Reads data from an Excel file, makes a request to the given Requestable instance, and handles the response accordingly.
     * If the response requires additional information, sends a message via Telegram using the provided TelegramBot instance.
     *
     * @param openAiEndpoint An object implementing the Requestable interface, representing the endpoint for making requests.
     *                       It can be an instance of the Assistant or any other class that extends Requestable.
     * @param <T>            The generic type parameter that extends the Requestable interface.
     * @throws FileNotFoundException   If the Excel file specified by the filepath is not found.
     * @throws TelegramApiException     If an exception occurs while interacting with the Telegram API.
     *
     * @see ua.vozniuk.demo.openaiapi.OpenAi
     * @see Assistant
     * @see TelegramBot
     */
    public  <T extends Requestable> void checkWithEmail(T openAiEndpoint) throws FileNotFoundException, TelegramApiException {
        Excel excel = new Excel(filepath);
        String answer = openAiEndpoint.makeRequest(excel.getExcelData(), email, assistantPrompt);
        if (checkAnswer(answer)) {
            excel.changeSheet(answer);
            if (excel.checkForAdditionalInfo(answer)) {
                TelegramBot telegramBot = new TelegramBot(telegramToken);
                telegramBot.register();
                telegramBot.sendMessage(chatId, excel.getAdditionalInfo(answer));
            }
        }
    }


    /**
     * Checks whether the provided answer is positive (contains the substring "YES"). Case-sensitive.
     *
     * @param answer A string representing the answer to be checked.
     * @return       {@code true} if the answer contains the substring "YES," {@code false} otherwise.
     */
    public static boolean checkAnswer(String answer) {
        Pattern pattern = Pattern.compile("YES");
        Matcher matcher = pattern.matcher(answer);
        return matcher.find();
    }

    public Assistant getAssistant(){
        return new Assistant(openAiToken);
    }

    public ChatGPT getChatGPT(){
        return new ChatGPT(openAiToken);
    }

    public Completion getCompletion(){
        return new Completion(openAiToken);
    }

    public long getChatId() {
        return chatId;
    }

    public String getFilepath() {
        return filepath;
    }

    public String getOpenAiToken() {
        return openAiToken;
    }

    public String getAssistantPrompt() {
        return assistantPrompt;
    }

    public String getTelegramToken() {
        return telegramToken;
    }

    public String getEmail() {
        return email;
    }

}



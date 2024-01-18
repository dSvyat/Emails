package ua.vozniuk.demo;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.FileNotFoundException;
import java.util.ResourceBundle;

@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) throws TelegramApiException, FileNotFoundException {
        ResourceBundle rb = ResourceBundle.getBundle("application");
        Constructor constructor = Constructor.builder()
                .setEmail(rb.getString("email"))
                .setAssistantPrompt(rb.getString("assistantId"))
                .setFilepath("D:\\Work\\work.xlsx")
                .setChatId(384164240)
                .setTelegramToken("telegramToken")
                .setOpenAiToken(rb.getString("openAiToken"))
                .build();
        constructor.checkWithEmail(constructor.getAssistant());
    }
}

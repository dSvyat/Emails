package ua.vozniuk.demo;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.vozniuk.demo.googleapi.GoogleDrive;
import ua.vozniuk.demo.openaiapi.Assistant;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.ResourceBundle;

@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) throws TelegramApiException, IOException, GeneralSecurityException, InterruptedException {

        ResourceBundle rb = ResourceBundle.getBundle("application");
        JobManager manager = JobManager.builder()
                .setEmail(rb.getString("email"))
                .setAssistantPrompt(rb.getString("assistantId"))
                .setFilepath("GoogleDriveSheet.xlsx")
                .setChatId(384164240)
                .setTelegramToken("telegramToken")
                .setOpenAiToken(rb.getString("openAiToken"))
                .build();
        Assistant assistant = new Assistant(rb.getString("openAiToken"));
        manager.checkWithEmail(manager.getAssistant());
        manager.run(assistant, 10);
        GoogleDrive.listFiles();
        FileOutputStream fileOutputStream = new FileOutputStream(new File("test1.txt"));

        GoogleDrive.downloadFile(GoogleDrive.uploadFile("GoogleDriveSheet.xlsx", "GoogleDriveSheet"));
    }
}

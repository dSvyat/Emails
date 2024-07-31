package ua.vozniuk.demo.openaiapi;

public interface Requestable {
    String makeRequest(String excelData, String emailText, String prompt);
}

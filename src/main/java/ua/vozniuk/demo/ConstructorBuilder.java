package ua.vozniuk.demo;

public class ConstructorBuilder {
    private long chatId;
    private String filepath;
    private String openAiToken;
    private String assistantPrompt;
    private String telegramToken;
    private String email;

    public long getChatId() {
        return chatId;
    }

    public ConstructorBuilder setChatId(long chatId) {
        this.chatId = chatId;
        return this;
    }

    public String getFilepath() {
        return filepath;
    }

    public ConstructorBuilder setFilepath(String filepath) {
        this.filepath = filepath;
        return this;
    }

    public String getOpenAiToken() {
        return openAiToken;
    }

    public ConstructorBuilder setOpenAiToken(String openAiToken) {
        this.openAiToken = openAiToken;
        return this;
    }

    public String getAssistantPrompt() {
        return assistantPrompt;
    }

    public ConstructorBuilder setAssistantPrompt(String assistantPrompt) {
        this.assistantPrompt = assistantPrompt;
        return this;
    }

    public String getTelegramToken() {
        return telegramToken;
    }

    public ConstructorBuilder setTelegramToken(String telegramToken) {
        this.telegramToken = telegramToken;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public ConstructorBuilder setEmail(String email) {
        this.email = email;
        return this;
    }

    public Constructor build() {
        return new Constructor(this);
    }
}

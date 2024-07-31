package ua.vozniuk.demo;

class JobManagerBuilder {
    private long chatId;
    private String filepath;
    private String openAiToken;
    private String assistantPrompt;
    private String telegramToken;
    private String email;

    public long getChatId() {
        return chatId;
    }

    public JobManagerBuilder setChatId(long chatId) {
        this.chatId = chatId;
        return this;
    }

    public String getFilepath() {
        return filepath;
    }

    public JobManagerBuilder setFilepath(String filepath) {
        this.filepath = filepath;
        return this;
    }

    public String getOpenAiToken() {
        return openAiToken;
    }

    public JobManagerBuilder setOpenAiToken(String openAiToken) {
        this.openAiToken = openAiToken;
        return this;
    }

    public String getAssistantPrompt() {
        return assistantPrompt;
    }

    public JobManagerBuilder setAssistantPrompt(String assistantPrompt) {
        this.assistantPrompt = assistantPrompt;
        return this;
    }

    public String getTelegramToken() {
        return telegramToken;
    }

    public JobManagerBuilder setTelegramToken(String telegramToken) {
        this.telegramToken = telegramToken;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public JobManagerBuilder setEmail(String email) {
        this.email = email;
        return this;
    }

    public JobManager build() {
        return new JobManager(this);
    }
}

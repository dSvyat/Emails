package ua.vozniuk.demo;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * Represents a Telegram bot that extends the TelegramLongPollingBot class.
 *
 * @see TelegramLongPollingBot
 */
public class TelegramBot extends TelegramLongPollingBot {
    private TelegramBotsApi telegramBotsApi;
    private final String token;

    /**
     * Constructs an instance of the TelegramBot using the provided Telegram API token.
     *
     * @param token The Telegram API token for authenticating the bot with the Telegram service.
     * @throws TelegramApiException If an exception occurs during the initialization of the TelegramBotsApi.
     */
    public TelegramBot(String token) throws TelegramApiException{
        telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        this.token = token;
    }

    /**
     * Registers the bot with the Telegram Bots API.
     *
     * @throws TelegramApiException If an exception occurs while registering the bot.
     */
    public TelegramBot register() throws TelegramApiException{
        telegramBotsApi.registerBot(new TelegramBot(token));
        return this;
    }

    /**
     * Sends a text message to the specified chat ID.
     * To get your own chat ID, use {@link #onUpdateReceived(Update) onUpdateReceived(Update update)}
     * and simply text your bot anything; your unique chat ID will be printed in the console.
     *
     * @param chatId Your own chat ID.
     * @param text   Text to be sent.
     * @return       {@code true} if the text was sent successfully, {@code false} otherwise.
     */
    public boolean sendMessage(long chatId, String text){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(text);
        sendMessage.setChatId(chatId);
        try{
            execute(sendMessage);
            return true;
        } catch (TelegramApiException e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println(update.getMessage().getFrom().getId());
    }

    @Override
    public String getBotUsername() {
        return "PersonalBot";
    }

    @Override
    public String getBotToken() {
        return "6333127453:AAH1YVI26hBsBFmljHrJU_7nHQslUurDAR8";
    }
}

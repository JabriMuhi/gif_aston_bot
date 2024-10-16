package org.jabrimuhi.bot;

import internal.DatabaseManager;
import org.jabrimuhi.api.GiphyAPI;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

public class GifBot extends TelegramLongPollingBot {
    private static final String BOT_USERNAME = "gif_aston_bot";
    private static final String BOT_TOKEN = "8151829511:AAEQpsQHrm5srdzYu5kE1Xwnx8N9Cf0rAeA";
    private final DatabaseManager databaseManager;

    public GifBot(DatabaseManager databaseManager) {
        super(BOT_TOKEN);
        this.databaseManager = databaseManager;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();

            switch (message) {
                case "/start":
                    try {
                        sendMessage(chatId, "Введите ключевое слово для поиска гифки...");
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "/history":
                    try {
                        List<String> requestHistory = databaseManager.getRequestHistory(Long.parseLong(chatId));
                        if (requestHistory.isEmpty()) {
                            sendMessage(chatId, "У вас пока нет истории запросов.");
                        } else {
                            StringBuilder historyMessage = new StringBuilder("Ваша история запросов:\n");
                            for (String request : requestHistory) {
                                historyMessage.append(request).append("\n");
                            }
                            sendMessage(chatId, historyMessage.toString());
                        }
                    } catch (Exception e) {
                        try {
                            sendMessage(chatId, "Error: " + e.getMessage());
                        } catch (TelegramApiException ex) {
                            throw new RuntimeException(e);
                        }
                    }
                    break;
                default:
                    try {
                        long userId = Long.parseLong(chatId);
                        String searchQuery = message;
                        String response = GiphyAPI.searchGifs(searchQuery);
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        if (!jsonArray.isEmpty()) {
                            JSONObject gif = jsonArray.getJSONObject(0);
                            String gifUrl = gif.getJSONObject("images").getJSONObject("original").getString("url");
                            databaseManager.saveRequest(userId, searchQuery, gifUrl);
                            sendMessage(chatId, gifUrl);
                        } else {
                            sendMessage(chatId, "Гифок по такому запросу нет :(");
                        }
                    } catch (Exception e) {
                        try {
                            sendMessage(chatId, "Error: " + e.getMessage());
                        } catch (TelegramApiException ex) {
                            throw new RuntimeException(e);
                        }
                    }
                    break;
            }

        }
    }

    public void sendMessage(String chatId, String text) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        execute(message);
    }

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public void onRegister() {
        super.onRegister();
    }
}

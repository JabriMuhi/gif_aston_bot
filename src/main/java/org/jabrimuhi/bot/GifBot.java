package org.jabrimuhi.bot;

import org.jabrimuhi.api.GiphyAPI;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class GifBot extends TelegramLongPollingBot {
    private static final String BOT_TOKEN = "8151829511:AAEQpsQHrm5srdzYu5kE1Xwnx8N9Cf0rAeA";

    public GifBot() {
        super(BOT_TOKEN);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();

            if (message.equals("/start")){
                try {
                    sendMessage(chatId, "Введите ключевое слово для поиска гифки...");
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            } else {

                try {
                    String response = GiphyAPI.searchGifs(message);
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    if (!jsonArray.isEmpty()) {
                        JSONObject gif = jsonArray.getJSONObject(0);
                        String gifUrl = gif.getJSONObject("images").getJSONObject("original").getString("url");
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
        return "gif_aston_bot";
    }

    @Override
    public void onRegister() {
        super.onRegister();
    }
}

package org.jabrimuhi;

import internal.DatabaseManager;
import org.jabrimuhi.bot.GifBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.sql.*;

public class Main {
    private static final String URL = "jdbc:postgresql://localhost:5432/aston_gif_bot";
    private static final String USER = "postgres";
    private static final String PASSWORD = "a6d3fdd4";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) throws TelegramApiException, SQLException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        Connection connection = getConnection();
        DatabaseManager databaseManager = new DatabaseManager(connection);
        telegramBotsApi.registerBot(new GifBot(databaseManager));

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM example");
            while (resultSet.next()) {
                System.out.println(resultSet.getString("first_name"));
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
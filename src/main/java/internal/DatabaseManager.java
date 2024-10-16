package internal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private Connection connection;

    public DatabaseManager(Connection connection) {
        this.connection = connection;
    }

    public void saveUser(long userId) throws SQLException {
        String sql = "INSERT INTO users (user_id) VALUES (?) ON CONFLICT DO NOTHING";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            statement.executeUpdate();
        }
    }

    public void saveRequest(long userId, String searchQuery, String gifUrl) throws SQLException {
        String sql = "INSERT INTO requests (user_id, search_query, gif_url) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            statement.setString(2, searchQuery);
            statement.setString(3, gifUrl);
            statement.executeUpdate();
        }
    }

    public List<String> getRequestHistory(long userId) throws SQLException {
        List<String> requestHistory = new ArrayList<>();
        String sql = "SELECT search_query, gif_url FROM requests WHERE user_id = ? ORDER BY created_at DESC";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String searchQuery = resultSet.getString("search_query");
                    String gifUrl = resultSet.getString("gif_url");
                    requestHistory.add(searchQuery + " - " + gifUrl);
                }
            }
        }
        return requestHistory;
    }
}

package br.com.dio.persistence.config;

import lombok.NoArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class ConnectionConfig {

    public static Connection getConnection() throws SQLException {
        var url = System.getenv("DB_URL") != null ? System.getenv("DB_URL") : "jdbc:mysql://localhost/board";
        var user = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "root";
        var password = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "818283";
        var connection = DriverManager.getConnection(url, user, password);
        connection.setAutoCommit(false);
        return connection;
    }
}
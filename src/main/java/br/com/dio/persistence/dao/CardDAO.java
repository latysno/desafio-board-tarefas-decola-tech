package br.com.dio.persistence.dao;

import br.com.dio.dto.CardDetailsDTO;
import br.com.dio.persistence.entity.BoardColumnEntity;
import br.com.dio.persistence.entity.CardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@AllArgsConstructor
public class CardDAO {

    private final Connection connection;

    public Optional<CardEntity> findEntityById(final Long id) throws SQLException {
        final String sql = "SELECT id, title, description, board_column_id FROM CARDS WHERE id = ?;";
        try (final PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (final ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    final CardEntity entity = new CardEntity();
                    entity.setId(resultSet.getLong("id"));
                    entity.setTitle(resultSet.getString("title"));
                    entity.setDescription(resultSet.getString("description"));

                    final BoardColumnEntity boardColumn = new BoardColumnEntity();
                    boardColumn.setId(resultSet.getLong("board_column_id"));
                    entity.setBoardColumn(boardColumn);

                    return Optional.of(entity);
                }
                return Optional.empty();
            }
        }
    }

    public void update(final CardEntity entity) throws SQLException {
        final String sql = "UPDATE CARDS SET title = ?, description = ? WHERE id = ?;";
        try (final PreparedStatement statement = connection.prepareStatement(sql)) {
            int i = 1;
            statement.setString(i++, entity.getTitle());
            statement.setString(i++, entity.getDescription());
            statement.setLong(i, entity.getId());
            statement.executeUpdate();
        }
    }

    public Optional<CardDetailsDTO> findById(final Long id) throws SQLException {
        final String sql = "SELECT id, title, description FROM CARDS WHERE id = ?";
        try (final PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (final ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new CardDetailsDTO(
                            rs.getLong("id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            false, // ou outro valor booleano correto
                            null, // OffsetDateTime, caso necessário buscar do banco
                            null, // String para outro campo do DTO
                            0,    // int para outro campo do DTO
                            null, // Long para outro campo do DTO
                            null  // String para outro campo do DTO
                    ));
                }
                return Optional.empty();
            }
        }
    }

    public void insert(CardEntity entity) throws SQLException {
        final String sql = "INSERT INTO CARDS (title, description, board_column_id) VALUES (?, ?, ?);";
        try (final PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, entity.getTitle());
            statement.setString(2, entity.getDescription());
            statement.setLong(3, entity.getBoardColumn().getId());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getLong(1)); // Define o ID gerado para o card
                }
            }
        }
    }


}

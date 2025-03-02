package br.com.dio.persistence.dao;

import br.com.dio.persistence.entity.BoardEntity;
import com.mysql.cj.jdbc.StatementImpl;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@AllArgsConstructor
public class BoardDAO {

    private static final Logger logger = LoggerFactory.getLogger(BoardDAO.class);
    private final Connection connection;

    public BoardEntity insert(final BoardEntity entity) throws SQLException {
        var sql = "INSERT INTO BOARDS (name) VALUES (?);";
        try (var statement = connection.prepareStatement(sql)) {
            statement.setString(1, entity.getName());
            statement.executeUpdate();
            if (statement instanceof StatementImpl impl) {
                entity.setId(impl.getLastInsertID()); // Corrigido de setld para setId
            }
            logger.debug("Quadro inserido no banco: ID {}", entity.getId()); // Corrigido de getld para getId
            return entity;
        }
    }

    public void delete(final Long id) throws SQLException {
        var sql = "DELETE FROM BOARDS WHERE id = ?;";
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            int rowsAffected = statement.executeUpdate();
            logger.debug("Quadro deletado, linhas afetadas: {}", rowsAffected);
        }
    }

    public Optional<BoardEntity> findById(final Long id) throws SQLException {
        var sql = "SELECT id, name FROM BOARDS WHERE id = ? LIMIT 1;";
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                var entity = new BoardEntity();
                entity.setId(resultSet.getLong("id")); // Corrigido de setld para setId
                entity.setName(resultSet.getString("name")); // setName existe na entidade
                logger.debug("Quadro encontrado: ID {}", id);
                return Optional.of(entity);
            }
            logger.debug("Nenhum quadro encontrado com ID: {}", id);
            return Optional.empty();
        }
    }

    public boolean exists(final Long id) throws SQLException {
        var sql = "SELECT EXISTS(SELECT 1 FROM BOARDS WHERE id = ?) AS `exists`;";
        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            var resultSet = statement.executeQuery();
            return resultSet.next() && resultSet.getBoolean("exists");
        }
    }
}
package br.com.dio.service;

import br.com.dio.persistence.dao.BoardColumnDAO;
import br.com.dio.persistence.dao.BoardDAO;
import br.com.dio.persistence.entity.BoardEntity;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

@AllArgsConstructor
public class BoardService {

    private static final Logger logger = LoggerFactory.getLogger(BoardService.class);
    private final Connection connection;

    public BoardEntity insert(final BoardEntity entity) throws SQLException {
        // Validação de dados
        if (entity.getName() == null || entity.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do quadro não pode estar vazio.");
        }
        if (entity.getBoardColumns() == null || entity.getBoardColumns().isEmpty()) {
            throw new IllegalArgumentException("Um quadro deve ter pelo menos uma coluna.");
        }

        var dao = new BoardDAO(connection); // DAO inicializado com conexão
        var boardColumnDAO = new BoardColumnDAO(connection); // DAO inicializado com conexão
        try {
            logger.info("Iniciando inserção do quadro: {}", entity.getName());
            dao.insert(entity);
            var columns = entity.getBoardColumns().stream()
                    .map(c -> {
                        c.setBoard(entity);
                        return c;
                    })
                    .toList();
            for (var column : columns) {
                boardColumnDAO.insert(column);
            }
            connection.commit();
            logger.info("Quadro inserido com sucesso: ID {}", entity.getId());
            return entity;
        } catch (SQLException e) {
            connection.rollback();
            logger.error("Erro ao inserir quadro: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao inserir quadro: " + e.getMessage(), e);
        }
    }

    public boolean delete(final Long id) throws SQLException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido para exclusão.");
        }

        var dao = new BoardDAO(connection); // DAO inicializado com conexão
        try {
            logger.info("Verificando existência do quadro com ID: {}", id);
            if (!dao.exists(id)) {
                logger.warn("Quadro com ID {} não encontrado para exclusão.", id);
                return false;
            }
            logger.info("Deletando quadro com ID: {}", id);
            dao.delete(id);
            connection.commit();
            logger.info("Quadro com ID {} deletado com sucesso.", id);
            return true;
        } catch (SQLException e) {
            connection.rollback();
            logger.error("Erro ao deletar quadro com ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Erro ao deletar quadro: " + e.getMessage(), e);
        }
    }
}
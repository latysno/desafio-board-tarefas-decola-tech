package br.com.dio.service;

import br.com.dio.dto.BoardColumnInfoDTO;
import br.com.dio.exception.EntityNotFoundException;
import br.com.dio.persistence.dao.CardDAO;
import br.com.dio.persistence.entity.CardEntity;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@AllArgsConstructor
public class CardService {

    private static final Logger logger = LoggerFactory.getLogger(CardService.class);
    private final Connection connection;

    public void create(CardEntity card) throws SQLException {
        if (card == null || card.getTitle() == null || card.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("O título do cartão não pode estar vazio.");
        }

        try {
            var dao = new CardDAO(connection);
            dao.insert(card);
            connection.commit();
            logger.info("Cartão criado com sucesso: {}", card.getTitle());
        } catch (SQLException ex) {
            connection.rollback();
            logger.error("Erro ao criar cartão: {}", ex.getMessage(), ex);
            throw new RuntimeException("Erro ao criar cartão: " + ex.getMessage(), ex);
        }
    }

    public void block(long cardId, String reason, List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        if (cardId <= 0) {
            throw new IllegalArgumentException("ID do cartão inválido.");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("O motivo do bloqueio não pode estar vazio.");
        }

        try {
            var dao = new CardDAO(connection);
            var card = dao.findEntityById(cardId)
                    .orElseThrow(() -> new EntityNotFoundException("O cartão de ID %s não foi encontrado".formatted(cardId)));

            card.setBlocked(true);
            card.setBlockReason(reason);
            dao.update(card);
            connection.commit();

            logger.info("Cartão ID {} bloqueado com sucesso. Motivo: {}", cardId, reason);
        } catch (SQLException ex) {
            connection.rollback();
            logger.error("Erro ao bloquear cartão ID {}: {}", cardId, ex.getMessage(), ex);
            throw new RuntimeException("Erro ao bloquear cartão: " + ex.getMessage(), ex);
        }
    }

    public void unblock(long cardId, String reason) throws SQLException {
        if (cardId <= 0) {
            throw new IllegalArgumentException("ID do cartão inválido.");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("O motivo do desbloqueio não pode estar vazio.");
        }

        try {
            var dao = new CardDAO(connection);
            var card = dao.findEntityById(cardId)
                    .orElseThrow(() -> new EntityNotFoundException("O cartão de ID %s não foi encontrado".formatted(cardId)));

            if (!card.isBlocked()) {
                throw new RuntimeException("O cartão ID " + cardId + " não está bloqueado.");
            }

            card.setBlocked(false);
            card.setBlockReason(null);
            dao.update(card);
            connection.commit();

            logger.info("Cartão ID {} desbloqueado com sucesso. Motivo: {}", cardId, reason);
        } catch (SQLException ex) {
            connection.rollback();
            logger.error("Erro ao desbloquear cartão ID {}: {}", cardId, ex.getMessage(), ex);
            throw new RuntimeException("Erro ao desbloquear cartão: " + ex.getMessage(), ex);
        }
    }

    public void moveToNextColumn(long cardId, List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        if (cardId <= 0) {
            throw new IllegalArgumentException("ID do cartão inválido.");
        }

        var dao = new CardDAO(connection);
        var card = dao.findEntityById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("O cartão de ID %s não foi encontrado".formatted(cardId)));

        var currentColumnId = card.getBoardColumn().getId();

        boardColumnsInfo.sort((a, b) -> Integer.compare(a.order(), b.order()));
        var currentIndex = -1;

        for (int i = 0; i < boardColumnsInfo.size(); i++) {
            if (boardColumnsInfo.get(i).id().equals(currentColumnId)) {
                currentIndex = i;
                break;
            }
        }

        if (currentIndex == -1 || currentIndex == boardColumnsInfo.size() - 1) {
            throw new RuntimeException("O cartão já está na última coluna ou a coluna atual não foi encontrada.");
        }

        var nextColumnId = boardColumnsInfo.get(currentIndex + 1).id();
        card.getBoardColumn().setId(nextColumnId);

        dao.update(card);
        connection.commit();

        logger.info("Cartão ID {} movido para a próxima coluna de ID {}", cardId, nextColumnId);
    }

    public void cancel(long cardId, Long cancelColumnId, List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        if (cardId <= 0) {
            throw new IllegalArgumentException("ID do cartão inválido.");
        }
        if (cancelColumnId == null) {
            throw new IllegalArgumentException("ID da coluna de cancelamento não pode ser nulo.");
        }

        try {
            var dao = new CardDAO(connection);
            var card = dao.findEntityById(cardId)
                    .orElseThrow(() -> new EntityNotFoundException("O cartão de ID %s não foi encontrado".formatted(cardId)));

            boolean columnExists = boardColumnsInfo.stream()
                    .anyMatch(column -> column.id().equals(cancelColumnId));

            if (!columnExists) {
                throw new RuntimeException("A coluna de cancelamento ID " + cancelColumnId + " não foi encontrada.");
            }

            card.getBoardColumn().setId(cancelColumnId);
            dao.update(card);
            connection.commit();

            logger.info("Cartão ID {} movido para a coluna de cancelamento ID {}", cardId, cancelColumnId);
        } catch (SQLException ex) {
            connection.rollback();
            logger.error("Erro ao cancelar cartão ID {}: {}", cardId, ex.getMessage(), ex);
            throw new RuntimeException("Erro ao cancelar cartão: " + ex.getMessage(), ex);
        }
    }
}

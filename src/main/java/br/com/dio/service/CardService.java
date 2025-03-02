package br.com.dio.service;

import br.com.dio.dto.BoardColumnInfoDTO;
import br.com.dio.exception.CardBlockedException;
import br.com.dio.exception.CardFinishedException;
import br.com.dio.exception.EntityNotFoundException;
import br.com.dio.persistence.dao.BlockDAO;
import br.com.dio.persistence.dao.CardDAO;
import br.com.dio.persistence.entity.CardEntity;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static br.com.dio.persistence.entity.BoardColumnKindEnum.CANCEL;
import static br.com.dio.persistence.entity.BoardColumnKindEnum.FINAL;

@AllArgsConstructor
public class CardService {

    private static final Logger logger = LoggerFactory.getLogger(CardService.class);
    private final Connection connection;

    public CardEntity create(final CardEntity entity) throws SQLException {
        if (entity.getTitle() == null || entity.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("O título do cartão não pode estar vazio.");
        }
        if (entity.getBoardColumn() == null || entity.getBoardColumn().getId() == null) {
            throw new IllegalArgumentException("A coluna do cartão deve ser especificada.");
        }

        try {
            var dao = new CardDAO(connection); // DAO inicializado com conexão
            logger.info("Criando cartão: {}", entity.getTitle());
            dao.insert(entity);
            connection.commit();
            logger.info("Cartão criado com sucesso: ID {}", entity.getId()); // Corrigido de getld para getId
            return entity;
        } catch (SQLException ex) {
            connection.rollback();
            logger.error("Erro ao criar cartão: {}", ex.getMessage(), ex);
            throw new RuntimeException("Erro ao criar cartão: " + ex.getMessage(), ex);
        }
    }

    public void moveToNextColumn(final Long cardId, final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        if (cardId == null || cardId <= 0) {
            throw new IllegalArgumentException("ID do cartão inválido.");
        }
        if (boardColumnsInfo == null || boardColumnsInfo.isEmpty()) {
            throw new IllegalArgumentException("Lista de colunas não pode estar vazia.");
        }

        try {
            var dao = new CardDAO(connection); // DAO inicializado com conexão
            logger.info("Buscando cartão com ID: {}", cardId);
            var optional = dao.findById(cardId);
            var dto = optional.orElseThrow(() -> {
                logger.warn("Cartão com ID {} não encontrado.", cardId);
                return new EntityNotFoundException("O cartão de ID %s não foi encontrado".formatted(cardId));
            });

            if (dto.blocked()) {
                logger.warn("Tentativa de mover cartão bloqueado: ID {}", cardId);
                throw new CardBlockedException("O cartão %s está bloqueado, é necessário desbloqueá-lo para mover".formatted(cardId));
            }

            var currentColumn = findCurrentColumn(dto.columnId(), boardColumnsInfo);
            if (currentColumn.kind().equals(FINAL)) {
                logger.warn("Cartão com ID {} já está finalizado.", cardId);
                throw new CardFinishedException("O cartão já foi finalizado");
            }

            var nextColumn = findNextColumn(currentColumn, boardColumnsInfo);
            logger.info("Movendo cartão ID {} da coluna {} para a coluna {}", cardId, currentColumn.id(), nextColumn.id());
            dao.moveToColumn(nextColumn.id(), cardId);
            connection.commit();
            logger.info("Cartão ID {} movido com sucesso.", cardId);
        } catch (SQLException ex) {
            connection.rollback();
            logger.error("Erro ao mover cartão ID {}: {}", cardId, ex.getMessage(), ex);
            throw new RuntimeException("Erro ao mover cartão: " + ex.getMessage(), ex);
        }
    }

    public void cancel(final Long cardId, final Long cancelColumnId, final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        if (cardId == null || cardId <= 0 || cancelColumnId == null || cancelColumnId <= 0) {
            throw new IllegalArgumentException("IDs inválidos para cancelamento.");
        }

        try {
            var dao = new CardDAO(connection); // DAO inicializado com conexão
            var optional = dao.findById(cardId);
            var dto = optional.orElseThrow(() -> new EntityNotFoundException("O cartão de ID %s não foi encontrado".formatted(cardId)));
            if (dto.blocked()) {
                throw new CardBlockedException("O cartão %s está bloqueado, é necessário desbloqueá-lo para mover".formatted(cardId));
            }
            var currentColumn = findCurrentColumn(dto.columnId(), boardColumnsInfo);
            if (currentColumn.kind().equals(FINAL)) {
                throw new CardFinishedException("O cartão já foi finalizado");
            }
            findNextColumn(currentColumn, boardColumnsInfo); // Valida que há uma próxima coluna
            dao.moveToColumn(cancelColumnId, cardId);
            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }

    public void block(final Long id, final String reason, final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("O motivo do bloqueio não pode estar vazio.");
        }

        try {
            var dao = new CardDAO(connection); // DAO inicializado com conexão
            var optional = dao.findById(id);
            var dto = optional.orElseThrow(() -> new EntityNotFoundException("O cartão de ID %s não foi encontrado".formatted(id)));
            if (dto.blocked()) {
                throw new CardBlockedException("O cartão %s já está bloqueado".formatted(id));
            }
            var currentColumn = findCurrentColumn(dto.columnId(), boardColumnsInfo);
            if (currentColumn.kind().equals(FINAL) || currentColumn.kind().equals(CANCEL)) {
                throw new IllegalStateException("O cartão está em uma coluna do tipo %s e não pode ser bloqueado".formatted(currentColumn.kind()));
            }
            var blockDAO = new BlockDAO(connection); // DAO inicializado com conexão
            blockDAO.block(reason, id);
            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }

    public void unblock(final Long id, final String reason) throws SQLException {
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("O motivo do desbloqueio não pode estar vazio.");
        }

        try {
            var dao = new CardDAO(connection); // DAO inicializado com conexão
            var optional = dao.findById(id);
            var dto = optional.orElseThrow(() -> new EntityNotFoundException("O cartão de ID %s não foi encontrado".formatted(id)));
            if (!dto.blocked()) {
                throw new CardBlockedException("O cartão %s não está bloqueado".formatted(id));
            }
            var blockDAO = new BlockDAO(connection); // DAO inicializado com conexão
            blockDAO.unblock(reason, id);
            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }

    private BoardColumnInfoDTO findCurrentColumn(Long columnId, List<BoardColumnInfoDTO> boardColumnsInfo) {
        return boardColumnsInfo.stream()
                .filter(bc -> bc.id().equals(columnId))
                .findFirst()
                .orElseThrow(() -> {
                    logger.error("Coluna com ID {} não encontrada para o cartão.", columnId);
                    return new IllegalStateException("O cartão informado pertence a outro quadro.");
                });
    }

    private BoardColumnInfoDTO findNextColumn(BoardColumnInfoDTO currentColumn, List<BoardColumnInfoDTO> boardColumnsInfo) {
        return boardColumnsInfo.stream()
                .filter(bc -> bc.order() == currentColumn.order() + 1)
                .findFirst()
                .orElseThrow(() -> {
                    logger.error("Próxima coluna não encontrada após a ordem {}.", currentColumn.order());
                    return new IllegalStateException("Não há próxima coluna disponível.");
                });
    }
}
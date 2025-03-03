package br.com.dio.persistence.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

//@Data
//public class BoardColumnEntity {
//
//    private Long id;
//    private String name;
//    private int order;
//    private BoardColumnKindEnum kind;
//    private BoardEntity board = new BoardEntity();
//    @ToString.Exclude
//    @EqualsAndHashCode.Exclude
//    private List<CardEntity> cards = new ArrayList<>();
//
//    public void setId(long boardColumnId) {
//        this.id = boardColumnId;
//    }
//
//    public BoardEntity getBoard() {
//        return board;
//    }
//
//    public BoardColumnKindEnum getKind() {
//        return kind;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public int getOrder() {
//        return order;
//    }
//}

@Data
public class BoardColumnEntity {
    private Long id;
    private String name;
    private int order;
    private BoardColumnKindEnum kind;
    private BoardEntity board = new BoardEntity();
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<CardEntity> cards = new ArrayList<>();
}

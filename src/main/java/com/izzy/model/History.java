package com.izzy.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import org.hibernate.annotations.Check;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;

@Entity
@Table(name = "history", schema = "public")
@Check(constraints = "type IN ('user', 'order', 'task', 'notification')")
@Check(constraints = "action in ('create', 'update', 'delete', 'complete', 'cancel', 'approve', 'reject')")
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at")
    private Timestamp createdAt = Timestamp.from(Instant.now());

    @Column(name = "type")
    private String type;

    @Column(name = "action")
    private String action;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "description")
    private String msg;

    public History() {
    }

    public History(String type, String action, Long userId, String msg) {
        this.type = type;
        this.action = action;
        this.userId = userId;
        this.msg = msg;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return msg;
    }

    public void setDescription(String description) {
        this.msg = description;
    }

    public enum Type {
        USER("user"), ORDER("order"), TASK("task"), NOTIFICATION("notification"), UNDEFINED("undefined");
        private final String value;

        Type(String value) {
            this.value = value;
        }

        public static History.Type getTypeByValue(String value) {
            return Arrays.stream(values()).filter(m -> m.getValue().equals(value)).findFirst().orElse(UNDEFINED);
        }

        public static String stringTypeByValue(String value) {
            return Arrays.stream(values()).filter(m -> m.getValue().equals(value)).findFirst().map(History.Type::getValue).orElse("UNDEFINED");
        }

        public String getValue() {
            return value;
        }
    }

    public enum Action {
        CREATE("create"), UPDATE("update"), DELETE("delete"), COMPLETE("complete"), CANCEL("cancel"), APPROVE("approve"), REJECT("reject"), UNDEFINED("undefined");
        private final String value;

        Action(String value) {
            this.value = value;
        }

        public static History.Action getActionByValue(String value) {
            return Arrays.stream(values()).filter(m -> m.getValue().equals(value)).findFirst().orElse(UNDEFINED);
        }

        public static String stringActionByValue(String value) {
            return Arrays.stream(values()).filter(m -> m.getValue().equals(value)).findFirst().map(History.Action::getValue).orElse("UNDEFINED");
        }

        public String getValue() {
            return value;
        }
    }

}

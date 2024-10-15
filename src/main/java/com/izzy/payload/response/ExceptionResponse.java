package com.izzy.payload.response;

import java.io.Serializable;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

public class ExceptionResponse implements Serializable {
    private final List<String> messages;
    private final String error;
    private final Integer status;
    private final Instant timestamp;

    public ExceptionResponse(List<String> messages, String error, Integer status) {
        this.messages = (messages == null) ? null : Collections.unmodifiableList(messages);
        this.error = error;
        this.status = status;
        this.timestamp = Instant.now();
    }

    public List<String> getMessages() {
        return messages;
    }
}

package com.izzy.payload.response;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExceptionResponse {
    private List<String> messages;

    public ExceptionResponse(List<String> messages, String error, Integer status) {
        setMessages(messages);
        Instant timestamp = Instant.now();
    }

    public List<String> getMessages() {
        return messages == null ? null : new ArrayList<>(messages);
    }

    public final void setMessages(List<String> messages) {
        if (messages == null) {
            this.messages = null;
        } else {
            this.messages = Collections.unmodifiableList(messages);
        }
    }

}

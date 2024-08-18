package com.izzy.payload.response;

public class MessageResponse {
    private final int code;
    private final String message;

    public MessageResponse(String message){
        this(message.toLowerCase().contains("error")?400:200, /* Default value for error code = 400 */
                message);
    }
    public MessageResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}

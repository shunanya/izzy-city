package com.izzy.payload.response;

public class MessageResponse {
    private int code;
    private String message;

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

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

package com.izzy.payload.response;

public class MessageResponse extends ApiResponse {

    public MessageResponse(String message){
        this(message.toLowerCase().contains("error")?400:200, /* Default value for error code = 400 */
                message);
    }
    public MessageResponse(int code, String message) {
        super(code, message);
    }
}

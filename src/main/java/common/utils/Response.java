package common.utils;

import common.enums.types.ResponseType;

import java.io.Serializable;

public class Response implements Serializable {
    private ResponseType type;
    private String message;

    public Response(ResponseType responseType, String message) {
        this.type = responseType;
        this.message = message;
    }

    public Response() {}

    public ResponseType getType() {
        return type;
    }

    public void setType(ResponseType type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

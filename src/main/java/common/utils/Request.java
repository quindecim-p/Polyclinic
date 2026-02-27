package common.utils;

import common.enums.types.RequestType;

import java.io.Serializable;

public class Request implements Serializable {
    private RequestType type;
    private String message;

    public Request(RequestType type, String message) {
        this.type = type;
        this.message = message;
    }

    public Request() {}

    public RequestType getType() {
        return type;
    }

    public void setType(RequestType type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
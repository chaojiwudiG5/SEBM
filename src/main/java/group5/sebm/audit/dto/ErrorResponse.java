package group5.sebm.audit.dto;

public class ErrorResponse {
    public int code;
    public String message;
    public Object details;

    public ErrorResponse() {
    }

    public ErrorResponse(int code, String message, Object details) {
        this.code = code;
        this.message = message;
        this.details = details;
    }
}


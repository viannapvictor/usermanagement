package br.com.infnet.edur.usermanagement.utils.messages;

public enum ErrorMessages {
    
    USER_NOT_FOUND("User not found"),
    USER_ALREADY_EXISTS("User already exists"),
    VALIDATION_ERROR("Validation error"),
    INTERNAL_SERVER_ERROR("Internal server error");
    
    private final String message;
    
    ErrorMessages(String message) {
        this.message = message;
    }
    
    public String getMessage() {
        return message;
    }

}
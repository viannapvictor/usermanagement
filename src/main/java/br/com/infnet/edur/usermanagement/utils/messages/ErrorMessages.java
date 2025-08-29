package br.com.infnet.edur.usermanagement.utils.messages;

public enum ErrorMessages {
    
    USER_NOT_FOUND("User not found"),
    USER_ALREADY_EXISTS("User already exists"),
    PRODUCT_NOT_FOUND("Product not found"),
    PRODUCT_ALREADY_EXISTS("Product already exists"),
    CUSTOMER_NOT_FOUND("Customer not found"),
    CUSTOMER_ALREADY_EXISTS("Customer already exists"),
    ORDER_NOT_FOUND("Order not found"),
    ORDER_VALIDATION_ERROR("Order validation error"),
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
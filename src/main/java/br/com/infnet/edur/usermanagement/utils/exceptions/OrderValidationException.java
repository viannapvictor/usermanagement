package br.com.infnet.edur.usermanagement.utils.exceptions;

import br.com.infnet.edur.usermanagement.utils.messages.ErrorMessages;

public class OrderValidationException extends RuntimeException {
    
    public OrderValidationException(String message) {
        super(ErrorMessages.ORDER_VALIDATION_ERROR.getMessage() + ": " + message);
    }
}
package br.com.infnet.edur.usermanagement.utils.exceptions;

import br.com.infnet.edur.usermanagement.utils.messages.ErrorMessages;

public class CustomerAlreadyExistsException extends RuntimeException {
    
    public CustomerAlreadyExistsException(String field, String value) {
        super(ErrorMessages.CUSTOMER_ALREADY_EXISTS.getMessage() + " with " + field + ": " + value);
    }
}
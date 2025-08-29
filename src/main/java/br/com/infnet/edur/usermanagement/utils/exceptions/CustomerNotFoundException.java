package br.com.infnet.edur.usermanagement.utils.exceptions;

import br.com.infnet.edur.usermanagement.utils.messages.ErrorMessages;

public class CustomerNotFoundException extends RuntimeException {
    
    public CustomerNotFoundException(Long id) {
        super(ErrorMessages.CUSTOMER_NOT_FOUND.getMessage() + " with ID: " + id);
    }
}
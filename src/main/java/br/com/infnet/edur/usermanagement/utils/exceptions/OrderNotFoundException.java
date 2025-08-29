package br.com.infnet.edur.usermanagement.utils.exceptions;

import br.com.infnet.edur.usermanagement.utils.messages.ErrorMessages;

public class OrderNotFoundException extends RuntimeException {
    
    public OrderNotFoundException(Long id) {
        super(ErrorMessages.ORDER_NOT_FOUND.getMessage() + " with ID: " + id);
    }
}
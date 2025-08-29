package br.com.infnet.edur.usermanagement.utils.exceptions;

import br.com.infnet.edur.usermanagement.utils.messages.ErrorMessages;

public class ProductNotFoundException extends RuntimeException {
    
    public ProductNotFoundException(Long id) {
        super(ErrorMessages.PRODUCT_NOT_FOUND.getMessage() + " with ID: " + id);
    }
}
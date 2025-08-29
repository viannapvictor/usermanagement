package br.com.infnet.edur.usermanagement.utils.exceptions;

import br.com.infnet.edur.usermanagement.utils.messages.ErrorMessages;

public class ProductAlreadyExistsException extends RuntimeException {
    
    public ProductAlreadyExistsException(String field, String value) {
        super(ErrorMessages.PRODUCT_ALREADY_EXISTS.getMessage() + " with " + field + ": " + value);
    }
}
package br.com.infnet.edur.usermanagement.utils.exceptions;

import br.com.infnet.edur.usermanagement.utils.messages.ErrorMessages;

public class UserAlreadyExistsException extends RuntimeException {
    
    public UserAlreadyExistsException(String field, String value) {
        super(ErrorMessages.USER_ALREADY_EXISTS.getMessage() + " with " + field + ": " + value);
    }
}
package br.com.infnet.edur.usermanagement.utils.exceptions;

import br.com.infnet.edur.usermanagement.utils.messages.ErrorMessages;

public class UserNotFoundException extends RuntimeException {
    
    public UserNotFoundException(Long id) {
        super(ErrorMessages.USER_NOT_FOUND.getMessage() + " with ID: " + id);
    }
}
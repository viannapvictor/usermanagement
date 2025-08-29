package br.com.infnet.edur.usermanagement.utils.exceptions;

public class SupplierAlreadyExistsException extends RuntimeException {
    public SupplierAlreadyExistsException(String field, String value) {
        super("Supplier already exists with " + field + ": " + value);
    }
}
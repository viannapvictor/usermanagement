package br.com.infnet.edur.usermanagement.utils.exceptions;

public class SupplierNotFoundException extends RuntimeException {
    public SupplierNotFoundException(Long id) {
        super("Supplier not found with id: " + id);
    }
}
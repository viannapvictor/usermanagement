package br.com.infnet.edur.usermanagement.service;

import br.com.infnet.edur.usermanagement.model.Supplier;
import br.com.infnet.edur.usermanagement.repository.SupplierRepository;
import br.com.infnet.edur.usermanagement.utils.exceptions.SupplierAlreadyExistsException;
import br.com.infnet.edur.usermanagement.utils.exceptions.SupplierNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierService {
    
    @Autowired
    private SupplierRepository supplierRepository;


    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    public Supplier getSupplierById(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new SupplierNotFoundException(id));
    }

    public Supplier createSupplier(Supplier supplier) {
        
        if (existsByEmail(supplier.getEmail())) {
            throw new SupplierAlreadyExistsException("email", supplier.getEmail());
        }
        
        if (existsByPhoneNumber(supplier.getPhoneNumber())) {
            throw new SupplierAlreadyExistsException("phone number", supplier.getPhoneNumber());
        }
        
        return supplierRepository.save(supplier);
    }

    public Supplier updateSupplier(Long id, Supplier supplier) {
        Supplier existingSupplier = getSupplierById(id);
        
        if (!existingSupplier.getName().equals(supplier.getName()) && existsByName(supplier.getName())) {
            throw new SupplierAlreadyExistsException("name", supplier.getName());
        }
        
        if (!existingSupplier.getEmail().equals(supplier.getEmail()) && existsByEmail(supplier.getEmail())) {
            throw new SupplierAlreadyExistsException("email", supplier.getEmail());
        }
        
        if (!existingSupplier.getPhoneNumber().equals(supplier.getPhoneNumber()) && existsByPhoneNumber(supplier.getPhoneNumber())) {
            throw new SupplierAlreadyExistsException("phone number", supplier.getPhoneNumber());
        }
        
        Supplier updatedSupplier = Supplier.builder()
                .id(id)
                .name(supplier.getName())
                .email(supplier.getEmail())
                .phoneNumber(supplier.getPhoneNumber())
                .build();
        
        return supplierRepository.save(updatedSupplier);
    }

    public void deleteSupplier(Long id) {
        Supplier supplier = getSupplierById(id);
        supplierRepository.delete(supplier);
    }

    public boolean existsByName(String name) {
        return supplierRepository.existsByName(name);
    }

    public boolean existsByEmail(String email) {
        return supplierRepository.existsByEmail(email);
    }

    public boolean existsByPhoneNumber(String phoneNumber) {
        return supplierRepository.existsByPhoneNumber(phoneNumber);
    }
}
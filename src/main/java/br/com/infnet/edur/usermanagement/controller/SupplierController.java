package br.com.infnet.edur.usermanagement.controller;

import br.com.infnet.edur.usermanagement.dto.reponse.APIResponse;
import br.com.infnet.edur.usermanagement.dto.request.SupplierInputDTO;
import br.com.infnet.edur.usermanagement.service.SupplierService;
import br.com.infnet.edur.usermanagement.model.Supplier;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {
    
    @Autowired
    private SupplierService supplierService;
    
    @GetMapping
    public ResponseEntity<APIResponse<List<Supplier>>> getAllSuppliers() {
        List<Supplier> suppliers = supplierService.getAllSuppliers();
        APIResponse<List<Supplier>> response = APIResponse.success(suppliers);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<Supplier>> getSupplierById(@PathVariable Long id) {
        Supplier supplier = supplierService.getSupplierById(id);
        APIResponse<Supplier> response = APIResponse.success(supplier);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping
    public ResponseEntity<APIResponse<Supplier>> createSupplier(@Valid @RequestBody SupplierInputDTO supplierInputDTO) {
        Supplier supplier = new Supplier(supplierInputDTO.getName(), supplierInputDTO.getEmail(), 
                                        supplierInputDTO.getPhoneNumber());
        Supplier createdSupplier = supplierService.createSupplier(supplier);
        APIResponse<Supplier> response = APIResponse.success(createdSupplier, HttpStatus.CREATED.value());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<Supplier>> updateSupplier(@PathVariable Long id, @Valid @RequestBody SupplierInputDTO supplierInputDTO) {
        Supplier supplier = new Supplier(supplierInputDTO.getName(), supplierInputDTO.getEmail(), 
                                        supplierInputDTO.getPhoneNumber());
        Supplier updatedSupplier = supplierService.updateSupplier(id, supplier);
        APIResponse<Supplier> response = APIResponse.success(updatedSupplier);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        APIResponse<Void> response = APIResponse.success(null, HttpStatus.NO_CONTENT.value());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}
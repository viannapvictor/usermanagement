package br.com.infnet.edur.usermanagement.controller;

import br.com.infnet.edur.usermanagement.dto.APIResponse;
import br.com.infnet.edur.usermanagement.service.UserService;
import br.com.infnet.edur.usermanagement.model.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    public ResponseEntity<APIResponse<List<User>>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        APIResponse<List<User>> response = APIResponse.success(users);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<User>> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        APIResponse<User> response = APIResponse.success(user);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping
    public ResponseEntity<APIResponse<User>> createUser(@Valid @RequestBody User user) {
        User createdUser = userService.createUser(user);
        APIResponse<User> response = APIResponse.success(createdUser, HttpStatus.CREATED.value());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<User>> updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        User updatedUser = userService.updateUser(id, user);
        APIResponse<User> response = APIResponse.success(updatedUser);
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{id}")
    public ResponseEntity<APIResponse<User>> partialUpdateUser(@PathVariable Long id, @RequestBody User user) {
        User updatedUser = userService.partialUpdateUser(id, user);
        APIResponse<User> response = APIResponse.success(updatedUser);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        APIResponse<Void> response = APIResponse.success(null, HttpStatus.NO_CONTENT.value());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}
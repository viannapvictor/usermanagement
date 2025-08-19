package br.com.infnet.edur.usermanagement.service;

import br.com.infnet.edur.usermanagement.model.User;
import br.com.infnet.edur.usermanagement.repository.UserRepository;
import br.com.infnet.edur.usermanagement.utils.exceptions.UserAlreadyExistsException;
import br.com.infnet.edur.usermanagement.utils.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public User createUser(User user) {
        if (existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("email", user.getEmail());
        }
        
        if (existsByPhoneNumber(user.getPhoneNumber())) {
            throw new UserAlreadyExistsException("phone number", user.getPhoneNumber());
        }
        
        return userRepository.save(user);
    }

    public User updateUser(Long id, User user) {
        User existingUser = getUserById(id);
        
        if (!existingUser.getEmail().equals(user.getEmail()) && existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("email", user.getEmail());
        }
        
        if (!existingUser.getPhoneNumber().equals(user.getPhoneNumber()) && existsByPhoneNumber(user.getPhoneNumber())) {
            throw new UserAlreadyExistsException("phone number", user.getPhoneNumber());
        }
        
        user.setId(id);
        return userRepository.save(user);
    }

    public User partialUpdateUser(Long id, User user) {
        User existingUser = getUserById(id);
        
        if (user.getFirstName() != null) {
            existingUser.setFirstName(user.getFirstName());
        }
        
        if (user.getLastName() != null) {
            existingUser.setLastName(user.getLastName());
        }
        
        if (user.getEmail() != null) {
            if (!existingUser.getEmail().equals(user.getEmail()) && existsByEmail(user.getEmail())) {
                throw new UserAlreadyExistsException("email", user.getEmail());
            }
            existingUser.setEmail(user.getEmail());
        }
        
        if (user.getPhoneNumber() != null) {
            if (!existingUser.getPhoneNumber().equals(user.getPhoneNumber()) && existsByPhoneNumber(user.getPhoneNumber())) {
                throw new UserAlreadyExistsException("phone number", user.getPhoneNumber());
            }
            existingUser.setPhoneNumber(user.getPhoneNumber());
        }
        
        return userRepository.save(existingUser);
    }

    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }
}
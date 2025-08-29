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
        
        User updatedUser = User.builder()
                .id(id)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .build();
        
        return userRepository.save(updatedUser);
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
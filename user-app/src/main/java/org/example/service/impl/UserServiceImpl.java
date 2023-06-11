package org.example.service.impl;

import org.example.data.model.User;
import org.example.data.repository.UserRepository;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String register(String name, String email, String password) {
        final User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setToken(String.valueOf(System.currentTimeMillis())); // set token as current epoch time
        userRepository.save(user);
        Optional<User> userOptional = userRepository.findByEmailAndPassword(email, password);
        return userOptional.get().getId();
    }

    @Override
    public String login(String email, String password) {
        Iterable<User> users = userRepository.findAllByEmailAndPassword(email, password);
        StringBuffer tokens = new StringBuffer();
        for (User user : users) {
            tokens.append(user.getToken()).append(";");
        }
        if (tokens.length() == 0) {
            return "ERROR: Login denied";
        }
        return tokens.toString();
    }

    @Override
    public String getResource(String token) {
        String [] tokens = token.split(";");
        for (String tokenVal : tokens) {
            Optional<User> userOptional = userRepository.findByToken(tokenVal);
            if (userOptional.isPresent()) {
                return "Secure content accessible";
            }
        }
        return "Secure content not accessible";
    }
}

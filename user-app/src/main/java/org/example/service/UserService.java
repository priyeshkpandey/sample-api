package org.example.service;

import org.springframework.stereotype.Service;

@Service
public interface UserService {
    public void register(String name, String email, String password);
    public String login(String email, String password);
    public String getResource(String token);
}

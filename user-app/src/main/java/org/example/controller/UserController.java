package org.example.controller;

import org.example.data.model.request.LoginRequest;
import org.example.data.model.request.RegisterRequest;
import org.example.data.model.response.LoginResponse;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/register")
    public @ResponseBody String register(@RequestBody RegisterRequest registerRequest) {
        return userService.register(registerRequest.getName(), registerRequest.getEmail(), registerRequest.getPassword());
    }

    @RequestMapping(method = RequestMethod.POST, value = "/login")
    public @ResponseBody LoginResponse login(@RequestBody LoginRequest loginRequest) {
        final LoginResponse loginResponse = new LoginResponse();
        loginResponse.setLoginToken(userService.login(loginRequest.getEmail(), loginRequest.getPassword()));
        return loginResponse;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/resource")
    public @ResponseBody String getResource(@RequestBody String token) {
        return userService.getResource(token);
    }

}

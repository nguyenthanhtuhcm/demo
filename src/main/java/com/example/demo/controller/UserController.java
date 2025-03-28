package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.request.UserCreationRequest;
import com.example.demo.dto.request.UserUpdateRequest;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping
    public ApiResponse<User> createUser(@RequestBody UserCreationRequest request)
    {
        ApiResponse<User> response = new ApiResponse<>();
        response.setResult(userService.CreateUser(request));
        return response;
    }

    @GetMapping
    public ApiResponse<List<User>>  getAllUsers()
    {
        ApiResponse<List<User>> response = new ApiResponse<>();
        response.setResult(userService.getAllUsers());
        return response;
    }
    @GetMapping("/{id}")
    public ApiResponse<User> getUserById(@PathVariable String id)
    {
        ApiResponse<User> response = new ApiResponse<>();
        response.setResult(userService.getUserById(id));
        return response;
    }
    @PutMapping("/{id}")
    public ApiResponse<User> updateUser(@PathVariable String id, @RequestBody UserUpdateRequest request)
    {
        ApiResponse<User> response = new ApiResponse<>();
        response.setResult(userService.updateUser(id, request));
        return response;
    }
    @DeleteMapping("/{id}")
    public ApiResponse deleteUser(@PathVariable String id)
    {
        ApiResponse<User> response = new ApiResponse<>();
        userService.deleteUser(id);
        return  response;

    }
}

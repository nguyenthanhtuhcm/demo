package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.request.UserCreationRequest;
import com.example.demo.dto.request.UserUpdateRequest;
import com.example.demo.dto.response.UserResonse;
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
    public ApiResponse<UserResonse> createUser(@RequestBody UserCreationRequest request)
    {
        return ApiResponse.<UserResonse>builder()
                .result(userService.CreateUser(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<UserResonse>>  getAllUsers()
    {
        return ApiResponse.<List<UserResonse>>builder()
                .result(userService.getAllUsers())
                .build();
    }
    @GetMapping("/{id}")
    public ApiResponse<UserResonse> getUserById(@PathVariable String id)
    {
        return ApiResponse.<UserResonse>builder()
                .result(userService.getUserById(id))
                .build();
    }
    @PutMapping("/{id}")
    public ApiResponse<UserResonse> updateUser(@PathVariable String id, @RequestBody UserUpdateRequest request)
    {
        return ApiResponse.<UserResonse>builder()
                .result(userService.updateUser(id, request))
                .build();
    }
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteUser(@PathVariable String id)
    {
        userService.deleteUser(id);
        return  ApiResponse.<String>builder()
            .result("User deleted successfully")
            .build();

    }
}

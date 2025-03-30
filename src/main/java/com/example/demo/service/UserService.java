package com.example.demo.service;

import com.example.demo.dto.request.UserCreationRequest;
import com.example.demo.dto.request.UserUpdateRequest;
import com.example.demo.dto.response.UserResonse;
import com.example.demo.entity.User;
import com.example.demo.enums.Roles;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {

     UserRepository userRepository;

     UserMapper userMapper;
    public UserResonse CreateUser(UserCreationRequest request)
    {
        if(userRepository.existsByUsername(request.getUsername()))
        {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        User user = userMapper.toUser(request);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        HashSet<String> roles = new HashSet<>();
        roles.add(Roles.USER.name());
        user.setRoles(roles);
        return userMapper.toUserRespone(userRepository.save(user));
    }

    public List<UserResonse> getAllUsers()
    {
        return userRepository.findAll().stream()
                .map(userMapper::toUserRespone)
                .toList();
    }
    @PostAuthorize("returnObject.username == authentication.name")
    public UserResonse getUserById(String id)
    {
        return userMapper.toUserRespone(userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }
    public UserResonse updateUser(String id, UserUpdateRequest request)
    {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        userMapper.updateEntity(request,user);
        if (request.getPassword() != null && !request.getPassword().isEmpty())
        {
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        return userMapper.toUserRespone(userRepository.save(user));
    }
    public void deleteUser(String id)
    {
        userRepository.deleteById(id);
    }

    public UserResonse getUserInfo()
    {
        var name = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return userMapper.toUserRespone(user);
    }
}

package com.example.demo.service;

import com.example.demo.dto.request.UserCreationRequest;
import com.example.demo.dto.request.UserUpdateRequest;
import com.example.demo.dto.response.UserResonse;
import com.example.demo.entity.User;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        return userMapper.toUserRespone(userRepository.save(user));
    }

    public List<UserResonse> getAllUsers()
    {
        return userRepository.findAll().stream()
                .map(userMapper::toUserRespone)
                .toList();
    }
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
}

package com.example.demo.service;

import com.example.demo.dto.request.PermissionCreateRequest;
import com.example.demo.dto.request.UserCreationRequest;
import com.example.demo.dto.request.UserUpdateRequest;
import com.example.demo.dto.response.PermissionResponse;
import com.example.demo.dto.response.UserResonse;
import com.example.demo.entity.Permission;
import com.example.demo.entity.User;
import com.example.demo.enums.Roles;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.PermissionMapper;
import com.example.demo.repository.PermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    public PermissionResponse CreatePermission(PermissionCreateRequest request) {

        Permission permission = permissionMapper.toPermission(request);
        return permissionMapper.toPermissionResponse(permissionRepository.save(permission));
    }

    public List<PermissionResponse> getAllPermissions() {
        return permissionRepository.findAll().stream()
                .map(permissionMapper::toPermissionResponse)
                .toList();
    }

    public PermissionResponse getPermissionById(String id) {
        return permissionMapper.toPermissionResponse(permissionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_EXISTED)));
    }

    public void deletePermissionById(String id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_EXISTED));
        permissionRepository.delete(permission);
    }

}

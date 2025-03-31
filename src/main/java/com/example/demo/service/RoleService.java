package com.example.demo.service;

import com.example.demo.dto.request.PermissionCreateRequest;
import com.example.demo.dto.request.RoleCreateRequest;
import com.example.demo.dto.response.PermissionResponse;
import com.example.demo.dto.response.RoleResponse;
import com.example.demo.entity.Permission;
import com.example.demo.entity.Role;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.PermissionMapper;
import com.example.demo.mapper.RoleMapper;
import com.example.demo.repository.PermissionRepository;
import com.example.demo.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    RoleMapper roleMapper;


    public RoleResponse CreateRole(RoleCreateRequest request) {
        Role role = roleMapper.toRole(request);
        var permission = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permission));
        return roleMapper.toRoleResponse(roleRepository.save(role));
    }

    public List<RoleResponse> getAll() {
        return roleRepository.findAll().stream()
                .map(roleMapper::toRoleResponse)
                .toList();
    }

    public RoleResponse getRoleById(String id) {
        return roleMapper.toRoleResponse(roleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED)));
    }

    public void deleteRoleById(String id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
        roleRepository.delete(role);
    }

}

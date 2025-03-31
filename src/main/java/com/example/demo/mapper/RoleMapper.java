package com.example.demo.mapper;

import com.example.demo.dto.request.PermissionCreateRequest;
import com.example.demo.dto.request.RoleCreateRequest;
import com.example.demo.dto.response.PermissionResponse;
import com.example.demo.dto.response.RoleResponse;
import com.example.demo.entity.Permission;
import com.example.demo.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleCreateRequest request);

    RoleResponse toRoleResponse(Role role);

}

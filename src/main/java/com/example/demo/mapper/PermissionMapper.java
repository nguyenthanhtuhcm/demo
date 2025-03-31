package com.example.demo.mapper;

import com.example.demo.dto.request.PermissionCreateRequest;
import com.example.demo.dto.request.UserCreationRequest;
import com.example.demo.dto.request.UserUpdateRequest;
import com.example.demo.dto.response.PermissionResponse;
import com.example.demo.dto.response.UserResonse;
import com.example.demo.entity.Permission;
import com.example.demo.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionCreateRequest request);
    PermissionResponse toPermissionResponse(Permission permission);

}

package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.request.PermissionCreateRequest;
import com.example.demo.dto.request.RoleCreateRequest;
import com.example.demo.dto.response.PermissionResponse;
import com.example.demo.dto.response.RoleResponse;
import com.example.demo.service.PermissionService;
import com.example.demo.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
    RoleService roleService;
    @PostMapping("/create")
    public ApiResponse<RoleResponse> createPermission(@RequestBody RoleCreateRequest request) {
       return ApiResponse.<RoleResponse>builder()
                .result(roleService.CreateRole(request))
                .build();
    }
    @GetMapping()
    public ApiResponse<List<RoleResponse>> getAll() {
        return ApiResponse.<List<RoleResponse>>builder()
                .result(roleService.getAll())
                .build();
    }
    @GetMapping("/{id}")
    public ApiResponse<RoleResponse> getPermissionById(@PathVariable String id) {
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.getRoleById(id))
                .build();
    }
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> deleteRoleById(@PathVariable String id) {
        boolean tr = true;
        try {
            roleService.deleteRoleById(id);
        } catch (Exception e) {
            tr = false;
        }
        return ApiResponse.<Boolean>builder()
                .result(tr)
                .build();
    }
}

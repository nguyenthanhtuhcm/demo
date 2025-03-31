package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.request.PermissionCreateRequest;
import com.example.demo.dto.response.PermissionResponse;
import com.example.demo.service.PermissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionController {
    PermissionService permissionService;
    @PostMapping("/create")
    public ApiResponse<PermissionResponse> createPermission(@RequestBody PermissionCreateRequest request) {
       return ApiResponse.<PermissionResponse>builder()
                .result(permissionService.CreatePermission(request))
                .build();
    }
    @GetMapping()
    public ApiResponse<List<PermissionResponse>> getAllPermissions() {
        return ApiResponse.<List<PermissionResponse>>builder()
                .result(permissionService.getAllPermissions())
                .build();
    }
    @GetMapping("/{id}")
    public ApiResponse<PermissionResponse> getPermissionById(@PathVariable String id) {
        return ApiResponse.<PermissionResponse>builder()
                .result(permissionService.getPermissionById(id))
                .build();
    }
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> deletePermissionById(@PathVariable String id) {
        boolean tr = true;
        try {
            permissionService.deletePermissionById(id);
        } catch (Exception e) {
            tr = false;
        }
        return ApiResponse.<Boolean>builder()
                .result(tr)
                .build();
    }
}

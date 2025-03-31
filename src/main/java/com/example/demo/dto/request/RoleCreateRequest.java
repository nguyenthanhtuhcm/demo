package com.example.demo.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleCreateRequest {
     String name;
     String description;
     Set<String> permissions;
}

package com.practical.identityService.dto;

import lombok.Data;

@Data
public class AssignRoleRequest {
    private String email;
    private String role;
}
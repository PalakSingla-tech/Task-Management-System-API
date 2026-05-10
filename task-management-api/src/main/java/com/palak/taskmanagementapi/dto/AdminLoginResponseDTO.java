package com.palak.taskmanagementapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminLoginResponseDTO {
    private String token;
    private String refreshToken;
    private Long adminId;
}

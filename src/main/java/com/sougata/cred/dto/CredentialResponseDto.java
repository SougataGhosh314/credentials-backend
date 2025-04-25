package com.sougata.cred.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CredentialResponseDto {

    private Long id;
    private String title;
    private String username;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Getters and Setters
}
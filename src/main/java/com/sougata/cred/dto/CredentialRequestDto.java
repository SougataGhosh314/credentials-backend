package com.sougata.cred.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CredentialRequestDto {
    @NotBlank
    private String title;
    private String username;

    @NotBlank
    private String password;
    private String description;

    // Getters and Setters
}

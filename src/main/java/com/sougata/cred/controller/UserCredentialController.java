package com.sougata.cred.controller;

import com.sougata.cred.dto.CredentialRequestDto;
import com.sougata.cred.dto.CredentialResponseDto;
import com.sougata.cred.model.*;
import com.sougata.cred.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/credentials")
public class UserCredentialController {

    private final CredentialService credentialService;
    private final EncryptionService encryptionService;

    public UserCredentialController(CredentialService credentialService, EncryptionService encryptionService) {
        this.credentialService = credentialService;
        this.encryptionService = encryptionService;
    }

    @PostMapping
    public ResponseEntity<CredentialResponseDto> createCredential(
            @AuthenticationPrincipal UserEntity user,
            @Valid @RequestBody CredentialRequestDto dto
    ) {

        CredentialEntity entity = new CredentialEntity();
        entity.setUserEntity(user);
        entity.setTitle(dto.getTitle());
        entity.setUsername(dto.getUsername());
        entity.setDescription(dto.getDescription());
        entity.setEncryptedPassword(encryptionService.encrypt(dto.getPassword()));

        CredentialEntity saved = credentialService.createCredential(entity);
        return ResponseEntity.ok(toDto(saved));
    }

    @GetMapping
    public List<CredentialResponseDto> getAll(@AuthenticationPrincipal UserEntity user) {
        return credentialService.getAllForUser(user).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CredentialResponseDto> getById(@PathVariable Long id, @AuthenticationPrincipal UserEntity user) {
        return credentialService.getByIdForUser(id, user)
                .map(cred -> ResponseEntity.ok(toDto(cred)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CredentialResponseDto> update(@PathVariable Long id,
                                                        @Valid @RequestBody CredentialRequestDto dto,
                                                        @AuthenticationPrincipal UserEntity user) {
        return credentialService.updateCredential(id, dto, user)
                .map(updated -> ResponseEntity.ok(toDto(updated)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal UserEntity user) {
        return credentialService.deleteCredential(id, user)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/password")
    public ResponseEntity<String> getDecryptedPassword(
            @PathVariable Long id,
            @AuthenticationPrincipal UserEntity user
    ) {
        String password = credentialService.getDecryptedPassword(id, user);
        return ResponseEntity.ok(password);
    }


    private CredentialResponseDto toDto(CredentialEntity entity) {
        CredentialResponseDto dto = new CredentialResponseDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setUsername(entity.getUsername());
        dto.setDescription(entity.getDescription());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
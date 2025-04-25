package com.sougata.cred.service;

import com.sougata.cred.dto.CredentialRequestDto;
import com.sougata.cred.model.CredentialEntity;
import com.sougata.cred.model.UserEntity;
import com.sougata.cred.repository.CredentialRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CredentialService {

    private final CredentialRepository repository;
    private final EncryptionService encryptionService;

    public CredentialService(CredentialRepository repository, EncryptionService encryptionService) {
        this.repository = repository;
        this.encryptionService = encryptionService;
    }

    public CredentialEntity createCredential(CredentialEntity entity) {
        return repository.save(entity);
    }

    public List<CredentialEntity> getAllForUser(UserEntity user) {
        return repository.findAllByUserEntity(user);
    }

    public Optional<CredentialEntity> getByIdForUser(Long id, UserEntity user) {
        return repository.findByIdAndUserEntity(id, user);
    }

    public Optional<CredentialEntity> updateCredential(Long id, CredentialRequestDto dto, UserEntity user) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return repository.findByIdAndUserEntity(id, user).map(cred -> {
            cred.setTitle(dto.getTitle());
            cred.setUsername(dto.getUsername());
            cred.setDescription(dto.getDescription());
            cred.setEncryptedPassword(encryptionService.encrypt(dto.getPassword()));
            return repository.save(cred);
        });
    }

    public boolean deleteCredential(Long id, UserEntity user) {
        return repository.findByIdAndUserEntity(id, user).map(cred -> {
            repository.delete(cred);
            return true;
        }).orElse(false);
    }

    public String getDecryptedPassword(Long id, UserEntity user) {
        CredentialEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Credential not found"));

        // Check if the credential belongs to the authenticated user
        if (!entity.getUserEntity().getId().equals(user.getId())) {
            throw new AccessDeniedException("Unauthorized access");
        }

        return encryptionService.decrypt(entity.getEncryptedPassword());
    }

}

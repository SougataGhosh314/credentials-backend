package com.sougata.cred.repository;

import com.sougata.cred.model.CredentialEntity;
import com.sougata.cred.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CredentialRepository extends JpaRepository<CredentialEntity, Long> {
    Optional<CredentialEntity> findByIdAndUserEntity(Long id, UserEntity userEntity);
    List<CredentialEntity> findAllByUserEntity(UserEntity userEntity);
}
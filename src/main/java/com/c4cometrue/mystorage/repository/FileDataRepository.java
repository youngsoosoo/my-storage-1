package com.c4cometrue.mystorage.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.c4cometrue.mystorage.entity.FileData;

@Repository
public interface FileDataRepository extends JpaRepository<FileData ,UUID> {
}

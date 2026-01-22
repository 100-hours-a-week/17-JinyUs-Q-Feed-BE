package com.ktb.file.repository;

import com.ktb.file.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * File 엔티티 Repository
 */
@Repository
public interface FileRepository extends JpaRepository<File, Long> {
}

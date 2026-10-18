package com.dobidan.bandeutseolap.domain.file.repository;

import com.dobidan.bandeutseolap.domain.file.entity.RelProjectFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RelProjectFileRepository extends JpaRepository<RelProjectFile, Long> {
}

package com.dobidan.bandeutseolap.domain.file.repository;

import com.dobidan.bandeutseolap.domain.file.entity.AppFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppFileRepository extends JpaRepository<AppFile, Long> {

}
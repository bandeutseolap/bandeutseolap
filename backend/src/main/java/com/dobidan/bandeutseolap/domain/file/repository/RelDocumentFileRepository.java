package com.dobidan.bandeutseolap.domain.file.repository;

import com.dobidan.bandeutseolap.domain.file.entity.RelDocumentFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RelDocumentFileRepository extends JpaRepository<RelDocumentFile,Long>  {
}

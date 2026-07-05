package com.dobidan.bandeutseolap.domain.file.event;


public record PendingFilePayload(
        Long fileId,
        String storageKey,
        String targetDir,
        byte[] fileBytes) { }

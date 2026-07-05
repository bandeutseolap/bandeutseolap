package com.dobidan.bandeutseolap.domain.file.event;
import com.dobidan.bandeutseolap.domain.file.event.PendingFilePayload;

import java.util.List;

public class FileUploadedEvent {
    private final List<PendingFilePayload> payloads;

    public FileUploadedEvent(List<PendingFilePayload> payloads) {
        this.payloads = payloads;
    }

    public List<PendingFilePayload> getPayloads() {
        return payloads;
    }
}

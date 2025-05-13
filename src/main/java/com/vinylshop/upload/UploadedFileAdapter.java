package com.vinylshop.upload;

import org.springframework.core.io.InputStreamSource;

import java.io.IOException;

public interface UploadedFileAdapter {
    String getName();
    String getOriginalFilename();
    String getDescription();

    String getContentType();

    long getSize();

    String getExtension();

    InputStreamSource getInputStream() throws IOException;

    byte[] getBytes() throws IOException;

    default String getSafeFilename() {
        return getOriginalFilename() != null ? getOriginalFilename().replaceAll("[^a-zA-Z0-9\\.\\-_]", "_") : "file";
    }
}
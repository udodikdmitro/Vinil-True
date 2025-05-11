package com.vinylshop.upload;

import org.springframework.core.io.InputStreamSource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class MultipartFileUploadedFileAdapter implements UploadedFileAdapter {
    private final MultipartFile file;

    public MultipartFileUploadedFileAdapter(MultipartFile file) {
        this.file = file;
    }

    @Override
    public String getName() {
        return file.getName();
    }

    @Override
    public String getOriginalFilename() {
        return file.getOriginalFilename();
    }

    @Override
    public String getDescription() {
        return file.getResource().getDescription();
    }

    @Override
    public String getContentType() {
        return file.getContentType();
    }

    @Override
    public long getSize() {
        return file.getSize();
    }

    public String getExtension() {
        String name = file.getOriginalFilename();
        return name != null && name.contains(".") ? name.substring(name.lastIndexOf('.') + 1) : "";
    }

    @Override
    public InputStreamSource getInputStream() throws IOException {
        return file;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return file.getBytes();
    }
}
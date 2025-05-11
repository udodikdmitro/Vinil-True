package com.vinylshop.upload;

import org.apache.poi.ss.usermodel.PictureData;
import org.springframework.core.io.InputStreamSource;

import java.io.ByteArrayInputStream;

public class SsPictureDataUploadedFileAdapter implements UploadedFileAdapter {
    private final PictureData picture;

    public SsPictureDataUploadedFileAdapter(PictureData picture) {
        this.picture = picture;
    }

    @Override
    public String getName() {
        return getOriginalFilename();
    }

    @Override
    public String getOriginalFilename() {
        return "image." + picture.suggestFileExtension();
    }

    @Override
    public String getDescription() {
        return getOriginalFilename();
    }

    @Override
    public String getExtension() {
        return picture.suggestFileExtension();
    }

    @Override
    public String getContentType() {
        return picture.getMimeType();
    }

    @Override
    public long getSize() {
        return picture.getData().length;
    }

    @Override
    public InputStreamSource getInputStream() {
        return () -> new ByteArrayInputStream(picture.getData());
    }

    public byte[] getBytes() {
        return picture.getData();
    }

}
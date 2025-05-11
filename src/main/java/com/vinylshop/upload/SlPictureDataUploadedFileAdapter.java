package com.vinylshop.upload;

import org.apache.poi.sl.usermodel.PictureData;
import org.springframework.core.io.InputStreamSource;

import java.io.ByteArrayInputStream;

public class SlPictureDataUploadedFileAdapter implements UploadedFileAdapter {
    private final PictureData picture;

    public SlPictureDataUploadedFileAdapter(PictureData picture) {
        this.picture = picture;
    }

    @Override
    public String getName() {
        return getOriginalFilename();
    }

    @Override
    public String getOriginalFilename() {
        return "image." + getExtension();
    }

    @Override
    public String getDescription() {
        return getOriginalFilename();
    }

    @Override
    public String getExtension() {
        return picture.getType().extension;
    }

    @Override
    public String getContentType() {
        return picture.getType().contentType;
    }

    @Override
    public long getSize() {
        return getBytes().length;
    }

    @Override
    public InputStreamSource getInputStream() {
        return () -> new ByteArrayInputStream(picture.getData());
    }

    @Override
    public byte[] getBytes() {
        return picture.getData();
    }
}
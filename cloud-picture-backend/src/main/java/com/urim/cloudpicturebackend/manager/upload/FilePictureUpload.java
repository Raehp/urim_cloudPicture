package com.urim.cloudpicturebackend.manager.upload;

import com.urim.cloudpicturebackend.exception.ErrorCode;
import com.urim.cloudpicturebackend.exception.ThrowUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Url图片上传
 */
@Service
public class FilePictureUpload extends PictureUploadTemplate {
    @Override
    protected void validPicture(Object inputSource) {
        MultipartFile multipartFile = (MultipartFile) inputSource;
        // 1. 校验文件大小
        long size = multipartFile.getSize();
        final long ONE_MB = 1024 * 1024;
        ThrowUtils.throwIf(size > 2 * ONE_MB, ErrorCode.PARAMS_ERROR, "文件大小不能超过 2MB");
        // 2. 校验文件后缀
        // 允许上传的文件类型
        final List<String> ALLOW_FORMAT_TYPE = Arrays.asList("jpg", "jpeg", "png", "webp");
        String fileSuffix = multipartFile.getOriginalFilename();
        ThrowUtils.throwIf(ALLOW_FORMAT_TYPE.contains(fileSuffix), ErrorCode.PARAMS_ERROR, "文件格式错误");
    }

    @Override
    protected String getOriginalFilename(Object inputSource) {
        MultipartFile multipartFile = (MultipartFile) inputSource;
        return multipartFile.getOriginalFilename();
    }

    @Override
    protected void processFile(Object inputSource, File file) throws Exception {
        MultipartFile multipartFile = (MultipartFile) inputSource;
        multipartFile.transferTo(file);
    }
}

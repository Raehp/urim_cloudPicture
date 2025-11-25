package com.urim.cloudpicturebackend.model.dto.file;

import lombok.Data;

/**
 * 上传图片的结果
 */
@Data
public class UploadPictureResult {
    /**
     * 图片url
     */
    private String url;
    /**
     * 缩略图url
     */
    private String thumbnailUrl;
    /**
     * 图片名称
     */
    private String picName;
    /**
     * 图片大小
     */
    private Long picSize;
    /**
     * 图片宽度
     */
    private int picWidth;
    /**
     * 图片高度
     */
    private int picHeight;
    /**
     * 图片比例
     */
    private Double picScale;
    /**
     * 图片格式
     */
    private String picFormat;
    /**
     * 图片主色调
     */
    private String picColor;
}

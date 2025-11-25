package com.urim.cloudpicturebackend.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class PictureTageCategory {

    /**
     * 标签列表
     */
    private List<String> tagList;

    /**
     * 分类列表
     */
    private List<String> categoryList;
}

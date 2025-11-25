package com.urim.cloudpicturebackend.model.vo;

import cn.hutool.json.JSONUtil;
import com.urim.cloudpicturebackend.model.entity.Picture;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class PictureVo implements Serializable {
    /**
     * 图片id
     */
    private Long id;
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
    private String name;
    /**
     * 图片简介
     */
    private String introduction;
    /**
     * 图片标签
     */
    private List<String> tags;
    /**
     * 图片分类
     */
    private String category;
    /**
     * 图片大小
     */
    private Long picSize;
    /**
     * 图片宽度
     */
    private Integer picWidth;
    /**
     * 图片高度
     */
    private Integer picHeight;
    /**
     * 图片比例
     */
    private String picScale;
    /**
     * 图片格式
     */
    private String picFormat;
    /**
     * 图片主色调
     */
    private String picColor;
    /**
     * 图片上传者id
     */
    private Long userId;
    /**
     * 图片上传时间
     */
    private Date createTime;
    /**
     * 图片修改时间
     */
    private Date editTime;
    /**
     * 图片更新时间
     */
    private Date updateTime;
    /**
     * 图片上传者信息
     */
    private UserVo user;
    /**
     * 空间id
     */
    private Long spaceId;

    /**
     * 权限列表
     */
    private List<String> permissionList = new ArrayList<>();

    private static final long serialVersionUID = 1;

    /**
     * 封装类转对象
     */
    public static Picture voToObj(PictureVo pictureVO) {
        if (pictureVO == null) {
            return null;
        }
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureVO, picture);
        // 类型不同，需要转换
        picture.setTags(JSONUtil.toJsonStr(pictureVO.getTags()));
        return picture;
    }

    /**
     * 对象转封装类
     */
    public static PictureVo objToVo(Picture picture) {
        if (picture == null) {
            return null;
        }
        PictureVo pictureVO = new PictureVo();
        BeanUtils.copyProperties(picture, pictureVO);
        pictureVO.setTags(JSONUtil.toList(picture.getTags(), String.class));
        return pictureVO;
    }

}

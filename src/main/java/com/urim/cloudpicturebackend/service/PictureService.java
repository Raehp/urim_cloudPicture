package com.urim.cloudpicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.urim.cloudpicturebackend.api.aliyunai.model.CreateOutPaintingTaskResponse;
import com.urim.cloudpicturebackend.model.dto.picture.*;
import com.urim.cloudpicturebackend.model.entity.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import com.urim.cloudpicturebackend.model.entity.User;
import com.urim.cloudpicturebackend.model.vo.PictureVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author zhl20
 * @description 针对表【picture(图片)】的数据库操作Service
 * @createDate 2025-10-07 17:41:09
 */
public interface PictureService extends IService<Picture> {
    /**
     * 上传图片
     *
     * @return
     */
    PictureVo uploadpicture(Object object,
                            PictureUploadRequest pictureUploadRequest,
                            User loginUser);


    /**
     * 删除图片（私有）
     *
     * @param pictureId
     * @param loginUser
     */
    void deletePicture(long pictureId, User loginUser);


    /**
     * 编辑图片
     *
     * @param pictureEditRequest
     * @param loginUser
     */
    void editPicture(PictureEditRequest pictureEditRequest, User loginUser);

    /**
     * 校验图片
     *
     * @param picture
     */
    void validPicture(Picture picture);

    /**
     * 获取单个图片封装类
     * @param picture
     * @param request
     * @return
     */
    PictureVo getPictureVo(Picture picture, HttpServletRequest request);

    /**
     * 分页获取图片封装类
     * @param picturePage
     * @param request
     * @return
     */
    Page<PictureVo> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request);

    /**
     * 分页查询图片
     *
     * @param pictureQueryRequest
     * @return
     */
    QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);

    /**
     * 图片审核
     *
     * @param PictureReviewRequest
     * @param loginUser
     */
    void doPictureReview(PictureReviewRequest PictureReviewRequest, User loginUser);

    /**
     * 填充审核参数
     *
     * @param picture
     * @param user
     */
    void fillReviewParam(Picture picture, User user);


    /**
     * 批量抓取和创建文件
     * @param pictureUploadByBatchRequest
     * @param loginUser
     * @return
     */
    Integer uploadPictureByBatch(PictureUploadByBatchRequest pictureUploadByBatchRequest, User loginUser);

    /**
     * 删除图片
     */
    void clearPictureFile(Picture oldPicture);

    /**
     * 检查空间图片权限
     */
    void checkPictureAuth(User loginUser, Picture picture);

    /**
     * 根据颜色搜索图片
     * @param spaceId 空间ID，用于指定要搜索的图片所属的空间
     * @param picColor 图片颜色，用于筛选指定颜色的图片
     * @param loginUser 当前登录用户，用于权限验证
     * @return 返回符合条件的PictureVo对象列表，包含图片相关信息
     */
    List<PictureVo> searchPictureByColor(Long spaceId, String picColor, User loginUser);

    /**
     * 批量编辑图片
     *
     * @param pictureEditByBatchRequest 图片批量编辑请求参数
     * @param loginUser                 当前登录用户
     */
    void editPictureByBatch(PictureEditByBatchRequest pictureEditByBatchRequest, User loginUser);


    /**
     * 创建图片扩图任务
     *
     * @param createPictureOutPaintingTaskRequest 创建图片扩图任务请求参数
     * @param loginUser                           当前登录用户
     */
    CreateOutPaintingTaskResponse createPictureOutPaintingTask(CreatePictureOutPaintingTaskRequest createPictureOutPaintingTaskRequest, User loginUser);
}

package com.urim.cloudpicturebackend.api.imagesearch.sub;

import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;
import com.urim.cloudpicturebackend.exception.BusinessException;
import com.urim.cloudpicturebackend.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 获取以图搜图页面地址（step 1）
 */
@Slf4j
public class GetImagePageUrlApi {

    /**
     * 获取图片页面地址
     *
     * @param imageUrl
     * @return
     */
    public static String getImagePageUrl(String imageUrl) {
        // 1. 准备请求参数
        Map<String, Object> formData = new HashMap<>();
        formData.put("image", imageUrl);
        formData.put("tn", "pc");
        formData.put("from", "pc");
        formData.put("image_source", "PC_UPLOAD_URL");
        // 获取当前时间戳
        long uptime = System.currentTimeMillis();
        // 请求地址
        String url = "https://graph.baidu.com/upload?uptime=" + uptime;
        String accessToken =                "1756613940445_1756639223796_ChpSZVch6wnjQeJ+oBTpxUG9qUIn+S6VSpqpKQLtvhqz1r7DDj3mDK5dtsED6R/4KO9LaNddXno6QTpBQZL64SkitrbB5Z5wUKCpZrRnnfqRX2rLs/V2ypLE4BMRbXnFTtjMmCQxOPFYDxhN1usQEyjduH8ceqcXiE3YSer1A5gxBPFEFkmJVnrZn0/HpG6oaupcoH8urDxzDqdSNqhrqotuq2GL/gE/OMV8aIF1LzOAkkBJje0fdWLT2zCGux9IZilFzWQtzkdX4lR3ffJmjRRi3PeRuY1EHjSCK/Zh/Kcz0bsCqF5r8vQpuhjNbo31tIekaDW8s/l4zOxiVoblrsMOT8t912c5o/cwH7LjYsjqbHtLaBh0vZHd1ir3ddP0DJApLSO8KCkJEpXh+GujXOwwb1hrqNIC1NUw1uQyleo=";


        try {
            // 2. 发送 POST 请求到百度接口
            HttpResponse response = HttpRequest.post(url)
                    .form(formData)
                    .timeout(5000)
                    .header("acs-token",accessToken)
                    .execute();
            // 判断响应状态
            if (HttpStatus.HTTP_OK != response.getStatus()) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口调用失败");
            }
            // 解析响应
            String responseBody = response.body();
            Map<String, Object> result = JSONUtil.toBean(responseBody, Map.class);

            // 3. 处理响应结果
            if (result == null || !Integer.valueOf(0).equals(result.get("status"))) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口调用失败");
            }
            Map<String, Object> data = (Map<String, Object>) result.get("data");
            String rawUrl = (String) data.get("url");
            // 对 URL 进行解码
            String searchResultUrl = URLUtil.decode(rawUrl, StandardCharsets.UTF_8);
            // 如果 URL 为空
            if (searchResultUrl == null) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "未返回有效结果");
            }
            return searchResultUrl;
        } catch (Exception e) {
            log.error("搜索失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "搜索失败");
        }
    }

    public static void main(String[] args) {
        // 测试以图搜图功能
        String imageUrl = "https://qcloud.dpfile.com/pc/sZBSqXu33HQuBThgbhEbmC7YnywjxRWhI24575NEcKOMpU7jk1n9-dyjZitV3vvb.jpg";
        String result = getImagePageUrl(imageUrl);
        System.out.println("搜索成功，结果 URL：" + result);
    }
}


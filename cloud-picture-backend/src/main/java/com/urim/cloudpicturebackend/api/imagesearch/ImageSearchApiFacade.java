package com.urim.cloudpicturebackend.api.imagesearch;

import com.urim.cloudpicturebackend.api.imagesearch.model.ImageSearchResult;
import com.urim.cloudpicturebackend.api.imagesearch.sub.GetImageFirstUrlApi;
import com.urim.cloudpicturebackend.api.imagesearch.sub.GetImageListApi;
import com.urim.cloudpicturebackend.api.imagesearch.sub.GetImagePageUrlApi;

import java.util.List;

public class ImageSearchApiFacade {
    /**
     * 得到以图搜图的结果列表
     * @param url   目标图片的url
     * @return  搜图的结果
     */
    public static List<ImageSearchResult> getImages(String url){
        String imagePageUrl = GetImagePageUrlApi.getImagePageUrl(url);
        String imageFirstUrl = GetImageFirstUrlApi.getImageFirstUrl(imagePageUrl);
        return GetImageListApi.getImageList(imageFirstUrl);
    }
}

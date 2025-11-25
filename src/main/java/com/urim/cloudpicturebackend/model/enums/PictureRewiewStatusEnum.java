package com.urim.cloudpicturebackend.model.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

/**
 * 图片审核状态枚举
 */
@Getter
public enum PictureRewiewStatusEnum {
    REWIEWING("待审核", 0),
    PASS("通过", 1),
    REJECT("拒绝", 2);

    private final String text;

    private final int value;

    PictureRewiewStatusEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 通过value获取枚举值
     *
     * @param value 枚举值的value
     */
    public static PictureRewiewStatusEnum getByValue(Integer value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (PictureRewiewStatusEnum pictureRewiewStatusEnum : PictureRewiewStatusEnum.values()) {
            if (pictureRewiewStatusEnum.value == value) {
                return pictureRewiewStatusEnum;
            }
        }
        return null;
    }
}

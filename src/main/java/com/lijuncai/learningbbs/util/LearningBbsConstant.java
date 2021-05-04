package com.lijuncai.learningbbs.util;

/**
 * @description: 常量相关接口
 * @author: lijuncai
 **/
public interface LearningBbsConstant {
    /**
     * 激活成功
     */
    int ACTIVATION_SUCCESS = 0;

    /**
     * 重复激活
     */
    int ACTIVATION_REPEAT = 1;

    /**
     * 激活失败
     */
    int ACTIVATION_FAILURE = 2;

    /**
     * 默认状态的登录凭证的超时时间:12小时
     */
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    /**
     * 勾选“记住我”选项的登录凭证超时时间:100天
     */
    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100;

    /**
     * 评论所属的实体类型：帖子
     */
    int ENTITY_TYPE_POST = 1;

    /**
     * 评论所属的实体类型：评论
     */
    int ENTITY_TYPE_COMMENT = 2;
}

package com.yioj.model.model.dto.submission;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建请求
 *
 
 
 */
@Data
public class JudgeInfo implements Serializable {
    /**
     * 时间
     */
    private Long timeUsed;
    /**
     * 内存
     */
    private Long memoryUsed;
    /**
     * 结果
     */
    private String resultStr;
    /**
     * 出错代码
     */
    private String detailCode;

}
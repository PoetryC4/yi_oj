package com.yi.oj.model.dto.problem;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建请求
 *
 
 
 */
@Data
public class ProblemAddRequest implements Serializable {
    /**
     * 题目标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 官解
     */
    private String solution;

    /**
     * 难度
     */
    private Integer difficulty;
    /**
     * 题目标签列表
     */
    private List<String> tags;

    /**
     * 标签列表（json 数组）
     */
    private JudgeConfig judgeConfig;

    /**
     * 样例（回车分割）
     */
    private JudgeCases judgeCases;
    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * vip尊享?
     */
    private Integer isVip;
    /**
     * 函数名称，默认函数形式，初步处理函数，变量类型
     */
    private FunctionConfig functionConfig;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
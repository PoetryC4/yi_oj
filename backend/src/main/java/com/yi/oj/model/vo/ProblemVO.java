package com.yi.oj.model.vo;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.yi.oj.model.dto.problem.FunctionConfig;
import com.yi.oj.model.dto.problem.JudgeCases;
import com.yi.oj.model.dto.problem.JudgeConfig;
import com.yi.oj.model.entity.Problem;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 题
 *
 * @TableName problem
 */
@Data
public class ProblemVO implements Serializable {
    private final static Gson GSON = new Gson();
    /**
     * id
     */
    private Long id;

    /**
     * 题目标题
     */
    private String title;

    /**
     * 官解
     */
    private String solution;
    /**
     * 题目标签列表
     */
    private List<String> tags;

    /**
     * 难度
     */
    private Integer difficulty;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表（json 数组）
     */
    private JudgeConfig judgeConfig;

    /**
     * 样例（回车分割）
     */
    private JudgeCases judgeCases;
    /**
     * 通过数
     */
    private Long accpetedCount;
    /**
     * 提交数
     */
    private Long submittedCount;
    /**
     * 通过率
     */
    private Double acceptance;

    /**
     * 点赞数
     */
    private Integer thumbNum;

    /**
     * 点踩数
     */
    private Integer disLikeNum;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * vip尊享?
     */
    private Integer isVip;

    /**
     * 用户
     */
    private UserVO userVO;
    /**
     * 是否已解决
     */
    private Integer isSolved;

    /**
     * 是否已点赞
     */
    private Boolean isLiked;
    /**
     * 是否已点踩
     */
    private Boolean isDisliked;
    /**
     * 函数名称，默认函数形式，初步处理函数，变量类型
     */
    private FunctionConfig functionConfig;

    public static Problem voToObj(ProblemVO problemVO) {
        if (problemVO == null) {
            return null;
        }
        Problem problem = new Problem();
        BeanUtils.copyProperties(problemVO, problem);
        JudgeConfig judgeConfig1 = problemVO.getJudgeConfig();
        if (judgeConfig1 != null) {
            problem.setJudgeConfig(com.alibaba.fastjson2.JSON.toJSONString(judgeConfig1));
        }
        List<String> problemTag1 = problemVO.getTags();
        if (problemTag1 != null) {
            problem.setTags(com.alibaba.fastjson2.JSON.toJSONString(problemTag1));
        }
        FunctionConfig functionConfig1 = problemVO.getFunctionConfig();
        if (functionConfig1 != null) {
            problem.setFunctionConfig(com.alibaba.fastjson2.JSON.toJSONString(functionConfig1));
        }
        JudgeCases judgeCases1 = problemVO.getJudgeCases();
        if (judgeCases1 != null) {
            problem.setJudgeCases(com.alibaba.fastjson2.JSON.toJSONString(judgeCases1));
        }
        return problem;
    }

    /**
     * 对象转包装类
     *
     * @param problem
     * @return
     */
    public static ProblemVO objToVo(Problem problem) {
        if (problem == null) {
            return null;
        }
        ProblemVO problemVO = new ProblemVO();
        BeanUtils.copyProperties(problem, problemVO);
        String judgeConfig1 = problem.getJudgeConfig();
        if (StringUtils.isNotBlank(judgeConfig1)) {
            problemVO.setJudgeConfig(JSON.parseObject(judgeConfig1, JudgeConfig.class));
        }
        String problemTag1 = problem.getTags();
        if (StringUtils.isNotBlank(problemTag1)) {
            problemVO.setTags(JSON.parseArray(problemTag1, String.class));
        }
        String functionConfig1 = problem.getFunctionConfig();
        if (StringUtils.isNotBlank(functionConfig1)) {
            problemVO.setFunctionConfig(JSON.parseObject(functionConfig1, FunctionConfig.class));
        }
        String judgeCases1 = problem.getJudgeCases();
        if (StringUtils.isNotBlank(judgeCases1)) {
            problemVO.setJudgeCases(JSON.parseObject(judgeCases1, JudgeCases.class));
        }
        return problemVO;
    }
}
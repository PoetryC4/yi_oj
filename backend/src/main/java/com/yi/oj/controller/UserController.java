package com.yi.oj.controller;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yi.oj.annotation.AuthCheck;
import com.yi.oj.common.BaseResponse;
import com.yi.oj.common.DeleteRequest;
import com.yi.oj.common.ErrorCode;
import com.yi.oj.common.ResultUtils;
import com.yi.oj.config.WxOpenConfig;
import com.yi.oj.constant.UserConstant;
import com.yi.oj.exception.BusinessException;
import com.yi.oj.exception.ThrowUtils;
import com.yi.oj.model.dto.user.*;
import com.yi.oj.model.vo.UserRecordVO;
import com.yi.oj.service.EmailService;
import com.yi.oj.service.UserService;
import com.yi.oj.model.entity.User;
import com.yi.oj.model.vo.LoginUserVO;
import com.yi.oj.model.vo.UserVO;

import java.io.File;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.yi.oj.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * {
 * "userAccount":"huang",
 * "userPassword":"12345678",
 * "checkPassword":"12345678",
 * "userEmail":"3@ex.com"
 * }
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private UserService userService;

    @Resource
    private WxOpenConfig wxOpenConfig;

    @Resource
    private EmailService emailService;

    private static String userDir = System.getProperty("user.dir");

    private static String globalAvatarPath = userDir + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "avatars";


    // region 登录相关

    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String userEmail = userRegisterRequest.getUserEmail();
        String emailVerifyCode = userRegisterRequest.getEmailVerifyCode();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, userEmail, emailVerifyCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword, userEmail, emailVerifyCode);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        String userEmail = userLoginRequest.getUserEmail();
        Boolean isEmail = userLoginRequest.getIsEmail();
        if (isEmail) {
            if (StringUtils.isAnyBlank(userEmail, userPassword)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
            LoginUserVO loginUserVO = userService.userLogin("", userPassword, userEmail, request);
            return ResultUtils.success(loginUserVO);
        } else {
            if (StringUtils.isAnyBlank(userAccount, userPassword)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
            LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword, "", request);
            return ResultUtils.success(loginUserVO);
        }
    }

    /**
     * 用户登录（微信开放平台）
     */
/*    @GetMapping("/login/wx_open")
    public BaseResponse<LoginUserVO> userLoginByWxOpen(HttpServletRequest request, HttpServletResponse response,
                                                       @RequestParam("code") String code) {
        WxOAuth2AccessToken accessToken;
        try {
            WxMpService wxService = wxOpenConfig.getWxMpService();
            accessToken = wxService.getOAuth2Service().getAccessToken(code);
            WxOAuth2UserInfo userInfo = wxService.getOAuth2Service().getUserInfo(accessToken, code);
            String unionId = userInfo.getUnionId();
            String mpOpenId = userInfo.getOpenid();
            if (StringUtils.isAnyBlank(unionId, mpOpenId)) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "登录失败，系统错误");
            }
            return ResultUtils.success(userService.userLoginByMpOpen(userInfo, request));
        } catch (Exception e) {
            log.error("userLoginByWxOpen error", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "登录失败，系统错误");
        }
    }*/

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        return ResultUtils.success(userService.getLoginUserVO(user));
    }

    // endregion

    // region 增删改查

    /**
     * 创建用户
     *
     * @param userAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest, HttpServletRequest request) {
        if (userAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        boolean result = userService.save(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(user.getId());
    }

    /**
     * 删除用户
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    /**
     * 更新用户
     *
     * @param userUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest,
                                            HttpServletRequest request) {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        User user1 = userService.getById(user.getId());
        // 用户不存在
        if (user1 == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 3. 记录用户的登录态
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, user1);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取用户（仅管理员）
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String key = "UserCache:" + id;
        User user = (User) redisUtils.get(key);
        if (user == null) {
            user = userService.getById(id);
            ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
            redisUtils.set(key, user);
        }
        return ResultUtils.success(user);
    }

    /**
     * 根据 id 获取包装类
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserVO> getUserVOById(long id, HttpServletRequest request) {
        BaseResponse<User> response = getUserById(id, request);
        User user = response.getData();
        return ResultUtils.success(userService.getUserVO(user));
    }

    /**
     * 分页获取用户列表（仅管理员）
     *
     * @param userQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<User>> listUserByPage(@RequestBody UserQueryRequest userQueryRequest,
                                                   HttpServletRequest request) {
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        Page<User> userPage = userService.page(new Page<>(current, size),
                userService.getQueryWrapper(userQueryRequest));
        return ResultUtils.success(userPage);
    }

    /**
     * 分页获取用户封装列表
     *
     * @param userQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest,
                                                       HttpServletRequest request) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<User> userPage = userService.page(new Page<>(current, size),
                userService.getQueryWrapper(userQueryRequest));
        Page<UserVO> userVOPage = new Page<>(current, size, userPage.getTotal());
        List<UserVO> userVO = userService.getUserVO(userPage.getRecords());
        userVOPage.setRecords(userVO);
        return ResultUtils.success(userVOPage);
    }

    // endregion

    /**
     * 更新个人信息
     *
     * @param userUpdateMyRequest
     * @param request
     * @return
     */
    @PostMapping("/update/my")
    public BaseResponse<Boolean> updateMyUser(@RequestBody UserUpdateMyRequest userUpdateMyRequest,
                                              HttpServletRequest request) {
        if (userUpdateMyRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        User user = new User();
        BeanUtils.copyProperties(userUpdateMyRequest, user);
        user.setId(loginUser.getId());
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        User user1 = userService.getById(user.getId());
        // 用户不存在
        if (user1 == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 3. 记录用户的登录态
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, user1);
        return ResultUtils.success(true);
    }


    /**
     * 根据 id 获取用户（仅管理员）
     *
     * @param userEmail
     * @param request
     * @return
     */
    @GetMapping("/sendCode")
    public BaseResponse<String> sendVerifyCode(String userEmail, HttpServletRequest request) {
/*        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";

        // 编译正则表达式
        Pattern pattern = Pattern.compile(regex);

        // 创建Matcher对象
        Matcher matcher = pattern.matcher(userEmail);

        // 使用find()方法查找匹配项*/
        if (StringUtils.isAnyBlank(userEmail)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱错误");
        }
        emailService.sendVerificationCode(userEmail);
        return ResultUtils.success("验证码已发送，请检查您的邮箱。");
    }

    @GetMapping("/get/record")
    public BaseResponse<UserRecordVO> getUserRecordById(long id, HttpServletRequest request) {
        if (id < 0) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户错误");
        }
        return ResultUtils.success(userService.getUserRecordVO(id));
    }

    @PostMapping("/avatar/update")
    public BaseResponse<Boolean> updateAvatar(@RequestParam MultipartFile file, HttpServletRequest request) throws Exception {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (!FileUtil.exist(globalAvatarPath)) {
            FileUtil.mkdir(globalAvatarPath);
        }
        String originalFileName = file.getOriginalFilename();
        String suffix = ".png";
        for (int i = originalFileName.length()-1; i >=0; i--) {
            if(originalFileName.charAt(i) == '.') {
                suffix = originalFileName.substring(i);
                break;
            }
        }

        // 指定服务器上的保存路径，可以根据你的需求修改
        Long id = loginUser.getId();
        String savePath = globalAvatarPath + File.separator + id;

        if (!FileUtil.exist(savePath)) {
            FileUtil.mkdir(savePath);
        }
        File directory = FileUtil.file(savePath);

        if (directory.isDirectory()) {
            // 获取目标文件夹下的所有文件
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file1 : files) {
                    if (file1.isFile()) {
                        // 删除文件
                        FileUtil.del(file1);
                    }
                }
            }
        }
        String fileName = String.valueOf(id)+suffix;
        // 使用Hutool的FileUtil来保存文件
        FileUtil.writeBytes(file.getBytes(), savePath + File.separator + fileName);
        User user = new User();
        user.setId(id);
        user.setUserAvatar(fileName);
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        User user1 = userService.getById(user.getId());
        // 用户不存在
        if (user1 == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 3. 记录用户的登录态
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, user1);
        return ResultUtils.success(result);
    }
    @PostMapping("/update/password")
    public BaseResponse<Boolean> userUpdatePassword(@RequestBody UserUpdatePasswordRequest userUpdatePasswordRequest) {
        if (userUpdatePasswordRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userPassword = userUpdatePasswordRequest.getUserPassword();
        String checkPassword = userUpdatePasswordRequest.getCheckPassword();
        String userEmail = userUpdatePasswordRequest.getUserEmail();
        String emailVerifyCode = userUpdatePasswordRequest.getEmailVerifyCode();
        if (StringUtils.isAnyBlank(userPassword, checkPassword, userEmail, emailVerifyCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.userUpdatePassword(userPassword, checkPassword, userEmail, emailVerifyCode);
        return ResultUtils.success(result);
    }
}

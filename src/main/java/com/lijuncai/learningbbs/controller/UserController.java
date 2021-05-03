package com.lijuncai.learningbbs.controller;

import com.lijuncai.learningbbs.annotaion.LoginRequired;
import com.lijuncai.learningbbs.entity.User;
import com.lijuncai.learningbbs.service.UserService;
import com.lijuncai.learningbbs.util.HostHolder;
import com.lijuncai.learningbbs.util.LearningBbsUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @description: 处理用户相关请求
 * @author: lijuncai
 **/
@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Value("${learning-bbs.path.upload}")
    private String uploadPath;

    @Value("${learning-bbs.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    /**
     * 访问"账号设置"页面
     *
     * @return 页面模板地址
     */
    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage() {
        return "/site/setting";
    }

    /**
     * 更新用户头像
     *
     * @param headerImage 新头像url
     * @param model       model对象
     * @return 模板页面
     */
    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "您还没有选择图片!");
            return "/site/setting";
        }
        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (StringUtils.isBlank(suffix) || !suffix.equals(".png") || !suffix.equals(".jpg")) {
            model.addAttribute("error", "图片的格式不正确，仅支持png和jpg!");
            return "/site/setting";
        }
        //生成随机文件名
        fileName = LearningBbsUtil.generateUUID() + suffix;
        //确定文件存放的路径
        File dest = new File(uploadPath + "/" + fileName);
        try {
            //存储文件
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败: " + e.getMessage());
            throw new RuntimeException("上传文件失败,服务器发生异常!", e);
        }
        //更新当前用户的头像的路径(web访问路径)
        //http://localhost:8080/learning-bbs/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(), headerUrl);

        return "redirect:/index";
    }

    /**
     * 获取用户头像
     *
     * @param fileName 头像文件名
     * @param response 响应对象
     */
    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        // 服务器存放路径
        fileName = uploadPath + "/" + fileName;
        // 文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // 响应图片
        response.setContentType("image/" + suffix);
        try (
                FileInputStream fis = new FileInputStream(fileName);
                OutputStream os = response.getOutputStream();
        ) {
            byte[] buffer = new byte[1024];
            int data = 0;
            while ((data = fis.read(buffer)) != -1) {
                os.write(buffer, 0, data);
            }
        } catch (IOException e) {
            logger.error("读取头像失败: " + e.getMessage());
        }
    }

    /**
     * 修改用户密码
     *
     * @param oldPassword 原密码
     * @param newPassword 新密码
     * @param model       model对象
     * @return 模板页面
     */
    @RequestMapping(path = "/update-password", method = RequestMethod.POST)
    public String updatePassword(String oldPassword, String newPassword, Model model) {
        int status = userService.updatePassword(oldPassword, newPassword);
        if (status == -1) {
            model.addAttribute("password_error", "原密码不正确!");
            return "/site/setting";
        }
        model.addAttribute("msg", "密码修改成功!");
        model.addAttribute("target", "/index");
        return "/site/operate-result";
    }
}
package com.lijuncai.learningbbs.controller;

import com.lijuncai.learningbbs.entity.DiscussPost;
import com.lijuncai.learningbbs.entity.Page;
import com.lijuncai.learningbbs.entity.User;
import com.lijuncai.learningbbs.service.DiscussPostService;
import com.lijuncai.learningbbs.service.LikeService;
import com.lijuncai.learningbbs.service.UserService;
import com.lijuncai.learningbbs.util.HostHolder;
import com.lijuncai.learningbbs.util.LearningBbsConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 处理首页相关请求的控制类
 * @author: lijuncai
 **/
@Controller
public class IndexController implements LearningBbsConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    /**
     * 访问首页,获取首页数据
     *
     * @return String "首页"的模板路径
     */
    @RequestMapping(path = {"/index", "/"}, method = RequestMethod.GET)
    public String getIndexData(Model model, Page page) {
        //方法在调用前，SpringMVC会自动实例化Model和Page对象，并且Page对象会被注入到Model中
        //所以，在前端thymeleaf中可以直接访问Page对象中的数据
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");

        List<DiscussPost> discussPostList = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit());
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        //在显示时需要将discussPost中的userId对应到用户名
        if (discussPostList != null) {
            for (DiscussPost discussPost : discussPostList) {
                //每个HashMap中存放的是帖子数据和用户数据、还有帖子点赞的数据
                Map<String, Object> map = new HashMap<>();
                map.put("post", discussPost);
                User user = userService.findUserById(discussPost.getUserId());
                map.put("user", user);
                //获取帖子点赞数量
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPost.getId());
                map.put("likeCount", likeCount);
                discussPosts.add(map);
            }
        }
        //将discussPosts添加至model
        model.addAttribute("discussPosts", discussPosts);

        return "/index";
    }

}

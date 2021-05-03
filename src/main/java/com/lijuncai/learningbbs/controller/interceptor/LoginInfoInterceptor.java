package com.lijuncai.learningbbs.controller.interceptor;

import com.lijuncai.learningbbs.entity.LoginTicket;
import com.lijuncai.learningbbs.entity.User;
import com.lijuncai.learningbbs.service.UserService;
import com.lijuncai.learningbbs.util.CookieUtil;
import com.lijuncai.learningbbs.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * @description: 登录信息拦截器，用于显示登录之后的信息
 * @author: lijuncai
 **/
@Component
public class LoginInfoInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    /**
     * 在本次请求中持有当前user对象
     * preHandle()在Controller处理请求之前调用
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param handler  被拦截的对象
     * @return 处理是否成功
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //从cookie中获取凭证字符串
        String ticket = CookieUtil.getValue(request, "ticket");

        if (ticket != null) {
            //通过凭证字符串获取凭证对象
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            //检查凭证是否有效
            if (loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())) {
                //根据凭证中的userId查询对应的用户
                User user = userService.findUserById(loginTicket.getUserId());
                //在本次请求中持有用户
                hostHolder.setUser(user);
            }
        }
        return true;
    }

    /**
     * 将user对象存入ModelAndView,供模板引擎使用
     * postHandle()方法在Controller处理之后、模板引擎TemplateEngine执行之前调用
     *
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            modelAndView.addObject("loginUser", user);
        }
    }

    /**
     * 本次请求处理完毕，清除user对象
     * 在模板引擎执行完毕之后，就可以对user对象进行清理了
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CookieUtil;
import com.nowcoder.community.util.HostHolder;
import com.nowcoder.community.util.LoginTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;


@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Override//Before arrive controller
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ticket= CookieUtil.getValue(request,"ticket");
        if(ticket!=null)
        {
            LoginTicket loginTicket=userService.findLoginTicket(ticket);
            if(loginTicket!=null && loginTicket.getStatus()==0 && loginTicket.getExpired().after(new Date()))
            {
                User user=userService.findUserById(loginTicket.getUserId());
                hostHolder.setUsers(user);
            }
        }
        return true;
    }

    @Override//After arrive controller before arrive thymeleaf
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user=hostHolder.getUser();
        if(user!=null && modelAndView!=null)
        {
            modelAndView.addObject("loginUser",user);
        }
    }

    @Override//After thymeleaf processing templates
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}

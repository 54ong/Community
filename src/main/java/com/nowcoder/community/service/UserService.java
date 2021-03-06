package com.nowcoder.community.service;

import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.LoginTicket;
import com.nowcoder.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private LoginTicketMapper loginTicketMapper;
    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findUserById(int id)
    {
        return userMapper.selectById(id);
    }

    public Map<String,Object> register(User user)
    {
        Map<String,Object> map=new HashMap<>();
        if(user==null) throw new IllegalArgumentException("参数不能为空");
        if(StringUtils.isBlank(user.getUserName()))
        {
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getPassWord()))
        {
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail()))
        {
            map.put("emailMsg","邮箱不能为空");
            return map;
        }

        User u=userMapper.selectByName(user.getUserName());
        if(u!=null)
        {
            map.put("usernameMsg","该用户已存在");
            return map;
        }
        u=userMapper.selectByEmail(user.getEmail());
        if(u!=null)
        {
            map.put("emailMsg","该邮箱已注册");
            return map;
        }

        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassWord(CommunityUtil.md5(user.getPassWord()+user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl("/resources/default.png");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        Context context=new Context();
        context.setVariable("email",user.getEmail());
        String url=domain+contextPath+"/activation/"+user.getId()+"/"+user.getActivationCode();
        context.setVariable("url",url);

        String content=templateEngine.process("/mail/activation",context);
        mailClient.sendMail(user.getEmail(),"激活账号",content);
        return map;
    }

    public int activation(int userId,String code)
    {
        User user=userMapper.selectById(userId);
        if(user.getStatus()==1) return ACTIVATION_REPEAT;
        if(user.getActivationCode().equals(code))
        {
            userMapper.updateStatus(userId,1);
            return ACTIVATION_SUCCESS;
        }
        return ACTIVATION_FAILURE;
    }

    public Map<String,Object> login(String username,String password, long expiredSeconds)
    {
        Map<String,Object> map=new HashMap<>();
        if(StringUtils.isBlank(username))
        {
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        if(StringUtils.isBlank(password))
        {
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        User user=userMapper.selectByName(username);
        if(user==null)
        {
            map.put("usernameMsg","该账号不存在");
            return map;
        }
        if(user.getStatus()==0)
        {
            map.put("usernameMsg","该账号未激活");
            return map;
        }
        if(!CommunityUtil.md5(password+user.getSalt()).equals(user.getPassWord()))
        {
            map.put("passwordMsg","密码不正确");
            return map;
        }

        LoginTicket loginTicket=new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+1000*expiredSeconds));
        loginTicketMapper.insertLoginTicket(loginTicket);
        map.put("ticket",loginTicket.getTicket());
        return map;
    }

    public void logout(String ticket)
    {
        loginTicketMapper.updateStatus(ticket,1);

    }

    public LoginTicket findLoginTicket(String ticket)
    {
        return loginTicketMapper.selectByTicket(ticket);
    }
}

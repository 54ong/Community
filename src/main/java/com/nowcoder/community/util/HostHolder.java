package com.nowcoder.community.util;

import com.nowcoder.community.entity.User;
import org.springframework.stereotype.Component;

//代替session对象，且线程隔离
@Component
public class HostHolder {

    private ThreadLocal<User> users=new ThreadLocal<>();

    public void setUsers(User user)
    {
        users.set(user);
    }
    public User getUser()
    {
        return users.get();
    }
    public void clear()
    {
        users.remove();
    }
}

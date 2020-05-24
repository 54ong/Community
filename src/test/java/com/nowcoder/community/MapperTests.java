package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

@SpringBootTest
@ContextConfiguration(classes=CommunityApplication.class)
public class MapperTests
{
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    public void testSelectUser()
    {
        User user=userMapper.selectById(101);
        System.out.println(user);
    }

    @Test
    public void testSelectPosts()
    {
        List<DiscussPost> list= discussPostMapper.selectDiscussPosts(0,0,10);
        System.out.println(list.size());
        for(DiscussPost discussPost:list)
        {
            System.out.println(discussPost);
        }

        int rows=discussPostMapper.selectDiscussPostsRows(0);
        System.out.println(rows);
    }
}

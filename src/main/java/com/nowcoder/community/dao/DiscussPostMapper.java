package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    List<DiscussPost> selectDiscussPosts(int userId,int offset,int limit);
    //userId为0则不将此参数计入Sql语句内
    //offset 代表分页的时候，每一页第一条记录显示的是哪个帖子
    //limit 代表分页的时候，每一页的容量

    int selectDiscussPostsRows(@Param("userId") int userId);
    //@Param 用于给参数取别名
    //如果要在<if>中使用这个参数，且该方法只有一个参数，则必须使用Param



}

package com.kxdkcf.mapper;

import com.kxdkcf.annotation.AutoTime;
import com.kxdkcf.constant.OperationType;
import com.kxdkcf.enity.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * BelongsProject:      demo-cf
 * BelongsPackage:      com.kxdkcf.mapper
 * Author:              wenhao
 * CreateTime:          2024-12-27  13:49
 * Description:         用户相关dao
 * Version:             1.0
 */
@Mapper
public interface UserMapper {

    User queryUser(User user);

    //@Insert("insert into users (user_name, password, avatar, create_time, update_time)" +
    //       "value (#{userName},#{password}, #{avatar}, #{createTime}, #{updateTime})")
    /*@Options(useGeneratedKeys = true,keyProperty = "id",keyColumn = "id")*/
    @AutoTime(OperationType.INSERT)
    void insertUser(User user);

    @AutoTime(OperationType.UPDATE)
    void updateUser(User user);

    @Select("select id, user_name, password, avatar, create_time, update_time, email from users where user_name=#{user_name}")
        //@Options(useGeneratedKeys = true,keyProperty = "id",keyColumn = "id")
    User getByUserName(/*@Param("user_name")*/ String userName);

    //@Select("select  user_name, avatar from users where id =#{id}")
    User getUserById(Long id);

    User getUserAllById(Long id);

    @Select("select id, user_name, password, avatar, create_time, update_time, email from users")
    List<User> selectAllUser();

    @Delete("delete from users where id=#{userId}")
    void deleteById(Long userId);
}

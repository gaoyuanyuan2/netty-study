package com.netty.rpc.provider;

import com.netty.rpc.api.IRpcObject;
import com.netty.rpc.dto.User;

/**
 * Created by yan on  24/10/2019.
 */
public class RpcObject implements IRpcObject
{


    @Override
    public User setUser(String userName, int age)
    {
        User user = new User();
        user.setUserName(userName);
        user.setAge(age);
        user.setRemark("Test");
        return user;
    }
}

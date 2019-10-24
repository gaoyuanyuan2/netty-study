package com.netty.rpc.api;

import com.netty.rpc.dto.User;

/**
 * Created by yan on  24/10/2019.
 */
public interface IRpcObject {

    User setUser(String userName, int age);

}

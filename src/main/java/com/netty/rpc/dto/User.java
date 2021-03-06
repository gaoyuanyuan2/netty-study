package com.netty.rpc.dto;

import java.io.Serializable;

/**
 * Created by yan on  24/10/2019.
 */
public class User implements Serializable
{
    private String userName;

    private int age;

    private String remark;

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public int getAge()
    {
        return age;
    }

    public void setAge(int age)
    {
        this.age = age;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    @Override
    public String toString()
    {
        return "User{" +
                "userName='" + userName + '\'' +
                ", age=" + age +
                ", remark='" + remark + '\'' +
                '}';
    }
}

package com.netty.rpc.provider;


import com.netty.rpc.api.IRpcHello;

public class RpcHello implements IRpcHello {

	@Override
	public String hello(String name) {
		return "Hello , " + name + "!";
	}

}

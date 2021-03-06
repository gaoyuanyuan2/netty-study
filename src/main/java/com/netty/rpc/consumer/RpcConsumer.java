package com.netty.rpc.consumer;


import com.netty.rpc.api.IRpcCalc;
import com.netty.rpc.api.IRpcHello;
import com.netty.rpc.api.IRpcObject;
import com.netty.rpc.consumer.proxy.RpcProxy;

public class RpcConsumer {
	
	public static void main(String[] args) {
		
		//本机一个人在玩
		//自娱自乐
		//肯定是用动态代理来实现的,传给它一个接口，返回一个实例，伪代理
		IRpcHello rpcHello = RpcProxy.create(IRpcHello.class);
		String r = rpcHello.hello("Tom老师");
		System.out.println(r);


		int a = 8,b = 2;
		IRpcCalc calc = RpcProxy.create(IRpcCalc.class);
		System.out.println(a + " + " + b +" = " + calc.add(a, b));
		System.out.println(a + " - " + b +" = " + calc.sub(a, b));
		System.out.println(a + " * " + b +" = " + calc.mult(a, b));
		System.out.println(a + " / " + b +" = " + calc.div(a, b));


		IRpcObject object = RpcProxy.create(IRpcObject.class);

		System.out.println("object:" + object.setUser("张三",8));
		
	}
	
}

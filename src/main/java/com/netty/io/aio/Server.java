package com.netty.io.aio;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/*
*
* 读写异步，jdk1.7 异步非阻塞
*
* NIO 2.0引入了新的异步通道的概念，并提供了异步文件通道和异步套接字通道的实现。异步通道提供以下两种方式获取获取操作结果。
* 通过java. util.concurrent.Future类来表示异步操作的结果;
* 在执行异步操作的时候传入一个java.nio.channels。
* CompletionHandler接口的实现类作为操作完成的回调。
*
* NIO2.0的异步套接字通道是真正的异步非阻塞I/O,对应于UNIX网络编程中的事件驱动I/O(AIO)。
* 它不需要通过多路复用器(Selector)对注册的通道进行轮询操作即可实现异步读写，从而简化了NIO的编程模型。
*
*
*/
public class Server {
	//线程池
	private ExecutorService executorService;
	//线程组（配合线程池使用，避免频繁创建销毁线程，提高效率）
	private AsynchronousChannelGroup threadGroup;
	//服务器通道
	public AsynchronousServerSocketChannel assc;
	
	public Server(int port){
		try {
			//创建一个缓存池
			executorService = Executors.newCachedThreadPool();
			//创建线程组
			threadGroup = AsynchronousChannelGroup.withCachedThreadPool(executorService, 1);
			//创建服务器通道
			assc = AsynchronousServerSocketChannel.open(threadGroup);
			//进行绑定
			assc.bind(new InetSocketAddress(port));
			
			System.out.println("server start , port : " + port);
			//进行阻塞
			assc.accept(this, new ServerCompletionHandler());//关键代码
			//一直阻塞 不让服务器停止
			Thread.sleep(Integer.MAX_VALUE);//实际开发在容器中运行，不需要sleep
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Server server = new Server(8765);
	}
	
}

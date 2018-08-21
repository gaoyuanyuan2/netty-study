package com.netty.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * 1/O多路复用的主要应用场景如下。
 * 服务器需要同时处理多个处于监听状态或者多个连接状态的套接字;
 * 服务器需要同时处理多种网络协议的套接字。
 *
 *  在NIO库中，所有数据都是用缓冲区处理的。在读取数据时，它是直接读到缓冲区中的;在写入数据时，写入到缓冲区中。
 *  任何时候访问NIO中的数据，都是通过缓冲区进行操作。
 *  大多数都是ByteBuffer
 *
 *  一个多路复用器Selector可以同时轮询多个Channel，由于JDK使用了epoll()代替 传统的select 实现，
 *  所以它并没有最大连接句柄1024/2048 的限制。
 *  这也就意味着只需要一个线程负责Selector的轮询，
 *  就可以接入成千上万的客户端，这确实是个非常巨大的进步。
 */


public class Client {

	//需要一个Selector 
	public static void main(String[] args) {
		
		//创建连接的地址
		InetSocketAddress address = new InetSocketAddress("127.0.0.1", 8765);
		
		//声明连接通道
		SocketChannel sc = null;
		
		//建立缓冲区
		ByteBuffer buf = ByteBuffer.allocate(1024);
		
		try {
			//步骤一:打开SocketChannel,绑定客户端本地地址(可选，默认系统会随机分配一个可用的本地地址)
			//打开通道
			sc = SocketChannel.open();
			//步骤二：设置SocketChannel为非阻塞模式，同时设置客户端连接的TCP参数
			sc.configureBlocking(false);
			//进行连接
			//步骤三:异步连接服务端
			sc.connect(address);

			//步骤四:判断是否连接成功，如果连接成功，则直接注册读状态位到多路复用器中，
			//如果当前没有连接成功(异步连接，返回false,说明客户端已经发送sync包，服务端没有返回ack包，物理链路还没有建立)
			//步骤五:向Reactor线程的多路复用器注册OP_CONNECT状态位，监听服务端的TCPACK应答
			//步骤六:创建Reactor线程，创建多路复用器并启动线程
			//步骤七:多路复用器在线程run方法的无限循环体内轮询准备就绪的Key
			//步骤八:接收connect事件进行处理
			//步骤九:判断连接结果，如果连接成功，注册读事件到多路复用器
			//步骤十:注册读事件到多路复用器
			//步骤十一:异步读客户端请求消息到缓冲区
			//步骤十二:对ByteBuffer进行编解码，如果有半包消息接收缓冲区Reset, 继续读取后续的报文，将解码成功的消息封装成Task,投递到业务线程池中，进行业务逻辑编排。
			//步骤十三:将POJO对象encode成ByteBuffer, 调用SocketChannel的异步write 接口,将消息异步发送给客户端。
			while(true){
				//定义一个字节数组，然后使用系统录入功能：
				byte[] bytes = new byte[1024];
				System.in.read(bytes);
				
				//把数据放到缓冲区中
				buf.put(bytes);
				//对缓冲区进行复位
				buf.flip();
				//写出数据
				sc.write(buf);
				//清空缓冲区数据
				buf.clear();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(sc != null){
				try {
					sc.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
}

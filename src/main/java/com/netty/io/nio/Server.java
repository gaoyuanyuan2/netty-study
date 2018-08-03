package com.netty.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
/**
 * Selector是Java的非阻塞I/O实现的关键。它使用了事件通知API以确定在一组非阻塞套接字中有哪些已经就绪能够进行I/O相关的操作。
 * 因为可以在任何的时间检查任意的读操作或者写操作的完成状态，一个单一的线程便可以处理多个并发的连接。
 *
 * NIO编程的优点总结如下。
 * (1)客户端发起的连接操作是异步的，可以通过在多路复用器注册OP_ CONNECT等待后续结果，不需要像之前的客户端那样被同步阻塞。
 * (2) SocketChannel 的读写操作都是异步的，如果没有可读写的数据它不会同步等待，直接返回，
 * 这样1/O通信线程就可以处理其他的链路，不需要同步等待这个链路可用。
 * (3)线程模型的优化:由于JDK的Selector 在Linux等主流操作系统.上通过epoll 实现，
 * 它没有连接句柄数的限制(只受限于操作系统的最大句柄数或者对单个进程的句柄限制)，
 * 这意味着一个Selector 线程可以同时处理成千，上万个客户端连接，而且性能不会随着客户端的增加而线性下降。
 * 因此，它非常适合做高性能、高负载的网络服务器。
 *JDK1.7升级了NIO类库，升级后的NIO类库被称为NIO 2.0。 引人注目的是,
 *Java正式提供了异步文件I/O操作，  同时提供了与UNIX网络编程事件驱动1/O对应的AIO。
 */

public class Server implements Runnable{
	//1 多路复用器（管理所有的通道）
	private Selector seletor;
	//2 建立缓冲区
	private ByteBuffer readBuf = ByteBuffer.allocate(1024);
	//3 
	private ByteBuffer writeBuf = ByteBuffer.allocate(1024);
	public Server(int port){
		try {
			//1 打开路复用器
			// 步骤三:创建Reactor线程，创建多路复用器并启动线程
			this.seletor = Selector.open();
			//2 打开服务器通道   /
			// 步骤一:打开ServerSocketChannel,用于监听客户端的连接，它是所有客户端连接的父管道
			ServerSocketChannel ssc = ServerSocketChannel.open();
			//3 设置服务器通道为非阻塞模式
			ssc.configureBlocking(false);
			//4 绑定地址
			// 步骤二:绑定监听端口，设置连接为非阻塞模式
			ssc.bind(new InetSocketAddress(port));
			//5 把服务器通道注册到多路复用器上，并且监听阻塞事件
			// 步骤四:将ServerSocketChannel注册到Reactor线程的多路复用器Selector.上，监听ACCEPT事件
			ssc.register(this.seletor, SelectionKey.OP_ACCEPT);
			
			System.out.println("Server start, port :" + port);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while(true){
			try {
				//1 必须要让多路复用器开始监听
				// 步骤五:多路复用器在线程run方法的无限循环体内轮询准备就绪的Key
				this.seletor.select();
				//2 返回多路复用器已经选择的结果集
				Iterator<SelectionKey> keys = this.seletor.selectedKeys().iterator();
				//3 进行遍历
				while(keys.hasNext()){
					//4 获取一个选择的元素
					SelectionKey key = keys.next();
					//5 直接从容器中移除就可以了
					keys.remove();
					//6 如果是有效的
					if(key.isValid()){
						//7 如果为阻塞状态
						if(key.isAcceptable()){
							this.accept(key);
						}
						//8 如果为可读状态
						if(key.isReadable()){
							this.read(key);
						}
						//9 写数据
						if(key.isWritable()){
							//this.write(key); //ssc
						}
					}
					
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void write(SelectionKey key){
		// 步骤十一:将POJO对象encode成ByteBuffer, 调用SocketChannel的异步write 接口，将消息异步发送给客户端
		//ServerSocketChannel ssc =  (ServerSocketChannel) key.channel();
		//ssc.register(this.seletor, SelectionKey.OP_WRITE);
	}

	private void read(SelectionKey key) {
		try {
			//1 清空缓冲区旧的数据
			this.readBuf.clear();
			//2 获取之前注册的socket通道对象
			SocketChannel sc = (SocketChannel) key.channel();
			//3 读取数据
			//步骤九:异步读取客户端请求消息到缓冲区

			//由于我们已经将SocketChannel设置为异步非阻塞模式，因此它的read
			// 是非阻塞的。使用返回值进行判断，看读取到的字节数，返回值有以下三种可能的结果。
			// 返回值大于0:读到了字节，对字节进行编解码;
			// 返回值等于0:没有读取到字节，属于正常场景，忽略;
			// 返回值为-1:链路已经关闭，需要关闭SocketChannel,释放资源。

			int count = sc.read(this.readBuf);
			//4 如果没有数据
			if(count == -1){
				key.channel().close();
				key.cancel();
				return;
			}

			//步骤十:对ByteBuffer进行编解码，如果有半包消息指针reset,继续读取后续的报文，
			// 将解码成功的消息封装成Task,投递到业务线程池中，进行业务逻辑编排。

			//5 有数据则进行读取 读取之前需要进行复位方法(把position 和limit进行复位)
			this.readBuf.flip();

			// 由于SocketChannel是异步非阻塞的，
			// 它并不保证一次能够把需要发送的字节数组发送完，
			// 此时会出现“写半包”问题。我们需要注册写操作，
			// 不断轮询Selector 将没有发送完的ByteBuffer 发送完毕，
			// 然后可以通过ByteBuffer的hasRemain(方法判断消息是否发送完成。

			//6 根据缓冲区的数据长度创建相应大小的byte数组，接收缓冲区的数据
			byte[] bytes = new byte[this.readBuf.remaining()];

			//7 接收缓冲区数据
			this.readBuf.get(bytes);
			//8 打印结果
			String body = new String(bytes).trim();
			System.out.println("Server : " + body);
			
			// 9..可以写回给客户端数据 
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private void accept(SelectionKey key) {
		try {
			//1 获取服务通道
			ServerSocketChannel ssc =  (ServerSocketChannel) key.channel();
			//2 执行阻塞方法
			//步骤六:多路复用器监听到有新的客户端接入，处理新的接入请求，完成TCP三次握手，建立物理链路
			SocketChannel sc = ssc.accept();
			//3 设置阻塞模式
			//步骤七:设置客户端链路为非阻塞模式
			sc.configureBlocking(false);
			//4 注册到多路复用器上，并设置读取标识
			// 步骤八:将新接入的客户端连接注册到Reactor 线程的多路复用器上，监听读操作，读取客户端发送的网络消息，示例代码如下。
			sc.register(this.seletor, SelectionKey.OP_READ);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		new Thread(new Server(8765)).start();;
	}
	
	
}

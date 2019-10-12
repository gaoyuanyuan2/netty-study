package com.netty.demo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Server {

    /**
     * 一旦‘boss’接收到连接，就会把连接信息注册到‘worker’上。
     * 如何知道多少个线程已经被使用，
     * 如何映射到已经创建的Channels上都需要依赖于EventLoopGroup的实现，
     * 并且可以通过构造函数来配置他们的关系。
     * <p>
     * <p>
     * *   在开始使用Netty开发TimeServer之前，先回顾一下 使用NIO进行服务端开发的步骤。
     * (1)创建ServerSocketChannel, 配置它为非阻塞模式;
     * (2)绑定监听，配置TCP参数，例如backlog大小;
     * (3)创建一个独立的l/O线程，用于轮询多路复用器Selector;
     * (4)创建Selector, 将之前创建的ServerSocketChannel 注册到Selector 上，监听SelectionKey.ACCEPT;
     * (5)启动1/O线程，在循环体中执行Selector. select()方法， 轮询就绪的Channel;
     * (6)当轮询到了处于就绪状态的Channel时，需要对其进行判断，如果是OP_ ACCEPT状态，说明是新的客户端接入，
     * 则调用ServerSocketChannel accept()方法接受新的客户端;
     * (7)设置新接入的客户端链路SocketChannel为非阻塞模式，配置其他的一些TCP参数;
     * (8)将SocketChannel注册到Selector, 监听OP_ READ操作位;
     * (9)如果轮询的Channel为OP_READ,则说明SocketChannel中有新的就绪的数据包需要读取，则构造ByteBuffer对象，读取数据包:
     * ( 10)如果轮询的Channel为OP_ WRITE，说明还有数据没有发送完成，需要继续发送。
     * 一个简单的NIO服务端程序，如果我们直接使用JDK的NIO类库进行开发，竟然需要经过烦琐的十多步操作才能完成最基本的消息读取和发送，
     * 这也是我们要选择Netty等NIO框架的原因了，下面我们看看使用Netty是如何轻松搞定服务端开发的。
     */


    public static void main(String[] args) throws Exception {
        //1 创建线两个程组
        //一个是用于处理服务器端接收客户端连接的
        //一个是进行网络通信的（网络读写的）
        EventLoopGroup bossGroup = new NioEventLoopGroup();//是用来处理I/O操作的多线程事件循环器
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        //2 创建辅助工具类，用于服务器通道的一系列配置
        ServerBootstrap b = new ServerBootstrap();// 是一个启动NIO服务的辅助启动类。你可以在这个服务中直接使用Channel
        // ，但是这会是一个复杂的处理过程，在很多情况下你并不需要这样做。
        b.group(bossGroup, workerGroup)        //绑定俩个线程组
                .channel(NioServerSocketChannel.class)        //指定NIO的模式
                .option(ChannelOption.SO_BACKLOG, 1024)        //设置tcp缓冲区
                .option(ChannelOption.SO_SNDBUF, 32 * 1024)    //设置发送缓冲大小
                .option(ChannelOption.SO_RCVBUF, 32 * 1024)    //这是接收缓冲大小
                .option(ChannelOption.SO_KEEPALIVE, true)    //保持连接
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel sc) throws Exception {
                        //3 在这里配置具体数据接收方法的处理
                        //pipeline管道，可以添加自己业务逻辑
                        sc.pipeline().addLast(new ServerHandler());//当你的程序变的复杂时，可能你会增加更多的处理类到pipline上，然后提取这些匿名类到最顶层的类上。
                    }
                });

        //4 进行绑定
        ChannelFuture cf1 = b.bind(8765).sync();
        //ChannelFuture cf2 = b.bind(8764).sync();
        //5 等待关闭
        cf1.channel().closeFuture().sync();
        //cf2.channel().closeFuture().sync();
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}

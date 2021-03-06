package com.netty.chat.server;

import com.netty.chat.protocol.IMDecoder;
import com.netty.chat.protocol.IMEncoder;
import com.netty.chat.server.handler.BinaryWebSocketFrameHandler;
import com.netty.chat.server.handler.HttpHandler;
import com.netty.chat.server.handler.SocketHandler;
import com.netty.chat.server.handler.WebSocketHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.apache.log4j.Logger;

import java.io.IOException;

public class ChatServer{
	
	private static Logger LOG = Logger.getLogger(ChatServer.class);
	
	private int port = 80;
	
    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 1024)
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception
                {

                    ChannelPipeline pipeline = ch.pipeline();

                    /** 解析自定义协议 */
                    pipeline.addLast(new IMDecoder());
                    pipeline.addLast(new IMEncoder());
                    pipeline.addLast(new SocketHandler());

                    /** 解析Http请求 静态资源 HttpRequestDecoder和HttpResponseEncoder的一个组合，针对http协议进行编解码 */
                    pipeline.addLast(new HttpServerCodec());
                    //主要是将同一个http请求或响应的多个消息对象变成一个 fullHttpRequest完整的消息对象（将HttpMessage和HttpContents聚合到一个完成的
                    // FullHttpRequest或FullHttpResponse）
                    pipeline.addLast(new HttpObjectAggregator(64 * 1024));
                    //主要用于处理大数据流,比如一个1G大小的文件如果你直接传输肯定会撑暴jvm内存的 ,加上这个handler我们就不用考虑这个问题了（分块向客户端写数据，防止发送大文件时导致内存溢出）
                    pipeline.addLast(new ChunkedWriteHandler());
                    pipeline.addLast(new HttpHandler());

                    /** 解析WebSocket请求 */
                    pipeline.addLast(new WebSocketServerProtocolHandler("/im",null, true, 1024*1024*50));
                    pipeline.addLast(new WebSocketHandler());

                    // 自定义处理器 - 处理 web socket 二进制消息
                    pipeline.addLast(new BinaryWebSocketFrameHandler());

                }
            }); 
            ChannelFuture f = b.bind(this.port).sync();
            System.out.println("服务已启动,监听端口" + this.port);
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
    
    
    public static void main(String[] args) throws IOException{
        new ChatServer().start();
    }
    
}

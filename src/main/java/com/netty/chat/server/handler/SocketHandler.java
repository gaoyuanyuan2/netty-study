package com.netty.chat.server.handler;


import com.netty.chat.processor.MsgProcessor;
import com.netty.chat.protocol.IMMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.log4j.Logger;


public class SocketHandler extends SimpleChannelInboundHandler<IMMessage> {

	private static Logger LOG = Logger.getLogger(SocketHandler.class);
	
	private MsgProcessor processor = new MsgProcessor();

    
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("服务端Handler创建...");
        super.handlerAdded(ctx);
    }
    
    @Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception { // (3)
		Channel client = ctx.channel();
		processor.logout(client);
		System.out.println("Socket Client:" + processor.getNickName(client) + "离开");
	}
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelInactive");
        super.channelInactive(ctx);
    }
    /**
     * tcp链路建立成功后调用
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Socket Client: 有客户端连接："+ processor.getAddress(ctx.channel()));
    }


    /**
     * 异常处理
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("Socket Client: 与客户端断开连接:"+cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, IMMessage msg) throws Exception {
        processor.sendMsg(ctx.channel(), msg);
    }
}

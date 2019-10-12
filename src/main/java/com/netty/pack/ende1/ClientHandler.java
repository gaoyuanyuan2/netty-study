package com.netty.pack.ende1;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;


/**
 * 问题产生的原因有三个，分别如下。
 * (1)应用程序write写入的字节大小大于套接口发送缓冲区大小;
 * (2)进行MSS大小的TCP分段;
 * (3)以太网帧的payload大于MTU进行IP分片。
 * <p>
 * 由于底层的TCP无法理解。上层的业务数据，所以在底层是无法保证数据包不被拆分和重组的，
 * 这个问题只能通过上层的应用协议栈设计来解决，根据业界的主流协议的解决方案，  可以归纳如下。
 * (1)消息定长，例如每个报文的大小为固定长度200字节，如果不够，空位补空格;
 * (2)在包尾增加回车换行符进行分割，例如FTP协议;
 * (3)将消息分为消息头和消息体，消息头中包含表示消息总长度(或者消息体长度)的字段，
 * 通常设计思路为消息头的第一个字段使用int32来表示消息的总长度;
 * (4)更复杂的应用层协议。
 */

public class ClientHandler extends ChannelHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client channel active... ");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            String response = (String) msg;
            System.out.println("Client: " + response);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

}

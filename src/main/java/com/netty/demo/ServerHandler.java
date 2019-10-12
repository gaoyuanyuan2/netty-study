package com.netty.demo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

//网络事件进行读写操作
public class ServerHandler extends ChannelHandlerAdapter {


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("server channel active... ");
    }

    //并没有释放接受到的消息，这是因为当写入的时候Netty已经帮我们释放了
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        //做类型转换，将msg转换成Netty的ByteBuf对象。
        // ByteBuf类似于JDK中的java.nio.ByteBuffer对象，不过它提供了更加强大和灵活的功能。
        // 通过ByteBuf 的readableBytes方法可以获取缓冲区可读的字节数，根据可读的字节数创建byte数组，
        // 通过ByteBuf的readBytes方法将缓冲区中的字节数组复制到新建的byte数组中,最后通过newString构造函数获取请求消息。
        ByteBuf buf = (ByteBuf) msg;//操作方便
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = new String(req, "utf-8");
        System.out.println("Server :" + body);
        String response = "进行返回给客户端的响应：" + body;
        //调用了ChannelHandlerContext 的flush 方法，它的作用是将消息发送队列中的消息写入到SocketChannel中发送给对方。
        // 从性能角度考虑，为了防止频繁地唤醒Selector 进行消息发送，Netty的write 方法并不直接将消息写入SocketChannel中，
        // 调用write方法只是把待发送的消息放到发送缓冲数组中，再通过调用flush 方法，将发送缓冲区中的消息全部写到SocketChannel中。
        ctx.writeAndFlush(Unpooled.copiedBuffer(response.getBytes()));
        //.addListener(ChannelFutureListener.CLOSE);//关闭长连接
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx)
            throws Exception {
        //当发生异常时，关闭ChannelHandlerContext,释放和ChannelHandlerContext相关联的句柄等资源
        System.out.println("读完了");
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable t)
            throws Exception {
        ctx.close();
    }

}

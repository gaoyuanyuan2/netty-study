package com.netty.chat.server.handler;

import com.netty.chat.processor.MsgProcessor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

/**
 * Created by yan on  25/10/2019.
 */
public class BinaryWebSocketFrameHandler extends SimpleChannelInboundHandler<BinaryWebSocketFrame>
{
    private MsgProcessor processor = new MsgProcessor();

    @Override
    protected void messageReceived(ChannelHandlerContext ctx,
                                   BinaryWebSocketFrame msg) throws Exception
    {
        processor.sendMsg(ctx.channel(), msg);
    }
}

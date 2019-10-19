package com.netty.chat.server.handler;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URL;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpHeaderUtil.isKeepAlive;

public class HttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
	private static Logger LOG = Logger.getLogger(HttpHandler.class);
	
	//获取class路径
    private URL baseURL = HttpHandler.class.getProtectionDomain().getCodeSource().getLocation();
    private final String webroot = "webroot";
    
    private File getResource(String fileName) throws Exception{
    		String path = baseURL.toURI() + webroot + "/" + fileName;
        path = !path.contains("file:") ? path : path.substring(5);
        path = path.replaceAll("//", "/");
        return new File(path);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        Channel client = ctx.channel();
        LOG.info("Client:"+client.remoteAddress()+"异常");
        // 当出现异常就关闭连接
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, FullHttpRequest request)
            throws Exception {
        String uri = request.uri();

        RandomAccessFile file = null;
        try{
            String page = uri.equals("/") ? "chat.html" : uri;
            file =	new RandomAccessFile(getResource(page), "r");
        }catch(Exception e){
            ctx.fireChannelRead(request.retain());
            return;
        }

        HttpResponse response = new DefaultHttpResponse(request.protocolVersion(), HttpResponseStatus.OK);
        String contextType = "text/html;";
        if(uri.endsWith(".css")){
            contextType = "text/css;";
        }else if(uri.endsWith(".js")){
            contextType = "text/javascript;";
        }else if(uri.toLowerCase().matches("(jpg|png|gif)$")){
            String ext = uri.substring(uri.lastIndexOf("."));
            contextType = "image/" + ext;
        }
        response.headers().set(CONTENT_TYPE, contextType + "charset=utf-8;");

        boolean keepAlive = isKeepAlive(request);

        if (keepAlive) {
            response.headers().set(CONTENT_LENGTH, file.length()+"");
            response.headers().set(CONNECTION, KEEP_ALIVE);
        }
        ctx.write(response);

        ctx.write(new DefaultFileRegion(file.getChannel(), 0, file.length()));
//        ctx.write(new ChunkedNioFile(file.getChannel()));

        ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        if (!keepAlive) {
            future.addListener(ChannelFutureListener.CLOSE);
        }

        file.close();
    }
}


package com.netty.catalina.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.List;
import java.util.Map;

public class GPRequest {
	
	private ChannelHandlerContext ctx;
	private HttpRequest request;
	
	public GPRequest(ChannelHandlerContext ctx, HttpRequest request){
		this.ctx = ctx;
		this.request = request;
	}
	
	public String getUri(){
		return request.uri();
	}
	
	public String getMethod(){
		return request.method().toString();
	}
	
	public Map<String,List<String>> getParameters(){
		 QueryStringDecoder decoderQuery = new QueryStringDecoder(request.uri());
		return decoderQuery.parameters();  
	}
	
    /** 
     * <pre> 
     * @param name 
     * @param decoderQuery 
     * @return 
     * </pre> 
     */  
    public String getParameter(String name) {
        Map<String, List<String>> params = getParameters();  
        List<String> param = params.get(name);
        if(null != param){
        		return param.get(0);
        }else{
        		return null;
        }
    }  
	
}

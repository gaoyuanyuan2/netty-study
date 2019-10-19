package com.netty.catalina.servlets;


import com.netty.catalina.http.GPRequest;
import com.netty.catalina.http.GPResponse;
import com.netty.catalina.http.GPServlet;

public class FirstServlet extends GPServlet {

	
	@Override
	public void doGet(GPRequest request, GPResponse response) {
		doPost(request, response);
	}

	
	@Override
	public void doPost(GPRequest request, GPResponse response) {
		String param = "name";  
	    String str = request.getParameter(param);  
	    response.write(param + ":" + str,200);
	}
	
}

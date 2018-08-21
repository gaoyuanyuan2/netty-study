package com.netty.io.socket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketClient {
    public static void main(String[] args) {
        Socket socket =null;
        try {
            socket = new Socket("localhost",8123);
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()),true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer.println("客户端发送消息");
            while (true){
                String str = reader.readLine();
                if(str==null){
                    break;
                }
                System.out.println("客户端接收消息："+str);
            }
            writer.close();
            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

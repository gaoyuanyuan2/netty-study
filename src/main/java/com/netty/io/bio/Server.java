package com.netty.io.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
/*
 * 第一，在任何时候都可能有大量的线程处于休眠状态，只是等待输入或者输出数据就绪，这可能算是一种资源浪费。
 * 第二，需要为每个线程的调用栈都分配内存，其默认值大小区间为64 KB到1 MB，具体取决于操作系统。
 * 第三，即使Java虚拟机（JVM）在物理上可以支持非常大数量的线程，但是远在到达该极限之前，上下文切换所带来的开销就会带来麻烦
 * */

public class Server {

    final static int PROT = 8765;

    public static void main(String[] args) {

        ServerSocket server = null;
        try {
            server = new ServerSocket(PROT);//创建一个新的ServerSocket，用以监听指定端口上的连接请求
            System.out.println(" server start .. ");
            //进行阻塞
            Socket socket = server.accept();
            //新建一个线程执行客户端的任务
            new Thread(new ServerHandler(socket)).start();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (server != null) {
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            server = null;
        }


    }


}

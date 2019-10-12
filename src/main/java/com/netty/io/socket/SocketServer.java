package com.netty.io.socket;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

//记得flush
public class SocketServer {
    public static void main(String[] args) throws IOException {
        ServerSocket socketServer = null;
        try {
            socketServer = new ServerSocket(8123);
            while (true) {
                Socket socket = socketServer.accept();
                new Thread(() -> {
                    try {
                        PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                        BufferedReader bufferedReader =
                                new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        while (true) {
                            String str = bufferedReader.readLine();
                            if (str == null) {
                                break;
                            }
                            System.out.println("服务端收到消息：" + str);
                            printWriter.println("服务端发送消息");
                            printWriter.flush();
                        }
                        printWriter.close();
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socketServer != null) {
                socketServer.close();
            }
        }
    }
}

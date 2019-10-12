package com.netty.io.multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;


public class MulticastClient2 {

    public static void main(String[] args) throws UnknownHostException {
        InetAddress group = InetAddress.getByName("224.5.6.7");

        try {
            MulticastSocket socket = new MulticastSocket(8888);

            socket.joinGroup(group);  //加到指定的组里面

            byte[] buf = new byte[246];
            while (true) {
                DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
                socket.receive(msgPacket);

                String msg = new String(msgPacket.getData());
                System.out.println("接收数据：" + msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

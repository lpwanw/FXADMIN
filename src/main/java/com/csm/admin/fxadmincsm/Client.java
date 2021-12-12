package com.csm.admin.fxadmincsm;


import com.csm.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client
{
    public final static int ServerPort = 1234;
    public static ObjectOutputStream dos;
    public static ObjectInputStream dis;
    public static InetAddress ip;
    public static Socket s;
    public static void main(String args[]) throws UnknownHostException, IOException
    {
        Scanner scn = new Scanner(System.in);

        // getting localhost ip
        InetAddress ip = InetAddress.getByName("localhost");

        // establish the connection
        Socket s = new Socket(ip, ServerPort,ip,3123);

        // obtaining input and out streams
        ObjectOutputStream dos = new ObjectOutputStream(s.getOutputStream());
        ObjectInputStream dis = new ObjectInputStream(s.getInputStream());


        // sendMessage thread
        Thread sendMessage = new Thread(new Runnable()
        {
            @Override
            public void run() {
                while (true) {

                    // read the message to deliver.
                    String msg = scn.nextLine();
                    try {
                        // write on the output stream
                        Message object = new Message();
                        object.command= 0;
                        object.data = "data";
                        object.toId = msg;
                        dos.writeObject(object);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // readMessage thread
        Thread readMessage = new Thread(new Runnable()
        {
            @Override
            public void run() {

                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        // read the message sent to this client
                        Message msg = (Message) dis.readObject();
                        System.out.println(msg.command);
                    } catch (IOException e) {
                        Thread.currentThread().interrupt();
                        return;
                    } catch (ClassNotFoundException e){
                        e.printStackTrace();
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
        });
        sendMessage.start();
        readMessage.start();

    }
}

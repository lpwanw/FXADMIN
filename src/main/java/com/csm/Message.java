package com.csm;


import java.io.Serial;
import java.io.Serializable;

public class Message implements Serializable {
    @Serial
    private static final long serialVersionUID = 6529685098267757690L;
    public static final int TAKE_SCREEN_SHOT = 1;
    public static final int LOGOUT = 999;
    public static final int GET_LIST_USER = 2;
    public static final int GET_OS_INFO = 3;
    public static final int innit = 0;
    public static final int LOG_OUT = 4;
    public static final int SHUT_DOWN = 5;
    public static final int GET_CLIPBOARD = 6;
    public static final int GET_LIST_PROCESS = 7;
    public static final int KILL_PROCESS = 8;
    public static final int GET_DISK = 9;
    public static final int GET_RAM = 10;
    public static final int GET_CPU = 11;
    public static final int GET_KEYLOG = 12;
    public static final int OPEN_SOCKET_CPU = 13;
    public static final int SEND_PORT = 14;
    public String data;
    public String toId;
    public int command;
}

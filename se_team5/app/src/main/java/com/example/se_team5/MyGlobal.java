package com.example.se_team5;

/* 서버 주소 전역 객체 */
public class MyGlobal {
    static private String server_address = "http://56534799.ngrok.io";
    static public String getData()
    {
        return server_address;
    }

}

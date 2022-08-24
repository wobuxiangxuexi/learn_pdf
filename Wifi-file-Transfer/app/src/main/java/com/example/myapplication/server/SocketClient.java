package com.example.myapplication.server;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class SocketClient {

    private  int port;//端口
    private String site;//ip
    private Context mContext;
    private Socket clientSocket;
    private boolean isClient;
    private static final String TAG_LOG = "SocketClient_debug";
    private OutputStream out;
    private InputStream mInputStream;
    private String getStr;
    public static Handler ClientHandler;
    public static Integer i = 0;

    public SocketClient(Context context, String site, int port){
        this.mContext = context;
        this.site = site;
        this.port = port;
    }


    public void openClientThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    clientSocket = new Socket(site, port);

                    if(clientSocket != null){
                        isClient = true;
                       // getOutStream();
                        Log.d(TAG_LOG,"getOutString() success");
                       // getInStrram();
                    }else{
                        isClient = false;
                      //  Toast.makeText(mContext,"网络连接失败 openClientFail",Toast.LENGTH_LONG).show();
                        Log.d(TAG_LOG,"没连上");
                    }

                } catch (IOException e) {
                    isClient = false;
                    e.printStackTrace();

                }
            }
        }).start();
    }

    private void getOutStream() {
        Log.d(TAG_LOG,"getOutString() invoked");
        try {
            out = clientSocket.getOutputStream();
            Log.d(TAG_LOG,"getOutString() success");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG_LOG,"getOutString() fail");
        }

    }

    public void getInStrram(){
        Log.d(TAG_LOG,"getInstring() invoked");
        while(isClient){
            try {
                mInputStream = clientSocket.getInputStream();
                byte[] bt = new byte[100];
                mInputStream.read(bt);
                getStr = new String(bt, "UTF-8");


            } catch (IOException e) {
                e.printStackTrace();
            }

            if ( getStr != null){
                Message msg = new Message();
                msg.obj = getStr;
                ClientHandler.sendMessage(msg);

            }
        }
    }

    public void sendMessage(InputStream is,String filename) {


        //    Log.d(TAG_LOG,"sendMsg str:" + str);
        new Thread(new Runnable() {
            @Override
            public void run() {


                    try {
                        clientSocket = new Socket(site, port);
                        getOutStream();
                        out.write(((filename + "#").getBytes()));
                        Log.d(TAG_LOG, filename);
                        byte[] buf = new byte[1024];
                        int len;
                        while ((len = is.read(buf)) != -1) {
                            out.write(buf, 0, len);//将文件循环写入输出流
                        }
                        //returnMessage("文件发送成功" + i.toString() );
                      // out.close();
                        //文件发送完毕
                        clientSocket.shutdownOutput();
                        returnMessage("文件发送成功" + i.toString() );


                        //获取从服务端反馈的信息
                        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        String serverBack = in.readLine();
                        returnMessage(serverBack);
                        Log.d("TAG", serverBack);
                        //资源关闭
                        clientSocket.close();
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                 else {
//
//                    isClient = false;
//                    Log.d(TAG_LOG, "client send message fial ");
//
//                }
            }
        }).start();
    }

    public void returnMessage(String str) {
        Message msg = new Message();
        msg.obj = str;
        ClientHandler.sendMessage(msg);
    }
}

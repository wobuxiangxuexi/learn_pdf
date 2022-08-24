package com.example.myapplication.server;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {

    private boolean isClint;
    private ServerSocket server;
    private InputStream in = null;
    private Socket socket;
    private String str;
    public static Handler ServerHandler;
    private static final String TAG_LOG = "SocketServer_debug";
    private String publicPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
   // private String privatePath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString();


    public String getPublicPath() {
        return publicPath;
    }

    public SocketServer(int port) {

        try {
            server = new ServerSocket(port);
           // isClint = true;
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void beginListen() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                        while(true) {
                            try {
                                socket = server.accept();
                                in = socket.getInputStream();
                                if (in == null) {
                                    Log.d(TAG_LOG, "没取到socket的输入流");
                                }

                                int content;
                                //装载文件名的数组
                                byte[] c = new byte[1024];
                                //解析流中的文件名,也就是开头的流
                                for (int i = 0; (content = in.read()) != -1; i++) {
                                    //表示文件名已经读取完毕
                                    if (content == '#') {
                                        break;
                                    }
                                    c[i] = (byte) content;
                                }
                                String FileName = new String(c, "utf-8").trim();
                                Log.d(TAG_LOG, FileName);
                                OutputStream saveFile = new FileOutputStream(publicPath + File.separatorChar + FileName);
                                Log.d(TAG_LOG, publicPath + File.separatorChar + FileName);
                                if (saveFile == null) {
                                    Log.d(TAG_LOG, "没获取到文件的输出流");
                                }
                                byte[] buf = new byte[1024];
                                int len = 0;
                                Integer i = 0;
                                //判断是否读到文件末尾
                                returnMessage("开始接收文件……");
                                Log.d(TAG_LOG, "开始获取文件");
                                while ((len = in.read(buf)) != -1) {

                                    saveFile.write(buf, 0, len);
                                    i++;

                                    Log.d(TAG_LOG, i.toString());
                                }
                                Log.d(TAG_LOG, "文件接受完毕");
                                returnMessage("接收完毕，文件路径是：" + publicPath + File.separatorChar + System.currentTimeMillis() + FileName);
                                saveFile.close();
//                                in.close();

                                //给服务端发送收到的消息
                                OutputStream outputStream = socket.getOutputStream();
                                outputStream.write("文件发送成功".getBytes());
                                outputStream.flush();
                                outputStream.close();
                                socket.close();
                                Log.d(TAG_LOG, "通道关闭");
                            } catch (IOException e) {
                                e.printStackTrace();
                                System.out.println(socket.isClosed());
                            }
                        }

            }
        }).start();

    }

    public void returnMessage(String str) {
        Message msg = new Message();
        msg.obj = str;
        ServerHandler.sendMessage(msg);
    }

//    /**
//     * 发送消息
//     * @param chat
//     */
//    public void sendMessage(final String chat) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    PrintStream out = new PrintStream(socket.getOutputStream());
//                    out.print(chat);
//                    out.flush();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }
}

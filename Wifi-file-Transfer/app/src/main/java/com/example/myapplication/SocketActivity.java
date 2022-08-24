package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.server.SocketServer;
import com.example.myapplication.tool.QRCutil;
import com.example.myapplication.tool.ToastUtil;

public class SocketActivity extends AppCompatActivity implements View.OnClickListener {

    private Button show_ip;
    private Button send_message;
    private TextView show_message;
    private Button open_file;
    private SocketServer server;
    private ImageView show;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket);
        initView();
        getper();

        server = new SocketServer(7777);
        ToastUtil.show(this,"准备接收文件");
        server.beginListen();
        SocketServer.ServerHandler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                show_message.setText(msg.obj.toString());
                ToastUtil.show(SocketActivity.this,msg.obj.toString());

            }
        };

    }




    public void initView() {
        show_ip = (Button) findViewById(R.id.show_ip);
     //   send_message = (Button) findViewById(R.id.send_message);
        show = (ImageView) findViewById(R.id.show);
        show_message = (TextView) findViewById(R.id.show_message);
        open_file = (Button) findViewById(R.id.open_file);
        show_ip.setOnClickListener(this);
        //send_message.setOnClickListener(this);
        open_file.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.show_ip:
                show_message.setText("本机ip：" + getIpAddress());
                show.setImageBitmap(QRCutil.createShow(getIpAddress(), 400, 400, "UTF-8", "H", "5", Color.BLACK, Color.WHITE));


                break;

//            case R.id.send_message:
//                server.sendMessage(open_file.getText().toString().trim());
//                break;

            case R.id.open_file:{

                Uri uri = Uri.parse("content://com.android.externalstorage.documents/document/primary:Download");
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");//无类型限制
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);
                startActivity(intent);

            }



            //text
        }


    }
    /**
     *
     * @return ip地址
     */
    private String getIpAddress(){
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        return ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "." +(ipAddress >> 16 & 0xff)+ "." + (ipAddress >> 24 & 0xff));
    }
    public void getper() {
        // 请求读写权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

    }

}
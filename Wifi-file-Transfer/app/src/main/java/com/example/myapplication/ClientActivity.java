package com.example.myapplication;

import static com.example.myapplication.tool.util.getFileType;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.server.SocketClient;
import com.example.myapplication.tool.ToastUtil;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class ClientActivity extends AppCompatActivity {

    private Button client_sendMessage;
    private TextView client_ip;
    private TextView client_textView;
    private EditText client_editText;
    private SocketClient client;
  //  private String publicPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
  //  private String privatePath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString();

    private InputStream inputStream;
    private Button open_scan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        initView();
        client_ip.setText("本机ip:" + getLocal());

       // client = new SocketClient(this, "192.168.50.11", 7777);
        //client.openClientThread();

        client_sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client = new SocketClient(ClientActivity.this, client_editText.getText().toString(), 7777);
                Intent intent = new Intent(Intent.ACTION_PICK,null);
                intent.setType("*/*");
                startActivityForResult(intent,100);
            }
        });

        open_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                IntentIntegrator intentIntegrator = new IntentIntegrator(ClientActivity.this);
                intentIntegrator.setBeepEnabled(true);
                /*设置启动我们自定义的扫描活动，若不设置，将启动默认活动*/
                intentIntegrator.setCaptureActivity(ScanActivity.class);
                intentIntegrator.initiateScan();
            }
        });





        SocketClient.ClientHandler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                client_textView.setText(msg.obj.toString());
                ToastUtil.show(ClientActivity.this,msg.obj.toString());
            }
        };


    }

    private void initView() {
        client_sendMessage = (Button) findViewById(R.id.client_sendMessage);
        client_ip = (TextView) findViewById(R.id.client_ip);
        client_textView = (TextView) findViewById(R.id.client_textView);
        client_editText = (EditText) findViewById(R.id.client_editText);
        open_scan = (Button) findViewById(R.id.open_scan);
    }

    /**
     *
     * @return ip地址
     */
    private String getLocal(){
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        return ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "." +(ipAddress >> 16 & 0xff)+ "." + (ipAddress >> 24 & 0xff));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
      //  super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                String fileType = getFileType(data.getData().toString());
                ContentResolver contentResolver = this.getContentResolver();
                String fileName = System.currentTimeMillis() + fileType;
                try {
                    inputStream = contentResolver.openInputStream(uri);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                client.sendMessage(inputStream,fileName);
                ToastUtil.show(this,"正在发送……");
            }
        }
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                client_editText.setText(result.getContents());
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                client_editText.setText(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
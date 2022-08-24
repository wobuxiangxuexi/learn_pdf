package com.luckystar.test;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private TextView show_uri;
    private Button choose_file;
    private Button getpermission;
    private String privatePath;
    private String publicPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        choose_file = findViewById(R.id.choose_file);
        show_uri = (TextView) findViewById(R.id.show_uri);
        getpermission = findViewById(R.id.getpermission);

        publicPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        privatePath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString();

        String desc = "系统公共路径" + publicPath + "\n\n私有储存路径" + privatePath;

        getper();
//        show_uri.setText(desc);

        choose_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(Intent.ACTION_PICK,null);
////                intent.setType("video/*;image/*");
//                intent.setType("*/*");
//                startActivityForResult(intent,100);


                show_uri.setText(desc);
                File file1=new File(publicPath);
                if(!file1.exists()){
                    file1.mkdir();
                    Log.d("debug","1");
                }
//                File file = new File(publicPath + File.separatorChar + "123.txt");
//                if(file != null){
//                    Log.d("debug","1");
//                }else{
//                    Log.d("debug","0");
//                }
//                try {
//                    OutputStream out = new FileOutputStream(file) ;
//                    String str = "Hello World!!!" ;
//                    byte b[] = str.getBytes() ;
//                    out.write(b) ;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }





//                try {
//                    sendFile(path1);
//                    sendFile(path2);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

            }

        });


        getpermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //getper();
                Uri uri = Uri.parse("content://com.android.externalstorage.documents/document/primary:Download");
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");//无类型限制
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);
                startActivity(intent);


            }
        });

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

    @Override
    protected void onActivityResult  (int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100){
            if(resultCode == RESULT_OK){
               // String fileUri = data.getData().toString();
                String fileType = getFileType(data.getData().toString());

                Uri uri = data.getData();
                   // String path = uri.getPath();

                   // show_uri.setText(path);
                try {
                    //show_uri.setText(path);
//                    InputStream inputStream = new FileInputStream(publicPath + File.separatorChar + "6fb111adef299d4b.jpg");
                    ContentResolver contentResolver = this.getContentResolver();
                    InputStream inputStream = contentResolver.openInputStream(uri);
                    show_uri.setText("传输开始2");
                    OutputStream outputStream = new FileOutputStream(publicPath + File.separatorChar + System.currentTimeMillis() + fileType);
                    show_uri.setText("传输开始3");
                    byte[] buf = new byte[1024];
                    int len;
                    show_uri.setText("传输开始");
                    //判断是否读到文件末尾
                    while ((len = inputStream.read(buf)) != -1) {
                        outputStream.write(buf, 0, len);//将文件循环写入输出流
                    }
                    inputStream.close();
                    outputStream.close();
                    show_uri.setText("传输完成:" + publicPath + File.separatorChar + System.currentTimeMillis() + fileType);


                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            }
        }

    public void sendFile(String path) throws Exception {

        OutputStream out = new FileOutputStream(path);
        //将文件名写在流的头部以#分割
        out.write("aaa".getBytes());
        out.close();

    }

    public String getFileType(String str){
        String fileType = "";
        int a = str.lastIndexOf(".");
        fileType = str.substring(a, str.length());
        return fileType;
    }

}


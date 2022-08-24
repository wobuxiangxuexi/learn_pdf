package com.example.myapplication;

import static android.os.SystemClock.sleep;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {

    private static String data = "默认信息";
    private boolean running = false;




    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();



        running = true;
        new Thread(){

            @Override
            public void run() {
                int i = 0;
                while (running){
                    ++i;
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String str = i + "" +data;
                    System.out.println(str);


                    if (callback != null){
                        callback.onDataChange(str);
                    }

                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                super.run();
            }
        }.start();


    }

    @Override
    public IBinder onBind(Intent intent) {
        return new mBinder();
    }

    public class mBinder extends Binder{

        public void setDataString(String str){
            MyService.this.data = str;
        }

        public MyService getService(){
            return MyService.this;
        }

    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        data = intent.getStringExtra("data");
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        running = false;
        super.onDestroy();

    }


    ServiceCallback callback = null;

    public  void setCallback(ServiceCallback callback) {
        this.callback = callback;
    }

    public ServiceCallback getCallback() {
        return callback;
    }

    public  interface ServiceCallback{

        void onDataChange(String data);

    }

    public void test(){

                Integer i = 0;
                while(i != 100){
                    sleep(1000);
                    Log.d("test",i.toString());
                    i++;
                }

    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
}

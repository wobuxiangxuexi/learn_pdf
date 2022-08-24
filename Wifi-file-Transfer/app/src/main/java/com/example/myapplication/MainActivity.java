package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText editText;
    private MyService.mBinder binder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button c = (Button) findViewById(R.id.service);
        Button d = (Button) findViewById(R.id.client);
        c.setOnClickListener(this);
        d.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.service:
                //text
                Intent it = new Intent(MainActivity.this,SocketActivity.class);
                startActivity(it);
                break;
            case R.id.client:
                startActivity(new Intent(MainActivity.this,ClientActivity.class));
                break;


        }
    }





}
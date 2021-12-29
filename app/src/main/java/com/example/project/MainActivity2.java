package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Bundle bundle = getIntent().getExtras();

        String q = bundle.getString("q");
        String lan1 = bundle.getString("lan1");
        String lan2 = bundle.getString("lan2");

    }
}
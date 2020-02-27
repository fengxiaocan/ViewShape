package com.app.nbtest;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.app.vshape.ShapeHelper;


public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        ShapeHelper.registerActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

}

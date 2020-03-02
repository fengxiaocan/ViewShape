package com.app.nbtest;

import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.app.vshape.OnViewShapeInterceptorTemp;
import com.app.vshape.ShapeHelper;


public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        ShapeHelper.registerActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Log.e("noah","MainActivity resid = "+R.color.colorAccent);
//        Log.e("noah","MainActivity resid = "+R.color.colorPrimaryDark);
    }

}

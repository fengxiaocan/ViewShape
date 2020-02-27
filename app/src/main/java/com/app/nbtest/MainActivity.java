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

    //
    //    @Nullable
    //    @Override
    //    public View onCreateView(@Nullable View parent,@NonNull String name,@NonNull Context context,
    //            @NonNull AttributeSet attrs)
    //    {
    //        View view = super.onCreateView(parent,name,context,attrs);
    //        ShapeHelper.onCreateShape(view,context,attrs);
    //        return view;
    //    }
    //
    //    @Nullable
    //    @Override
    //    public View onCreateView(@NonNull String name,@NonNull Context context,@NonNull AttributeSet attrs){
    //        View view = super.onCreateView(name,context,attrs);
    //        ShapeHelper.onCreateShape(view,context,attrs);
    //        return view;
    //    }
}

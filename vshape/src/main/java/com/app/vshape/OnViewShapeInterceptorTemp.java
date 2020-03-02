package com.app.vshape;


import android.content.res.ColorStateList;
import android.content.res.TypedArray;

import androidx.annotation.StyleableRes;

public class OnViewShapeInterceptorTemp implements OnViewShapeInterceptor{
    private static OnViewShapeInterceptorTemp instance;

    public static synchronized OnViewShapeInterceptorTemp getInstance(){
        if(instance == null){
            instance = new OnViewShapeInterceptorTemp();
        }
        return instance;
    }

    @Override
    public ColorStateList getColorStateList(TypedArray array,@StyleableRes int index){
        return array.getColorStateList(index);
    }

    @Override
    public int getColor(TypedArray array,@StyleableRes int index,int defValue){
        return array.getColor(index,defValue);
    }

    @Override
    public int getInt(TypedArray array,@StyleableRes int index,int defValue){
        return array.getInt(index,defValue);
    }

    @Override
    public int getDimensionPixelSize(TypedArray array,@StyleableRes int index,int defValue){
        return array.getDimensionPixelSize(index,defValue);
    }

    @Override
    public float getDimension(TypedArray array,@StyleableRes int index,float defValue){
        return array.getDimension(index,defValue);
    }

    @Override
    public float getFloat(TypedArray array,@StyleableRes int index,float defValue){
        return array.getFloat(index,defValue);
    }

    public int getResourceId(TypedArray array,@StyleableRes int index){
        return array.getResourceId(index,0);
    }
}

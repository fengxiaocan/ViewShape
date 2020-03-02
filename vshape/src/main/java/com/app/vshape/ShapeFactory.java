package com.app.vshape;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class ShapeFactory implements LayoutInflater.Factory2{
    private final AppCompatDelegate appCompatDelegate;
    private LayoutInflater.Factory factory;
    private LayoutInflater.Factory2 factory2;
    private AppCompatViewInflater compatViewInflater;

    public ShapeFactory setFactory2(LayoutInflater.Factory2 factory2){
        if(factory2 != null){
            this.factory2 = factory2;
        }
        return this;
    }

    public ShapeFactory setFactory(LayoutInflater.Factory factory){
        if(factory != null){
            this.factory = factory;
        }
        return this;
    }

    public ShapeFactory(){
        appCompatDelegate = null;
    }

    public ShapeFactory(Activity activity){
        appCompatDelegate = null;
        activity.getLayoutInflater().setFactory2(this);
    }

    public ShapeFactory(AppCompatActivity activity){
        appCompatDelegate = activity.getDelegate();
        activity.getLayoutInflater().setFactory2(this);
    }

    @Override
    public View onCreateView(String name,Context context,AttributeSet attrs){
        return onCreateView(null,name,context,attrs);
    }

    @Nullable
    @Override
    public View onCreateView(@Nullable View parent,@NonNull String name,@NonNull Context context,
            @NonNull AttributeSet attrs)
    {
        View view = null;
        if(factory2 != null){
            view = factory2.onCreateView(parent,name,context,attrs);
        } else if(factory != null){
            view = factory.onCreateView(name,context,attrs);
        } else if(appCompatDelegate != null){
            view = appCompatDelegate.createView(parent,name,context,attrs);
        }
        if(view == null){
            if(compatViewInflater == null){
                compatViewInflater = new AppCompatViewInflater();
            }
            view = compatViewInflater.createView(parent,name,context,attrs);
        }
        if(ShapeHelper.onCreateShape(view,attrs)){
            ShapeHelper.applyDrawableToView(view,attrs);
        }
        return view;
    }
}


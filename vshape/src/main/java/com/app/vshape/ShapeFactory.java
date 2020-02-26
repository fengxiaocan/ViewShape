package com.app.vshape;

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

    public ShapeFactory setFactory2(LayoutInflater.Factory2 factory2){
        this.factory2 = factory2;
        return this;
    }

    public ShapeFactory setFactory(LayoutInflater.Factory factory){
        this.factory = factory;
        return this;
    }

    public ShapeFactory(){
        appCompatDelegate = null;
    }

    public ShapeFactory(AppCompatActivity activity){
        appCompatDelegate = activity.getDelegate();
        setFactory(activity.getLayoutInflater().getFactory());
        setFactory2(activity.getLayoutInflater().getFactory2());
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
        View view;
        if(factory2 != null){
            view = factory2.onCreateView(parent,name,context,attrs);
        } else if(factory != null){
            view = factory.onCreateView(name,context,attrs);
        } else if(appCompatDelegate != null){
            view = appCompatDelegate.createView(parent,name,context,attrs);
        } else{
            view = createView(name,context,attrs);
        }
        ShapeHelper.onCreateShape(view,context,attrs);
        return view;
    }

    private View createView(String name,Context context,AttributeSet attrs){
        View view = null;
        try{
            if(- 1 == name.indexOf('.')){    //不带".",说明是系统的View
                if("View".equals(name)){
                    view = LayoutInflater.from(context)
                                         .createView(name,"android.view.",attrs);
                }
                if(view == null){
                    view = LayoutInflater.from(context)
                                         .createView(name,"android.widget.",attrs);
                }
                if(view == null){
                    view = LayoutInflater.from(context)
                                         .createView(name,"android.webkit.",attrs);
                }
            } else{    //带".",说明是自定义的View
                view = LayoutInflater.from(context)
                                     .createView(name,null,attrs);
            }
        } catch(Exception e){
            view = null;
        }
        return view;
    }

}


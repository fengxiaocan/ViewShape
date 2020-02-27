package com.app.vshape;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.xmlpull.v1.XmlPullParser;

final class ViewShapeInflater extends LayoutInflater{
    private static final String[] sClassPrefixList = {"android.widget.","android.webkit.","android.app."};

    ViewShapeInflater(Context newContext,LayoutInflater original){
        super(original,newContext);
        ShapeFactory factory2 = new ShapeFactory();
        factory2.setFactory2(getFactory2());
        setFactory2(factory2);
    }

    @Override
    public LayoutInflater cloneInContext(Context newContext){
        return new ViewShapeInflater(newContext,this);
    }

    @Override
    public void setFactory(Factory factory){
        if(getFactory() != null){
            ShapeFactory f = (ShapeFactory)getFactory2();
            if(f == null){
                super.setFactory2(f = new ShapeFactory());
            }
            f.setFactory(factory);
        } else{
            super.setFactory(factory);
        }
    }

    @Override
    public void setFactory2(Factory2 factory2){
        ShapeFactory f = (ShapeFactory)getFactory2();
        if(f == null){
            super.setFactory2(f = new ShapeFactory());
        }
        f.setFactory2(factory2);
    }

    @Override
    public View inflate(XmlPullParser parser,ViewGroup root,boolean attachToRoot){
        if(getFactory2() == null){
            super.setFactory2(new ShapeFactory());
        }
        return super.inflate(parser,root,attachToRoot);
    }

    @Override
    protected View onCreateView(String name,AttributeSet attrs) throws ClassNotFoundException{
        for(String prefix: sClassPrefixList){
            try{
                View view = createView(name,prefix,attrs);
                if(view != null){
                    return view;
                }
            } catch(ClassNotFoundException e){
                // In this case we want to let the base class take a crack
                // at it.
            }
        }
        return super.onCreateView(name,attrs);
    }
}
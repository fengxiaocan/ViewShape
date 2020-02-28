package com.app.vshape;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

@Deprecated
final class ViewShapeInflater extends LayoutInflater{
    private static final String[] sClassPrefixList = {"android.widget.","android.webkit.","android.app."};

    ViewShapeInflater(Context newContext,LayoutInflater original){
        super(original,newContext);
        Factory2 factory2 = original.getFactory2();
        super.setFactory2(new ShapeFactory().setFactory2(factory2));
    }

    @Override
    public LayoutInflater cloneInContext(Context newContext){
        return new ViewShapeInflater(newContext,this);
    }

    @Override
    public void setFactory(Factory factory){
        ShapeFactory f = (ShapeFactory)getFactory2();
        f.setFactory(factory);
    }

    @Override
    public void setFactory2(Factory2 factory2){
        ShapeFactory f = (ShapeFactory)getFactory2();
        f.setFactory2(factory2);
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

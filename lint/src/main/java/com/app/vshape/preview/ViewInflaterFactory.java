package com.app.vshape.preview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import java.lang.reflect.Constructor;
import java.util.HashMap;

public class ViewInflaterFactory{
    //缓存已经找到的控件
    protected static final Class<?>[] sConstructorSignature=new Class[]{Context.class,AttributeSet.class};
    protected static final HashMap<String,Constructor<? extends View>> sConstructorMap=new HashMap<>();
    private static final String sAutoNs="http://schemas.android.com/apk/res-auto";
    protected static final String[] attrs={
            "shapeType","shapeGradientType","shapeGradientAngle","shapeSolidSize","shapeSolidWidth","shapeSolidHeight",
            "shapeSolidColor","shapeStrokeWidth","shapeStrokeColor","shapeStokeDashWidth","shapeStokeDashGap",
            "shapeCornerRadius","shapeCornerTopLeftRadius","shapeCornerTopRightRadius","shapeCornerBottomLeftRadius",
            "shapeCornerBottomRightRadius","shapeGradientStartColor","shapeGradientCenterColor","shapeGradientEndColor",
            "shapeGradientRadius","shapeGradientCenterX","shapeGradientCenterY"};

    public static View createView(String name,Context context,AttributeSet attrs){
        Constructor<? extends View> constructor=sConstructorMap.get(name);
        if(constructor==null){
            try{
                Class<? extends View> aClass=context.getClassLoader().loadClass(name).asSubclass(View.class);
                constructor=aClass.getConstructor(sConstructorSignature);
                constructor.setAccessible(true);
                sConstructorMap.put(name,constructor);
            } catch(Throwable e){
                e.printStackTrace();
            }
        }
        if(constructor!=null){
            try{
                return constructor.newInstance(context,attrs);
            } catch(Throwable e){
                e.printStackTrace();
            }
        }
        return null;
    }

    public static boolean isSupportedShareAttr(AttributeSet attr){
        for(String name: attrs){
            if(attr.getAttributeValue(sAutoNs,name)!=null){
                return true;
            }
        }
        return false;
    }
}

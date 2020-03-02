/*
 * Copyright (C) 2019 Cricin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.app.vshape.preview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.ide.common.rendering.api.LayoutlibCallback;
import com.android.layoutlib.bridge.Bridge;
import com.android.layoutlib.bridge.android.BridgeContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * A view factory takes responsibility of view creation, if the view
 * is created, ViewShape will create a drawable for it if available.
 */
final class ViewFactory implements LayoutInflater.Factory2{
    private static final String[] sClassPrefixList =
            {"android.widget.","android.webkit.","android.app.","android.view."};
    private static final Set<String> APPCOMPAT_VIEWS = new HashSet<>(Arrays.asList("LinearLayout",
            "RelativeLayout",
            "FrameLayout",
            "TextView",
            "ImageView",
            "Button",
            "EditText",
            "Spinner",
            "ImageButton",
            "CheckBox",
            "RadioButton",
            "CheckedTextView",
            "AutoCompleteTextView",
            "MultiAutoCompleteTextView",
            "RatingBar",
            "SeekBar"));

    private static final String sAutoNs = "http://schemas.android.com/apk/res-auto";
    private static boolean sExceptionCaught = false; //only log exception once, don't mess up log file
    private Set<String> mFailedAppCompatViews = new HashSet<>();
    private static final Class<?>[] sConstructorSignature = {Context.class,AttributeSet.class};
    private static final Object[] sConstructorArgs = new Object[2];
    private static final String sLegacyAppCompatViewPrefix = "android.support.v7.widget.AppCompat";
    private static final String sAndroidxViewPrefix = "androidx.appcompat.widget.AppCompat";

    private LayoutInflater mDelegate;
    private boolean mLoadAppCompatViews;
    private LayoutlibCallback mLayoutLibCallback;
    private boolean mUseAndroidx;

    ViewFactory(LayoutInflater inflater,LayoutlibCallback layoutlibCallback){
        this.mDelegate = inflater;
        this.mLayoutLibCallback = layoutlibCallback;
        this.mLoadAppCompatViews = getContext().isAppCompatTheme();
        try{
            this.mUseAndroidx = layoutlibCallback.hasAndroidXAppCompat();
        } catch(Throwable ignored){
        }
    }

    private BridgeContext getContext(){
        return (BridgeContext)mDelegate.getContext();
    }

    @Override
    public View onCreateView(View parent,String name,Context context,AttributeSet attrs){
        View result = null;
        if(this.mLoadAppCompatViews && APPCOMPAT_VIEWS.contains(name) && ! this.mFailedAppCompatViews.contains(name)){
            result = this.loadCustomView((mUseAndroidx ? sAndroidxViewPrefix : sLegacyAppCompatViewPrefix) + name,
                    attrs);
            if(result == null){
                this.mFailedAppCompatViews.add(name);
            }
        }
        if(result == null){
            for(String prefix: sClassPrefixList){
                try{
                    result = mDelegate.createView(name,prefix,attrs);
                    if(result != null){
                        break;
                    }
                } catch(Exception e){
                    // In this case we want to let the base class take a crack at it.
                }
            }
        }

        //if is a custom view
        if(result == null){
            result = loadCustomView(name,attrs);
        }

        if(result != null){
            //判断View是否有shape设定的属性
            if(attrs.getAttributeValue(sAutoNs,"shapeType") != null || attrs.getAttributeValue(sAutoNs,
                    "shapeSolidSize") != null || attrs.getAttributeValue(sAutoNs,"shapeSolidWidth") != null ||
               attrs.getAttributeValue(sAutoNs,"shapeSolidHeight") != null || attrs.getAttributeValue(sAutoNs,
                    "shapeSolidColor") != null || attrs.getAttributeValue(sAutoNs,"shapeStrokeWidth") != null ||
               attrs.getAttributeValue(sAutoNs,"shapeStrokeColor") != null || attrs.getAttributeValue(sAutoNs,
                    "shapeStokeDashWidth") != null || attrs.getAttributeValue(sAutoNs,"shapeStokeDashGap") != null ||
               attrs.getAttributeValue(sAutoNs,"shapeCornerRadius") != null || attrs.getAttributeValue(sAutoNs,
                    "shapeCornerTopLeftRadius") != null || attrs.getAttributeValue(sAutoNs,
                    "shapeCornerTopRightRadius") != null || attrs.getAttributeValue(sAutoNs,
                    "shapeCornerBottomLeftRadius") != null || attrs.getAttributeValue(sAutoNs,
                    "shapeCornerBottomRightRadius") != null || attrs.getAttributeValue(sAutoNs,"shapeGradientType") !=
                                                               null || attrs.getAttributeValue(sAutoNs,
                    "shapeGradientAngle") != null || attrs.getAttributeValue(sAutoNs,"shapeGradientStartColor") !=
                                                     null || attrs.getAttributeValue(sAutoNs,
                    "shapeGradientCenterColor") != null || attrs.getAttributeValue(sAutoNs,"shapeGradientEndColor") !=
                                                           null || attrs.getAttributeValue(sAutoNs,
                    "shapeGradientRadius") != null || attrs.getAttributeValue(sAutoNs,"shapeGradientCenterX") != null ||
               attrs.getAttributeValue(sAutoNs,"shapeGradientCenterY") != null)
            {
                applyDrawableToView(result,attrs);
            }
        }
        return result;
    }

    /**
     * 调用预览方法
     *
     * @param view
     * @param attrs
     */
    private void applyDrawableToView(View view,AttributeSet attrs){
        try{
            Class<?> c = mLayoutLibCallback.findClass("com.app.vshape.ShapeHelper");
            Method method = c.getDeclaredMethod("applyDrawableToView",View.class,AttributeSet.class);
            method.setAccessible(true);
            method.invoke(null,view,attrs);
        } catch(Throwable e){
            if(e instanceof InvocationTargetException){
                Throwable ex = ((InvocationTargetException)e).getTargetException();
                if(ex instanceof NoClassDefFoundError && ex.getMessage().contains("vshape/R$styleable")){
                    Bridge.getLog().warning("build-needed",
                            "ViewShape not worked due to missing R.class, try assemble project to refresh",
                            null,
                            null);
                }
            } else{
                if(! sExceptionCaught){
                    sExceptionCaught = true;
                    ShapePreview.sLogger.info(e);
                }
            }
        }
    }

    /**
     * 加载View的一些信息
     *
     * @param name
     * @param attrs
     * @return
     */
    private View loadCustomView(String name,AttributeSet attrs){
        if(mLayoutLibCallback == null)
            return null;
        if(name.equals("view")){
            name = attrs.getAttributeValue(null,"class");
            if(name == null){
                return null;
            }
        }
        sConstructorArgs[0] = getContext();
        sConstructorArgs[1] = attrs;
        Object customView = null;

        try{
            customView = mLayoutLibCallback.loadView(name,sConstructorSignature,sConstructorArgs);
        } catch(Exception e){
            //empty
        } finally{
            sConstructorArgs[0] = null;
            sConstructorArgs[1] = null;
        }
        if(customView instanceof View){
            return (View)customView;
        }
        return null;
    }

    @Override
    public View onCreateView(String name,Context context,AttributeSet attrs){
        return onCreateView(null,name,context,attrs);
    }

    protected LinearLayout createLinearLayout(Context context,AttributeSet attrs){
        return new LinearLayout(context,attrs);
    }

    protected RelativeLayout createRelativeLayout(Context context,AttributeSet attrs){
        return new RelativeLayout(context,attrs);
    }

    protected FrameLayout createFrameLayout(Context context,AttributeSet attrs){
        return new FrameLayout(context,attrs);
    }
}

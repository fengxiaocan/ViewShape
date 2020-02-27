package com.app.vshape;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public final class ShapeHelper{
    private static OnViewShapeInterceptor onViewShapeInterceptor;

    public static void setOnViewShapeInterceptor(OnViewShapeInterceptor onViewShapeInterceptor){
        ShapeHelper.onViewShapeInterceptor = onViewShapeInterceptor;
    }

    /**
     * 注册{@link AppCompatActivity#onCreate(Bundle)}方法,写在super.onCreate之前
     *
     * @param activity
     * @return
     */
    public static ShapeFactory registerActivity(AppCompatActivity activity){
        return new ShapeFactory(activity);
    }

    public static ShapeFactory registerShapeFactory(){
        return new ShapeFactory();
    }

    /**
     * 注册{@link Activity#onCreate(Bundle)}方法,写在super.onCreate之前
     *
     * @param activity
     * @return
     */
    public static ShapeFactory registerActivity(Activity activity){
        return new ShapeFactory(activity);
    }

    /**
     * 注册在{@link Activity#attachBaseContext(Context)}方法
     *
     * @param newBase
     * @return
     */
    public static Context attachBaseContext(final Context newBase){
        final LayoutInflater inflater = LayoutInflater.from(newBase);
        if(inflater instanceof ViewShapeInflater)
            return newBase;
        return new ContextWrapper(newBase){
            private ViewShapeInflater mInflater;

            @Override
            public Object getSystemService(String name){
                if(LAYOUT_INFLATER_SERVICE.equals(name)){
                    if(mInflater == null){
                        mInflater = new ViewShapeInflater(newBase,inflater);
                    }
                    return mInflater;
                }
                return super.getSystemService(name);
            }
        };
    }


    public static boolean isSupportedAttr(String attributeName){
        switch(attributeName){
            case "shapeType":
            case "shapeGradientType":
            case "shapeGradientAngle":
            case "shapeSolidSize":
            case "shapeSolidWidth":
            case "shapeSolidHeight":
            case "shapeSolidColor":
            case "shapeStrokeWidth":
            case "shapeStrokeColor":
            case "shapeStokeDashWidth":
            case "shapeStokeDashGap":
            case "shapeCornerRadius":
            case "shapeCornerTopLeftRadius":
            case "shapeCornerTopRightRadius":
            case "shapeCornerBottomLeftRadius":
            case "shapeCornerBottomRightRadius":
            case "shapeGradientStartColor":
            case "shapeGradientCenterColor":
            case "shapeGradientEndColor":
            case "shapeGradientRadius":
            case "shapeGradientCenterX":
            case "shapeGradientCenterY":
                return true;
            default:
                return false;
        }
    }

    /**
     * 可以重写{@link AppCompatActivity#onCreateView(View,String,Context,AttributeSet)},
     * {@link AppCompatActivity#onCreateView(String,Context,AttributeSet)}方法,调用super
     * .onCreateView后再调用该方法
     *
     * @param view
     * @param attrs
     */
    public static boolean onCreateShape(View view,AttributeSet attrs)
    {
        if(view == null){
            return false;
        }
        int attCount = attrs.getAttributeCount();
        for(int i = 0;i < attCount;++ i){
            String attributeName = attrs.getAttributeName(i);
            //是否支持该类型attribute
            if(ShapeHelper.isSupportedAttr(attributeName)){
                return true;
            }
        }
        return false;
    }

    /**
     * 在确定有shape的字段后调用最好
     *
     * @param view
     * @param context
     * @param attrs
     */
    public static void setShape(View view,Context context,AttributeSet attrs){
        view.setBackground(newshape(context.getResources(),attrs));
    }

    /**
     * 起到预览作用
     * @param view
     * @param attrs
     */
    static void applyDrawableToView(View view,AttributeSet attrs){
        view.setBackground(newshape(view.getContext().getResources(),attrs));
    }

    public static GradientDrawable newshape(Context context,AttributeSet attrs){
        return newshape(context.getResources(),attrs);
    }

    /**
     * 创建一个shapeDrawable
     *
     * @param resources
     * @param attrs
     * @return
     */
    public static GradientDrawable newshape(Resources resources,AttributeSet attrs){
        GradientDrawable drawable = new GradientDrawable();
        //shape类型
        TypedArray a = resources.obtainAttributes(attrs,R.styleable.ViewShape);
        final OnViewShapeInterceptor i;
        if(onViewShapeInterceptor != null){
            i = onViewShapeInterceptor;
        } else{
            i = OnViewShapeInterceptorTemp.getInstance();
        }

        drawable.setShape(i.getInt(a,R.styleable.ViewShape_shapeType,GradientDrawable.RECTANGLE));

        //size
        final int size = i.getDimensionPixelSize(a,R.styleable.ViewShape_shapeSolidSize,- 1);
        drawable.setSize(i.getDimensionPixelSize(a,R.styleable.ViewShape_shapeSolidWidth,size),
                i.getDimensionPixelSize(a,R.styleable.ViewShape_shapeSolidHeight,size));
        //gradient
        drawable.setGradientType(i.getInt(a,R.styleable.ViewShape_shapeGradientType,GradientDrawable.LINEAR_GRADIENT));
        drawable.setGradientRadius(i.getDimension(a,R.styleable.ViewShape_shapeGradientRadius,0));
        drawable.setGradientCenter(i.getFloat(a,R.styleable.ViewShape_shapeGradientCenterX,0.5F),
                i.getFloat(a,R.styleable.ViewShape_shapeGradientCenterY,0.5F));

        int[] gradientColors;
        final int gradientStartColor = i.getColor(a,R.styleable.ViewShape_shapeGradientStartColor,Color.TRANSPARENT);
        final int gradientEndColor = i.getColor(a,R.styleable.ViewShape_shapeGradientEndColor,Color.TRANSPARENT);
        if(a.hasValue(R.styleable.ViewShape_shapeGradientCenterColor)){
            final int gradientCenterColor = i.getColor(a,R.styleable.ViewShape_shapeGradientCenterColor,
                    Color.TRANSPARENT);
            gradientColors = new int[]{gradientStartColor,gradientCenterColor,gradientEndColor};
        } else{
            gradientColors = new int[]{gradientStartColor,gradientEndColor};
        }
        drawable.setColors(gradientColors);
        final int orientationIndex = i.getInt(a,R.styleable.ViewShape_shapeGradientAngle,0);
        drawable.setOrientation(GradientDrawable.Orientation.values()[orientationIndex]);

        //solid
        if(a.hasValue(R.styleable.ViewShape_shapeSolidColor)){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                drawable.setColor(i.getColorStateList(a,R.styleable.ViewShape_shapeSolidColor));
            } else{
                drawable.setColor(i.getColor(a,R.styleable.ViewShape_shapeSolidColor,Color.TRANSPARENT));
            }
        }
        //stroke
        drawable.setStroke(i.getDimensionPixelSize(a,R.styleable.ViewShape_shapeStrokeWidth,- 1),
                i.getColor(a,R.styleable.ViewShape_shapeStrokeColor,Color.TRANSPARENT),
                i.getDimensionPixelSize(a,R.styleable.ViewShape_shapeStokeDashGap,0),
                i.getDimensionPixelSize(a,R.styleable.ViewShape_shapeStokeDashWidth,0));
        //radius
        final float radius = i.getDimension(a,R.styleable.ViewShape_shapeCornerRadius,0);
        drawable.setCornerRadii(new float[]{i.getDimension(a,R.styleable.ViewShape_shapeCornerTopLeftRadius,radius),
                i.getDimension(a,R.styleable.ViewShape_shapeCornerTopLeftRadius,radius),
                i.getDimension(a,R.styleable.ViewShape_shapeCornerTopRightRadius,radius),
                i.getDimension(a,R.styleable.ViewShape_shapeCornerTopRightRadius,radius),
                i.getDimension(a,R.styleable.ViewShape_shapeCornerBottomRightRadius,radius),
                i.getDimension(a,R.styleable.ViewShape_shapeCornerBottomRightRadius,radius),
                i.getDimension(a,R.styleable.ViewShape_shapeCornerBottomLeftRadius,radius),
                i.getDimension(a,R.styleable.ViewShape_shapeCornerBottomLeftRadius,radius),});

        a.recycle();
        return drawable;
    }
}

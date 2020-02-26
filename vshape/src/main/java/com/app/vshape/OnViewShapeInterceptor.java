package com.app.vshape;

import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;

import androidx.annotation.ColorInt;
import androidx.annotation.StyleableRes;

public interface OnViewShapeInterceptor{

    ColorStateList getColorStateList(TypedArray array,@StyleableRes int index);

    int getColor(TypedArray array,@StyleableRes int index,@ColorInt int defValue);

    int getInt(TypedArray array,@StyleableRes int index, int defValue);

    int getDimensionPixelSize(TypedArray array,@StyleableRes int index, int defValue);

    float getDimension(TypedArray array,@StyleableRes int index, float defValue);

    float getFloat(TypedArray array,@StyleableRes int index, float defValue);
}

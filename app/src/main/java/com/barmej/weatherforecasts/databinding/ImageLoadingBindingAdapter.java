package com.barmej.weatherforecasts.databinding;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

public class ImageLoadingBindingAdapter {

    @BindingAdapter( value ={"image","errorDrawable"}, requireAll = false)
    public static void loadImage(ImageView imageView, String url, Drawable errorDrawable) {
        // lode the image from the url and display it .
    }
    @BindingAdapter("image")
    public static void loadImage(ImageView imageView, int resourceId) {
        imageView.setImageResource(resourceId);
    }

}

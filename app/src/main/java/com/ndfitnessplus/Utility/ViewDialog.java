package com.ndfitnessplus.Utility;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
//import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ndfitnessplus.R;

public class ViewDialog {
    Activity activity;
    Dialog dialog;
    //..we need the context else we can not create the dialog so get context in constructor
    public ViewDialog(Activity activity) {
        this.activity = activity;
    }

    public void showDialog() {

        dialog  = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //...set cancelable false so that it's never get hidden
        dialog.setCancelable(false);
        //...that's the layout i told you will inflate later
        dialog.setContentView(R.layout.loading_gif);

        //...initialize the imageView form infalted layout
      final  ImageView gifImageView = dialog.findViewById(R.id.custom_loading_imageView);

        /*
        it was never easy to load gif into an ImageView before Glide or Others library
        and for doing this we need DrawableImageViewTarget to that ImageView
        */
       // GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(gifImageView);
        Glide.with(activity)
                .load(R.drawable.ndgymtimegif)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        gifImageView.setImageDrawable(resource);
                    }
                });
        //...now load that gif which we put inside the drawble folder here with the help of Glide
//        RequestOptions requestOptions = new RequestOptions();
////        requestOptions.placeholder(R.drawable.nouser);
////        requestOptions.error(R.drawable.nouser);
////
////
////        Glide.with(activity)
////                .setDefaultRequestOptions(requestOptions)
////                .load(R.drawable.ndgymtimegif).into(imageViewTarget);
//        Glide.with(activity)
//                .load(R.drawable.ndgymtimegif)
//                .placeholder(R.drawable.ndgymtimegif)
//                .into(imageViewTarget);

        //...finaly show it
        dialog.show();
    }

    //..also create a method which will hide the dialog when some work is done
    public void hideDialog(){
        dialog.dismiss();
    }

}

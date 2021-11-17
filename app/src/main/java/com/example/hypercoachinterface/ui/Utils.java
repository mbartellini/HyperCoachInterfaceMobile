package com.example.hypercoachinterface.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.ImageView;

public class Utils {

    public static void setImageFromBase64(ImageView imageView, String imgSrc) {
        if (imageView == null || imgSrc == null)
            return;
        String imageDataBytes = imgSrc.substring(imgSrc.indexOf(",")+1);
        byte[] decodedString = Base64.decode(imageDataBytes, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        imageView.setImageBitmap(decodedByte);
    }

}

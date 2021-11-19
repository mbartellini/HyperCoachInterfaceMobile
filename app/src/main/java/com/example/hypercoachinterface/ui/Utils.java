package com.example.hypercoachinterface.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.example.hypercoachinterface.R;

public class Utils {

    public static void setImageFromBase64(ImageView imageView, String imgSrc, Activity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (imageView == null)
                    return;

                // TODO: generalize default case
                if(imgSrc == null || imgSrc.equals("@/assets/hci.png") ) {
                    imageView.setImageResource(R.mipmap.hci);
                    return;
                }

                String imageDataBytes = imgSrc.substring(imgSrc.indexOf(",")+1);
                byte[] decodedString = Base64.decode(imageDataBytes, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imageView.setImageBitmap(decodedByte);
            }
        });
    }

    public static void setImageFromBase64(ImageView imageView, String imgSrc) {
        if (imageView == null)
            return;
        // TODO: generalize default case
        if(imgSrc == null || imgSrc.equals("@/assets/hci.png") ) {
            imageView.setImageResource(R.mipmap.hci);
            return;
        }
        String imageDataBytes = imgSrc.substring(imgSrc.indexOf(",")+1);
        byte[] decodedString = Base64.decode(imageDataBytes, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        imageView.setImageBitmap(decodedByte);
    }

}

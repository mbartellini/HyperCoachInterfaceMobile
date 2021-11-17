package com.example.hypercoachinterface.backend.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserMetadata {

    @SerializedName("img_src")
    @Expose
    private String imgSrc;

    public UserMetadata() {
    }

    public UserMetadata(String imgSrc) {
        super();
        this.imgSrc = imgSrc;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }

}
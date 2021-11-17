package com.example.hypercoachinterface.backend.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ExerciseMetadata {
    @SerializedName("img_src")
    @Expose
    private String img_src;

    public ExerciseMetadata(String img_src) {
        this.img_src = img_src;
    }

    public String getImg_src() {
        return img_src;
    }

    public void setImg_src(String img_src) {
        this.img_src = img_src;
    }
}

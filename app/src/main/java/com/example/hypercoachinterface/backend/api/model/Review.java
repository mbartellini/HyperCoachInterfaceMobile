package com.example.hypercoachinterface.backend.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Review {

    @SerializedName("score")
    @Expose
    private Integer score;
    @SerializedName("review")
    @Expose
    private String review;

    public Review() {
    }

    public Review(Integer score, String review) {
        super();
        this.score = score;
        this.review = review;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

}
package com.example.hypercoachinterface.backend.api.model;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RoutineExercise {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("limit")
    @Expose
    private Integer limit;
    @SerializedName("limitType")
    @Expose
    private String limitType;

    public RoutineExercise() {
    }

    public RoutineExercise(Integer id, Integer limit, String limitType) {
        super();
        this.id = id;
        this.limit = limit;
        this.limitType = limitType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public String getLimitType() {
        return limitType;
    }

    public void setLimitType(String limitType) {
        this.limitType = limitType;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(RoutineExercise.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("id");
        sb.append('=');
        sb.append(((this.id == null)?"<null>":this.id));
        sb.append(',');
        sb.append("limit");
        sb.append('=');
        sb.append(((this.limit == null)?"<null>":this.limit));
        sb.append(',');
        sb.append("limitType");
        sb.append('=');
        sb.append(((this.limitType == null)?"<null>":this.limitType));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
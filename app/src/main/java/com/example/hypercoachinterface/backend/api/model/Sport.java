package com.example.hypercoachinterface.backend.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Sport {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("detail")
    @Expose
    private String detail;

    public Sport() {
    }

    public Sport(String name, String detail) {
        this(0, name, detail);
    }

    public Sport(Integer id, String name, String detail) {
        this.id = id;
        this.name = name;
        this.detail = detail;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}

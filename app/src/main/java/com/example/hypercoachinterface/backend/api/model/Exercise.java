package com.example.hypercoachinterface.backend.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Exercise {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("detail")
    @Expose
    private String detail;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("metadata")
    @Expose
    private ExerciseMetadata metadata;

    /**
     * No args constructor for use in serialization
     */
    public Exercise() {
    }

    public Exercise(String name, String detail, String type, ExerciseMetadata metadata) {
        super();
        this.name = name;
        this.detail = detail;
        this.type = type;
        this.metadata = metadata;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ExerciseMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(ExerciseMetadata metadata) {
        this.metadata = metadata;
    }

}
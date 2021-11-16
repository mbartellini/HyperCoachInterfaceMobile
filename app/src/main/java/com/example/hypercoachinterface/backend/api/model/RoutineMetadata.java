package com.example.hypercoachinterface.backend.api.model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RoutineMetadata implements Serializable {

    @SerializedName("img_src")
    @Expose
    private String imgSrc;
    @SerializedName("duration")
    @Expose
    private Integer duration;
    @SerializedName("equipment")
    @Expose
    private Boolean equipment;
    @SerializedName("cycles")
    @Expose
    private List<RoutineCycle> cycles = null;
    @SerializedName("fav_count")
    @Expose
    private Integer favCount;

    public RoutineMetadata() {
    }

    public RoutineMetadata(String imgSrc, Integer duration, Boolean equipment, List<RoutineCycle> cycles) {
        super();
        this.imgSrc = imgSrc;
        this.duration = duration;
        this.equipment = equipment;
        this.cycles = cycles;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Boolean getEquipment() {
        return equipment;
    }

    public void setEquipment(Boolean equipment) {
        this.equipment = equipment;
    }

    public List<RoutineCycle> getCycles() {
        return cycles;
    }

    public void setCycles(List<RoutineCycle> cycles) {
        this.cycles = cycles;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(RoutineMetadata.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("imgSrc");
        sb.append('=');
        sb.append(((this.imgSrc == null)?"<null>":this.imgSrc));
        sb.append(',');
        sb.append("duration");
        sb.append('=');
        sb.append(((this.duration == null)?"<null>":this.duration));
        sb.append(',');
        sb.append("equipment");
        sb.append('=');
        sb.append(((this.equipment == null)?"<null>":this.equipment));
        sb.append(',');
        sb.append("cycles");
        sb.append('=');
        sb.append(((this.cycles == null)?"<null>":this.cycles));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
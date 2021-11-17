package com.example.hypercoachinterface.ui.adapter;

public class RoutineSummary {

    private Integer routineId, favCount;
    private String name, imgSrc;

    public RoutineSummary(Integer routineId, Integer favCount, String name) {
        this.routineId = routineId;
        this.favCount = favCount;
        this.name = name;
    }

    public Integer getRoutineId() {
        return routineId;
    }

    public void setRoutineId(Integer routineId) {
        this.routineId = routineId;
    }

    public Integer getFavCount() {
        return favCount;
    }

    public void setFavCount(Integer favCount) {
        this.favCount = favCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }
}

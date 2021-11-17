package com.example.hypercoachinterface.ui.adapter;

import com.example.hypercoachinterface.backend.api.model.Routine;

public class RoutineSummary {

    private Integer routineId, favCount;
    private String name, imgSrc;

    public RoutineSummary(Integer routineId, Integer favCount, String name, String imgSrc) {
        this.routineId = routineId;
        this.favCount = favCount;
        this.name = name;
    }

    public static RoutineSummary fromRoutine(Routine routine, Integer favCount) {
        if (routine == null)
            return null;
        String imgSrc = null;
        if (routine.getMetadata() != null)
            imgSrc = routine.getMetadata().getImgSrc();
        return new RoutineSummary(routine.getId(), favCount, routine.getName(), imgSrc);
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

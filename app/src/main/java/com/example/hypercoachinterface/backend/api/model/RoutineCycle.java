package com.example.hypercoachinterface.backend.api.model;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RoutineCycle {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("repetitions")
    @Expose
    private Integer repetitions;
    @SerializedName("exercises")
    @Expose
    private List<RoutineExercise> exercises = null;

    public RoutineCycle() {
    }

    public RoutineCycle(String name, Integer repetitions, List<RoutineExercise> exercises) {
        super();
        this.name = name;
        this.repetitions = repetitions;
        this.exercises = exercises;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRepetitions() {
        return repetitions;
    }

    public void setRepetitions(Integer repetitions) {
        this.repetitions = repetitions;
    }

    public List<RoutineExercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<RoutineExercise> exercises) {
        this.exercises = exercises;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(RoutineCycle.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("name");
        sb.append('=');
        sb.append(((this.name == null)?"<null>":this.name));
        sb.append(',');
        sb.append("repetitions");
        sb.append('=');
        sb.append(((this.repetitions == null)?"<null>":this.repetitions));
        sb.append(',');
        sb.append("exercises");
        sb.append('=');
        sb.append(((this.exercises == null)?"<null>":this.exercises));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
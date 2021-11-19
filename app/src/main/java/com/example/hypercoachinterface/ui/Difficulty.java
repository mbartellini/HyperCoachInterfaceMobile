package com.example.hypercoachinterface.ui;

import com.example.hypercoachinterface.R;

public enum Difficulty {
    BEGINNER(R.string.beginner, "beginner"),
    INTERMEDIATE(R.string.intermediate, "intermediate"),
    ADVANCED(R.string.advanced, "advanced");


    private final int stringId;
    private final String apiName;

    Difficulty(int stringId, String apiName) {
        this.stringId = stringId;
        this.apiName = apiName;
    }

    public int getStringId() {
        return stringId;
    }

    public String getApiName() {
        return apiName;
    }
}

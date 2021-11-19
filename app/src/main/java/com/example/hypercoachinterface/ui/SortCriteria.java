package com.example.hypercoachinterface.ui;

import com.example.hypercoachinterface.R;

public enum SortCriteria {
    NAME(R.string.name_sort_criteria, "name"),
    DATE(R.string.date_sort_criteria, "date"),
    DIFFICULTY(R.string.diff_sort_criteria, "difficulty"),
    FAVS(R.string.favs_sort_criteria, "score");

    private final int stringId;
    private final String apiName;

    SortCriteria(int stringId, String apiName) {
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

package com.example.hypercoachinterface.backend.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.hypercoachinterface.backend.App;
import com.example.hypercoachinterface.backend.api.ApiClient;
import com.example.hypercoachinterface.backend.api.ApiExerciseService;
import com.example.hypercoachinterface.backend.api.ApiResponse;
import com.example.hypercoachinterface.backend.api.model.Exercise;

public class ExerciseRepository {

    private final ApiExerciseService apiService;

    public ExerciseRepository(App application) {
        this.apiService = ApiClient.create(application, ApiExerciseService.class);
    }

    public LiveData<Resource<Exercise>> getExercise(int exerciseId) {
        return new NetworkBoundResource<Exercise, Exercise>()
        {
            @NonNull
            @Override
            protected LiveData<ApiResponse<Exercise>> createCall() {
                return apiService.getExercise(exerciseId);
            }
        }.asLiveData();
    }
}

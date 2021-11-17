package com.example.hypercoachinterface.backend.api;

import androidx.lifecycle.LiveData;

import com.example.hypercoachinterface.backend.api.model.PagedList;
import com.example.hypercoachinterface.backend.api.model.Routine;
import com.example.hypercoachinterface.backend.api.model.Sport;

import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface ApiRoutineService {

    @GET("routines")
    LiveData<ApiResponse<PagedList<Routine>>> getRoutines(@QueryMap Map<String, String> options);

    @GET("routines/{routineId}")
    LiveData<ApiResponse<Routine>> getRoutine(@Path("routineId") int routineId);

}

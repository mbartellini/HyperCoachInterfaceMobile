package com.example.hypercoachinterface.backend.api;

import androidx.lifecycle.LiveData;

import com.example.hypercoachinterface.backend.api.model.PagedList;
import com.example.hypercoachinterface.backend.api.model.Routine;
import com.example.hypercoachinterface.backend.api.model.Sport;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiRoutineService {

    @GET("routines")
    LiveData<ApiResponse<PagedList<Routine>>> getRoutines();

//    @POST("sports")
//    LiveData<ApiResponse<Sport>> addSport(@Body Sport sport);
//

    @GET("routines/{routineId}")
    LiveData<ApiResponse<Routine>> getRoutine(@Path("routineId") int routineId);

//
//    @PUT("sports/{sportId}")
//    LiveData<ApiResponse<Sport>> modifySport(@Path("sportId") int sportId, @Body Sport sport);
//
//    @DELETE("sports/{sportId}")
//    LiveData<ApiResponse<Void>> deleteSport(@Path("sportId") int sportId);
}

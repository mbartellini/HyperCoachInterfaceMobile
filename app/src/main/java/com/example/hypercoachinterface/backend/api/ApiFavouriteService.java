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
import retrofit2.http.Query;

public interface ApiFavouriteService {

    @GET("favourites")
    LiveData<ApiResponse<PagedList<Routine>>> getFavourites(@Query("page") int page, @Query("size") int size);

    @POST("favourites/{routineId}")
    LiveData<ApiResponse<Void>> postFavourite(@Path("routineId") int routineId);

    @DELETE("favourites/{routineId}")
    LiveData<ApiResponse<Void>> deleteFavourite(@Path("routineId") int routineId);

}

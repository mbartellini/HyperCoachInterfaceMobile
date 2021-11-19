package com.example.hypercoachinterface.backend.api;

import androidx.lifecycle.LiveData;

import com.example.hypercoachinterface.backend.api.model.PagedList;
import com.example.hypercoachinterface.backend.api.model.RoutineCategory;
import com.example.hypercoachinterface.backend.api.model.Sport;

import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface ApiCategoryService {

    @GET("categories")
    LiveData<ApiResponse<PagedList<RoutineCategory>>> getCategories(@QueryMap Map<String, String> options);

}

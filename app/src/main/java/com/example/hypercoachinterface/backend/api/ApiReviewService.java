package com.example.hypercoachinterface.backend.api;

import androidx.lifecycle.LiveData;

import com.example.hypercoachinterface.backend.api.model.PagedList;
import com.example.hypercoachinterface.backend.api.model.Review;
import com.example.hypercoachinterface.backend.api.model.Routine;

import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface ApiReviewService {

    @GET("reviews/{routineId}")
    LiveData<ApiResponse<PagedList<Review>>> getReviews(@Path("routineId") int routineId,
                                                        @QueryMap Map<String, String> options);

    @POST("reviews/{routineId}")
    LiveData<ApiResponse<Void>> addReview(@Path("routineId") int routineId, @Body Review review);

}

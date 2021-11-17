package com.example.hypercoachinterface.backend.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.hypercoachinterface.backend.App;
import com.example.hypercoachinterface.backend.api.ApiClient;
import com.example.hypercoachinterface.backend.api.ApiResponse;
import com.example.hypercoachinterface.backend.api.ApiReviewService;
import com.example.hypercoachinterface.backend.api.model.PagedList;
import com.example.hypercoachinterface.backend.api.model.Review;

import java.util.HashMap;
import java.util.Map;

public class ReviewRepository {

    private final ApiReviewService apiService;

    public ReviewRepository(App application) {
        this.apiService = ApiClient.create(application, ApiReviewService.class);
    }

    public LiveData<Resource<PagedList<Review>>> getReviews(int routineId) {
        return new NetworkBoundResource<PagedList<Review>, PagedList<Review>>()
        {
            @NonNull
            @Override
            protected LiveData<ApiResponse<PagedList<Review>>> createCall() {
                Map<String, String> options = new HashMap<>();
                options.put("page", "0");
                options.put("size", "1");
                options.put("orderBy", "date");
                options.put("direction", "desc");
                return apiService.getReviews(routineId, options);
            }
        }.asLiveData();
    }

    public LiveData<Resource<Void>> addReview(int routineId, Review review) {
        return new NetworkBoundResource<Void, Void>()
        {
            @NonNull
            @Override
            protected LiveData<ApiResponse<Void>> createCall() {
                return apiService.addReview(routineId, review);
            }
        }.asLiveData();
    }
}

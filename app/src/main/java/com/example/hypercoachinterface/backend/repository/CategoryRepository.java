package com.example.hypercoachinterface.backend.repository;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;

import com.example.hypercoachinterface.backend.App;
import com.example.hypercoachinterface.backend.api.ApiCategoryService;
import com.example.hypercoachinterface.backend.api.ApiClient;
import com.example.hypercoachinterface.backend.api.ApiResponse;
import com.example.hypercoachinterface.backend.api.model.PagedList;
import com.example.hypercoachinterface.backend.api.model.RoutineCategory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryRepository {

    private static final String PAGE_SIZE_KEY = "size";
    private static final Integer CATEGORY_COUNT = 4;

    private final ApiCategoryService apiService;

    public CategoryRepository(App application) {
        this.apiService = ApiClient.create(application, ApiCategoryService.class);
    }

    public LiveData<Resource<List<RoutineCategory>>> getCategories() {
        return new NetworkBoundResource<PagedList<RoutineCategory>, List<RoutineCategory>>()
        {
            @NonNull
            @Override
            protected LiveData<ApiResponse<PagedList<RoutineCategory>>> createCall() {
                Map<String, String> options = new HashMap<>();
                options.put(PAGE_SIZE_KEY, String.valueOf(CATEGORY_COUNT));
                return apiService.getCategories(options);
            }

            @NonNull
            @Override
            @WorkerThread
            protected List<RoutineCategory> processResponse(PagedList<RoutineCategory> response)
            {
                return response.getContent();
            }

        }.asLiveData();
    }

}

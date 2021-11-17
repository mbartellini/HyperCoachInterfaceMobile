package com.example.hypercoachinterface.backend.repository;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;

import com.example.hypercoachinterface.backend.App;
import com.example.hypercoachinterface.backend.api.ApiClient;
import com.example.hypercoachinterface.backend.api.ApiFavouriteService;
import com.example.hypercoachinterface.backend.api.ApiResponse;
import com.example.hypercoachinterface.backend.api.ApiRoutineService;
import com.example.hypercoachinterface.backend.api.model.PagedList;
import com.example.hypercoachinterface.backend.api.model.Routine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoutineRepository {

    private final ApiRoutineService apiRoutineService;
    private final ApiFavouriteService apiFavouriteService;

    public RoutineRepository(App application) {
        this.apiRoutineService = ApiClient.create(application, ApiRoutineService.class);
        this.apiFavouriteService = ApiClient.create(application, ApiFavouriteService.class);
    }

    public LiveData<Resource<List<Routine>>> getRoutines() {
        return getRoutines(null, null, null, null);
    }

    public LiveData<Resource<List<Routine>>> getRoutines(
            Integer page, Integer size, String orderBy, String direction) {
        return new NetworkBoundResource<PagedList<Routine>, List<Routine>>()
        {
            @NonNull
            @Override
            protected LiveData<ApiResponse<PagedList<Routine>>> createCall() {
                Map<String, String> options = new HashMap<>();
                if(page != null) options.put("page", page.toString());
                if(size != null) options.put("size", size.toString());
                if(orderBy != null) options.put("orderBy", orderBy);
                if(direction != null) options.put("direction", direction);
                return apiRoutineService.getRoutines(options);
            }

            @NonNull
            @Override
            @WorkerThread
            protected List<Routine> processResponse(PagedList<Routine> response)
            {
                return response.getContent();
            }
        }.asLiveData();
    }



    public LiveData<Resource<Routine>> getRoutine(int routineId) {
        return new NetworkBoundResource<Routine, Routine>()
        {
            @NonNull
            @Override
            protected LiveData<ApiResponse<Routine>> createCall() {
                return apiRoutineService.getRoutine(routineId);
            }
        }.asLiveData();
    }

    public LiveData<Resource<List<Routine>>> getFavourites(int page, int size) {
        return new NetworkBoundResource<PagedList<Routine>, List<Routine>>()
        {
            @NonNull
            @Override
            protected LiveData<ApiResponse<PagedList<Routine>>> createCall() {
                return apiFavouriteService.getFavourites(page, size);
            }

            @NonNull
            @Override
            @WorkerThread
            protected List<Routine> processResponse(PagedList<Routine> response)
            {
                return response.getContent();
            }
        }.asLiveData();
    }

    public LiveData<Resource<Routine>> postFavourite(int routineId) {
        return new NetworkBoundResource<Routine, Routine>()
        {
            @NonNull
            @Override
            protected LiveData<ApiResponse<Routine>> createCall() {
                return apiFavouriteService.postFavourite(routineId);
            }
        }.asLiveData();
    }

    public LiveData<Resource<Void>> deleteFavourite(int routineId) {
        return new NetworkBoundResource<Void, Void>()
        {
            @NonNull
            @Override
            protected LiveData<ApiResponse<Void>> createCall() {
                return apiFavouriteService.deleteFavourite(routineId);
            }
        }.asLiveData();
    }

}

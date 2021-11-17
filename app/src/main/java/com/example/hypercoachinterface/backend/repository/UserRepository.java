package com.example.hypercoachinterface.backend.repository;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;

import com.example.hypercoachinterface.backend.App;
import com.example.hypercoachinterface.backend.api.ApiClient;
import com.example.hypercoachinterface.backend.api.ApiResponse;
import com.example.hypercoachinterface.backend.api.ApiUserService;
import com.example.hypercoachinterface.backend.api.model.Credentials;
import com.example.hypercoachinterface.backend.api.model.PagedList;
import com.example.hypercoachinterface.backend.api.model.Routine;
import com.example.hypercoachinterface.backend.api.model.Token;
import com.example.hypercoachinterface.backend.api.model.User;

import java.util.List;

public class UserRepository {

    private final ApiUserService apiService;

    public UserRepository(App app) {
        this.apiService = ApiClient.create(app, ApiUserService.class);
    }

    public LiveData<Resource<Token>> login(Credentials credentials) {
        return new NetworkBoundResource<Token, Token>()
        {
            @NonNull
            @Override
            protected LiveData<ApiResponse<Token>> createCall() {
                return apiService.login(credentials);
            }
        }.asLiveData();
    }

    public LiveData<Resource<Void>> logout() {
        return new NetworkBoundResource<Void, Void>()
        {
            @NonNull
            @Override
            protected LiveData<ApiResponse<Void>> createCall() {
                return apiService.logout();
            }
        }.asLiveData();
    }

    public LiveData<Resource<User>> getCurrentUser() {
        return new NetworkBoundResource<User, User>()
        {
            @NonNull
            @Override
            protected LiveData<ApiResponse<User>> createCall() {
                return apiService.getCurrentUser();
            }
        }.asLiveData();
    }

    public LiveData<Resource<List<Routine>>> getUserRoutines() {
        return new NetworkBoundResource<PagedList<Routine>, List<Routine>>()
        {
            @NonNull
            @Override
            protected LiveData<ApiResponse<PagedList<Routine>>> createCall() {
                return apiService.getUserRoutines();
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
}
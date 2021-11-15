package com.example.hypercoachinterface.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.hypercoachinterface.backend.api.model.User;
import com.example.hypercoachinterface.backend.repository.Resource;
import com.example.hypercoachinterface.backend.repository.UserRepository;
import com.example.hypercoachinterface.viewmodel.RepositoryViewModel;

import java.util.ArrayList;
import java.util.List;

public class UserViewModel extends RepositoryViewModel<UserRepository> {

    private final LiveData<Resource<User>> user;

    public UserViewModel(UserRepository repository) {
        super(repository);

        user = repository.getCurrentUser();
    }

    public LiveData<Resource<User>> getUser() {
        return user;
    }
}
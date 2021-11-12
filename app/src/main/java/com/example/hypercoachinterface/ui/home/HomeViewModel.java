package com.example.hypercoachinterface.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("");  // TODO: Only display if amount of cards is null
    }

    public LiveData<String> getText() {
        return mText;
    }
}
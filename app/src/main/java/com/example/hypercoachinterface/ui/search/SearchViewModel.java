package com.example.hypercoachinterface.ui.search;

import android.app.Application;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.hypercoachinterface.backend.api.model.Routine;
import com.example.hypercoachinterface.backend.api.model.RoutineCategory;
import com.example.hypercoachinterface.backend.repository.CategoryRepository;
import com.example.hypercoachinterface.backend.repository.Resource;
import com.example.hypercoachinterface.backend.repository.RoutineRepository;
import com.example.hypercoachinterface.backend.repository.Status;
import com.example.hypercoachinterface.viewmodel.RepositoryViewModel;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SearchViewModel extends ViewModel {

    private final static int PAGE_SIZE = 10;
    private static final String TAG = "SearchViewModel";

    private RoutineRepository routineRepository;
    private CategoryRepository categoryRepository;

    private int searchPage = 0;
    private boolean isLastSearchPage = false;
    private final List<Routine> allSearches = new ArrayList<>();
    private final MediatorLiveData<Resource<List<Routine>>> searches = new MediatorLiveData<>();
    private final Map<String, String> options;

    public SearchViewModel(RoutineRepository routineRepository, CategoryRepository categoryRepository) {
        this.routineRepository = routineRepository;
        this.categoryRepository = categoryRepository;
        this.options = new HashMap<>();
        this.options.put("size", String.valueOf(PAGE_SIZE));
    }

    public LiveData<Resource<List<RoutineCategory>>> getCategories() {
        return categoryRepository.getCategories();
    }

    public LiveData<Resource<List<Routine>>> getSearches() {
        getMoreSearches();
        return searches;
    }

    public void setSearch(Map<String, String> options) {
        this.options.clear();
        this.options.putAll(options);
        searchPage = 0;
        isLastSearchPage = false;
        allSearches.clear();
        getMoreSearches();
    }

    public void getMoreSearches() {
        if (isLastSearchPage)
            return;

        Log.d(TAG, "getMoreFavourites: requesting " + searchPage);

        int requestedPage = searchPage;

        options.put("page", String.valueOf(requestedPage));
        searches.addSource(routineRepository.getRoutines(options), resource -> {
            if (resource.getStatus() == Status.SUCCESS) {
                if ((resource.getData() == null) || (resource.getData().size() == 0) || (resource.getData().size() < PAGE_SIZE))
                    isLastSearchPage = true;

                if (requestedPage < searchPage)
                    return;

                searchPage++;

                Log.d(TAG, "getMoreSearches: requestedPage " + requestedPage + " hasContent " + !resource.getData().isEmpty());

                if (resource.getData() != null) {
                    allSearches.addAll(resource.getData());
                }

                searches.setValue(Resource.success(allSearches));
            } else if (resource.getStatus() == Status.LOADING) {
                searches.setValue(resource);
            }
        });
    }

    public static class SearchViewModelFactory implements ViewModelProvider.Factory {

        private final RoutineRepository routineRepository;
        private final CategoryRepository categoryRepository;

        public SearchViewModelFactory(RoutineRepository routineRepository, CategoryRepository categoryRepository) {
            this.routineRepository = routineRepository;
            this.categoryRepository = categoryRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> aClass) {
            return (T) new SearchViewModel(routineRepository, categoryRepository);
        }
    }

}
package com.example.hypercoachinterface.ui.search;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.hypercoachinterface.backend.api.model.Routine;
import com.example.hypercoachinterface.backend.repository.Resource;
import com.example.hypercoachinterface.backend.repository.RoutineRepository;
import com.example.hypercoachinterface.backend.repository.Status;
import com.example.hypercoachinterface.viewmodel.RepositoryViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchViewModel extends RepositoryViewModel<RoutineRepository> {

    private final static int PAGE_SIZE = 10;

    private RoutineRepository repository;
    private int searchPage = 0;
    private boolean isLastSearchPage = false;
    private final List<Routine> allSearches = new ArrayList<>();
    private final MediatorLiveData<Resource<List<Routine>>> searches = new MediatorLiveData<>();
    private final Map<String, String> options;

    public SearchViewModel(RoutineRepository repository) {
        super(repository);
        this.repository = repository;
        this.options = new HashMap<>();
    }

    public void setSearch(Map<String, String> options) {
        this.options.clear();
        this.options.put("page", String.valueOf(searchPage));
        this.options.put("size", String.valueOf(PAGE_SIZE));
        this.options.putAll(options);
        searchPage = 0;
        isLastSearchPage = false;
        allSearches.clear();
    }

    public LiveData<Resource<List<Routine>>> getSearches() {
        getMoreSearches();
        return searches;
    }

    public void getMoreSearches() {
        if (isLastSearchPage)
            return;

        searches.addSource(repository.getRoutines(options), resource -> {
            if (resource.getStatus() == Status.SUCCESS) {
                if ((resource.getData() == null) || (resource.getData().size() == 0) || (resource.getData().size() < PAGE_SIZE))
                    isLastSearchPage = true;

                searchPage++;

                if (resource.getData() != null)
                    allSearches.addAll(resource.getData());
                searches.setValue(Resource.success(allSearches));
            } else if (resource.getStatus() == Status.LOADING) {
                searches.setValue(resource);
            }
        });
    }

}

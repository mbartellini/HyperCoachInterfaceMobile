package com.example.hypercoachinterface.ui.favorites;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.hypercoachinterface.backend.api.model.Routine;
import com.example.hypercoachinterface.backend.api.model.User;
import com.example.hypercoachinterface.backend.repository.Resource;
import com.example.hypercoachinterface.backend.repository.RoutineRepository;
import com.example.hypercoachinterface.backend.repository.Status;
import com.example.hypercoachinterface.backend.repository.UserRepository;
import com.example.hypercoachinterface.viewmodel.RepositoryViewModel;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

public class FavoritesViewModel extends RepositoryViewModel<RoutineRepository> {

    private final static int PAGE_SIZE = 10;
    private static final String TAG = "FavouriteViewModel";

    private int favouritePage = 0;
    private boolean isLastFavouritePage = false;
    private final List<Routine> allFavourites = new ArrayList<>();
    private final MediatorLiveData<Resource<List<Routine>>> favourites = new MediatorLiveData<>();

    public FavoritesViewModel(RoutineRepository repository) {
        super(repository);
    }

    public LiveData<Resource<List<Routine>>> getFavourites() {
        getMoreFavourites();
        return favourites;
    }

    public void getMoreFavourites() {
        if (isLastFavouritePage)
            return;

        Log.d(TAG, "getMoreFavourites: requesting " + favouritePage);

        int requestedPage = favouritePage;

        favourites.addSource(repository.getFavourites(favouritePage, PAGE_SIZE), resource -> {
            if (resource.getStatus() == Status.SUCCESS) {
                if ((resource.getData() == null) || (resource.getData().size() == 0) || (resource.getData().size() < PAGE_SIZE))
                    isLastFavouritePage = true;

                if (requestedPage < favouritePage)
                    return;

                favouritePage++;

                Log.d(TAG, "getMoreFavourites: requestedPage " + requestedPage + " hasContent " + !resource.getData().isEmpty());

                if (resource.getData() != null) {
                    allFavourites.addAll(resource.getData());
                    for (Routine routine : resource.getData())
                        Log.d(TAG, "getMoreFavourites: Added routine of id " + routine.getId() + " in page " + requestedPage);
                }

                favourites.setValue(Resource.success(allFavourites));
            } else {
                favourites.setValue(resource);
            }
        });
    }

}

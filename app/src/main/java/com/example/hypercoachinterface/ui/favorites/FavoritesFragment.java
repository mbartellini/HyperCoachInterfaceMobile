package com.example.hypercoachinterface.ui.favorites;

import android.content.ClipData;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hypercoachinterface.R;
import com.example.hypercoachinterface.backend.App;
import com.example.hypercoachinterface.backend.api.model.Error;
import com.example.hypercoachinterface.backend.api.model.Routine;
import com.example.hypercoachinterface.backend.repository.Resource;
import com.example.hypercoachinterface.backend.repository.RoutineRepository;
import com.example.hypercoachinterface.backend.repository.Status;
import com.example.hypercoachinterface.databinding.FragmentFavoritesBinding;
import com.example.hypercoachinterface.ui.Utils;
import com.example.hypercoachinterface.ui.adapter.ItemAdapter;
import com.example.hypercoachinterface.ui.adapter.RoutineSummary;
import com.example.hypercoachinterface.viewmodel.RepositoryViewModelFactory;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {

    private static final String TAG = "Fav";
    private FavoritesViewModel favoritesViewModel;
    private FragmentFavoritesBinding binding;
    private final List<Routine> dataSet = new ArrayList<>();
    private ItemAdapter adapter;
    private App app;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        app = (App) getActivity().getApplication();
        ViewModelProvider.Factory viewModelFactory = new RepositoryViewModelFactory<>(RoutineRepository.class, app.getRoutineRepository());
        favoritesViewModel = new ViewModelProvider(this, viewModelFactory).get(FavoritesViewModel.class);

        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        /* Handling screen rotation */
        int columns = 0;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        FragmentActivity activity = getActivity();
        if (activity != null) activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        else throw new IllegalStateException("activity in favourites is null");
        float screenWidth = displayMetrics.widthPixels;
        float navMenuWidth = getResources().getDimensionPixelSize(R.dimen.nav_menu_width);
        float routineCardWidth = getResources().getDimensionPixelSize(R.dimen.routine_card_width);
        float routineCardMargin = getResources().getDimensionPixelSize(R.dimen.margin_medium);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            screenWidth = screenWidth - navMenuWidth;

        while (screenWidth > routineCardWidth + (routineCardMargin * 2)) {
            columns++;
            screenWidth -= routineCardWidth + (routineCardMargin * 2);
        }

        if (columns == 0) {
            throw new IllegalStateException("Invalid screen size");
        }

        binding.allFavouritesRoutinesView.setLayoutManager(new GridLayoutManager(
                this.getContext(),
                columns,
                GridLayoutManager.VERTICAL,
                false));

        /* Getting favourites from api */
        List<RoutineSummary> favourites = new ArrayList<>();
        ItemAdapter adapter = new ItemAdapter(favourites);

        favoritesViewModel.getFavourites().observe(getViewLifecycleOwner(), r -> {
            if (r.getStatus() == Status.SUCCESS) {
                binding.favouritesFragmentProgressBar.setVisibility(View.GONE);
                binding.allFavouritesEmptyTextview.setVisibility(View.VISIBLE);
                favourites.clear();
                if (r.getData() != null) {
                    if (!r.getData().isEmpty())
                        binding.allFavouritesEmptyTextview.setVisibility(View.GONE);
                    Log.d("TAG", "onCreateView: ACA " + r.getData().isEmpty());

                    for(Routine routine : r.getData()) {
                        RoutineSummary rs = RoutineSummary.fromRoutine(routine, 0);
                        favourites.add(rs);
                        app.getReviewRepository().getReviews(routine.getId()).observe(getViewLifecycleOwner(), r2 -> {
                            if(r2.getStatus() == Status.SUCCESS) {
                                if(r2.getData().getTotalCount() == 0)
                                    rs.setFavCount(0);
                                else
                                    rs.setFavCount(Integer.parseInt(r2.getData().getContent().get(0).getReview()));
                                adapter.notifyItemRangeChanged(0, r.getData().size());
                            } else {
                                defaultResourceHandler(r2);
                            }
                        });
                    }
                    adapter.notifyDataSetChanged();
                }
            } else if (r.getStatus() == Status.LOADING) {
                binding.allFavouritesEmptyTextview.setVisibility(View.GONE);
                binding.favouritesFragmentProgressBar.setVisibility(View.VISIBLE);
            } else if (r.getStatus() == Status.ERROR) {
                Log.d(TAG, "Error");
                binding.allFavouritesEmptyTextview.setVisibility(View.GONE);
                binding.favouritesFragmentProgressBar.setVisibility(View.GONE);
                defaultResourceHandler(r);
            }
        });

        binding.allFavouritesRoutinesView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (! binding.allFavouritesRoutinesView.canScrollVertically(1)) {
                    Log.d("scroll", "onScrollStateChanged: Reached bottom of scrollview, requesting more routines");
                    favoritesViewModel.getMoreFavourites();
                }
            }
        });

        binding.allFavouritesRoutinesView.setAdapter(adapter);

        return root;
    }

    private void defaultResourceHandler(Resource<?> resource) {
        if (resource.getStatus() == Status.ERROR) {
            Error error = resource.getError();
            if (error.getCode() == Error.LOCAL_UNEXPECTED_ERROR) {
                binding.getRoot().removeAllViews();
                Snackbar snackbar = Snackbar.make(requireActivity(), binding.getRoot(), getResources().getString(R.string.no_connection), Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction(R.string.retry, v -> {
                    BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottom_nav_menu);
                    if(bottomNavigationView != null) {
                        bottomNavigationView.setSelectedItemId(R.id.navigation_favorites);
                    } else {
                        Navigation.findNavController(requireView()).navigate(R.id.navigation_favorites);
                    }
                });
                snackbar.show();
                return;
            }
            String message = Utils.getErrorMessage(getActivity(), error.getCode());
            Toast.makeText((Context) getViewLifecycleOwner(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

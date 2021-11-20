package com.example.hypercoachinterface.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hypercoachinterface.R;
import com.example.hypercoachinterface.backend.App;
import com.example.hypercoachinterface.backend.api.model.Error;
import com.example.hypercoachinterface.backend.api.model.Routine;
import com.example.hypercoachinterface.backend.repository.Resource;
import com.example.hypercoachinterface.backend.repository.Status;
import com.example.hypercoachinterface.databinding.FragmentHomeBinding;
import com.example.hypercoachinterface.ui.Utils;
import com.example.hypercoachinterface.ui.adapter.ItemAdapter;
import com.example.hypercoachinterface.ui.adapter.RoutineSummary;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private static final String TAG = "HOME";
    private static final int NUMBER_PER_CATEGORY = 5;
    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private App app;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        app = (App) getActivity().getApplication();


        getData();

        binding.gotoRecent.setOnClickListener(v -> {
            BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottom_nav_menu);
            if(bottomNavigationView != null) {
                bottomNavigationView.setSelectedItemId(R.id.navigation_search);
            } else {
                Navigation.findNavController(v).navigate(R.id.navigation_search);
            }
        });

        binding.gotoFavoritesRoutines.setOnClickListener(v -> {
            BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottom_nav_menu);
            if(bottomNavigationView != null) {
                bottomNavigationView.setSelectedItemId(R.id.navigation_favorites);
            } else {
                Navigation.findNavController(v).navigate(R.id.navigation_favorites);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
                        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
                    } else {
                        Navigation.findNavController(requireView()).navigate(R.id.navigation_home);
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
    public void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        /* All Routines */
        binding.recentRoutinesView.setLayoutManager(new LinearLayoutManager(
                this.getContext(),
                LinearLayoutManager.HORIZONTAL,
                false));

        List<RoutineSummary> recentRoutines = new ArrayList<>();
        ItemAdapter recentRoutinesAdapter = new ItemAdapter(recentRoutines);

        app.getRoutineRepository().getRoutines(0, 5, null, "desc").observe(getViewLifecycleOwner(), r -> {
            if(r.getStatus() == Status.SUCCESS) {
                binding.recentProgressBar.setVisibility(View.GONE);
                binding.recentEmptyTextview.setVisibility(View.GONE);
                if (r.getData().isEmpty())
                    binding.recentEmptyTextview.setVisibility(View.VISIBLE);
                for(Routine routine : r.getData()) {
                    RoutineSummary rs = RoutineSummary.fromRoutine(routine, 0);
                    recentRoutines.add(rs);
                    app.getReviewRepository().getReviews(routine.getId()).observe(getViewLifecycleOwner(), r2 -> {
                        if(r2.getStatus() == Status.SUCCESS) {
                            if(r2.getData().getTotalCount() == 0)
                                rs.setFavCount(0);
                            else
                                rs.setFavCount(Integer.parseInt(r2.getData().getContent().get(0).getReview()));
                            recentRoutinesAdapter.notifyItemRangeChanged(0, r.getData().size());
                        } else {
                            defaultResourceHandler(r2);
                        }
                    });

                }
                recentRoutinesAdapter.notifyItemRangeChanged(0, r.getData().size());
            } else if (r.getStatus() == Status.LOADING) {
                binding.recentProgressBar.setVisibility(View.VISIBLE);
                binding.recentEmptyTextview.setVisibility(View.GONE);
            } else if (r.getStatus() == Status.ERROR) {
                binding.recentProgressBar.setVisibility(View.GONE);
                binding.recentEmptyTextview.setVisibility(View.GONE);
                defaultResourceHandler(r);
            }
        });

        binding.recentRoutinesView.setAdapter(recentRoutinesAdapter);

        /* My Routines */
        binding.myRoutinesView.setLayoutManager(new LinearLayoutManager(
                this.getContext(),
                LinearLayoutManager.HORIZONTAL,
                false));

        List<RoutineSummary> myRoutines = new ArrayList<>();
        ItemAdapter myRoutinesAdapter = new ItemAdapter(myRoutines);

        app.getUserRepository().getUserRoutines().observe(getViewLifecycleOwner(), r -> {
            if(r.getStatus() == Status.SUCCESS) {
                binding.myRoutinesProgressBar.setVisibility(View.GONE);
                binding.myRoutinesEmptyTextview.setVisibility(View.GONE);
                if (r.getData().isEmpty())
                    binding.myRoutinesEmptyTextview.setVisibility(View.VISIBLE);
                for(Routine routine : r.getData()) {
                    RoutineSummary rs = RoutineSummary.fromRoutine(routine, 0);
                    myRoutines.add(rs);
                    app.getReviewRepository().getReviews(routine.getId()).observe(getViewLifecycleOwner(), r2 -> {
                        if(r2.getStatus() == Status.SUCCESS) {
                            if(r2.getData().getTotalCount() == 0)
                                rs.setFavCount(0);
                            else
                                rs.setFavCount(Integer.parseInt(r2.getData().getContent().get(0).getReview()));
                            myRoutinesAdapter.notifyItemRangeChanged(0, r.getData().size());
                        } else {
                            defaultResourceHandler(r2);
                        }
                    });

                }
                myRoutinesAdapter.notifyItemRangeChanged(0, r.getData().size());
            } else if (r.getStatus() == Status.LOADING) {
                binding.myRoutinesProgressBar.setVisibility(View.VISIBLE);
                binding.myRoutinesEmptyTextview.setVisibility(View.GONE);
            } else if (r.getStatus() == Status.ERROR) {
                binding.myRoutinesProgressBar.setVisibility(View.GONE);
                binding.myRoutinesEmptyTextview.setVisibility(View.GONE);
                defaultResourceHandler(r);
            }
        });

        binding.myRoutinesView.setAdapter(myRoutinesAdapter);


        /* Favourites */
        binding.favouritesRoutinesView.setLayoutManager(new LinearLayoutManager(
                this.getContext(),
                LinearLayoutManager.HORIZONTAL,
                false));
        List<RoutineSummary> favourites = new ArrayList<>();
        ItemAdapter favouriteAdapter = new ItemAdapter(favourites);

        app.getRoutineRepository().getFavourites(0, 5).observe(getViewLifecycleOwner(), r -> {
            if(r.getStatus() == Status.SUCCESS) {
                binding.favouritesProgressBar.setVisibility(View.GONE);
                binding.favouritesEmptyTextview.setVisibility(View.GONE);
                if (r.getData().isEmpty())
                    binding.favouritesEmptyTextview.setVisibility(View.VISIBLE);
                for(Routine routine : r.getData()) {
                    RoutineSummary rs = RoutineSummary.fromRoutine(routine, 0);
                    favourites.add(rs);
                    app.getReviewRepository().getReviews(routine.getId()).observe(getViewLifecycleOwner(), r2 -> {
                        if(r2.getStatus() == Status.SUCCESS) {
                            if(r2.getData().getTotalCount() == 0)
                                rs.setFavCount(0);
                            else
                                rs.setFavCount(Integer.parseInt(r2.getData().getContent().get(0).getReview()));
                            favouriteAdapter.notifyItemRangeChanged(0, r.getData().size());
                        } else {
                            defaultResourceHandler(r2);
                        }
                    });

                }
                favouriteAdapter.notifyItemRangeChanged(0, r.getData().size());
            } else if (r.getStatus() == Status.LOADING) {
                binding.favouritesEmptyTextview.setVisibility(View.GONE);
                binding.favouritesProgressBar.setVisibility(View.VISIBLE);
            } else if (r.getStatus() == Status.ERROR) {
                binding.favouritesEmptyTextview.setVisibility(View.GONE);
                binding.favouritesProgressBar.setVisibility(View.GONE);
                defaultResourceHandler(r);
            }
        });

        binding.favouritesRoutinesView.setAdapter(favouriteAdapter);
    }
}
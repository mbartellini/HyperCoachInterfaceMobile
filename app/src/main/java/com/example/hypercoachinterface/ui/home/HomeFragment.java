package com.example.hypercoachinterface.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hypercoachinterface.R;
import com.example.hypercoachinterface.backend.App;
import com.example.hypercoachinterface.backend.api.model.Routine;
import com.example.hypercoachinterface.backend.repository.Status;
import com.example.hypercoachinterface.databinding.FragmentHomeBinding;
import com.example.hypercoachinterface.ui.adapter.ItemAdapter;
import com.example.hypercoachinterface.ui.adapter.RoutineSummary;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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
            }
        });

        binding.favouritesRoutinesView.setAdapter(favouriteAdapter);

        binding.gotoRecent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottom_nav_menu);
                bottomNavigationView.setSelectedItemId(R.id.navigation_search);
            }
        });

        binding.gotoFavoritesRoutines.setOnClickListener(v -> {
            BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottom_nav_menu);
            bottomNavigationView.setSelectedItemId(R.id.navigation_favorites);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
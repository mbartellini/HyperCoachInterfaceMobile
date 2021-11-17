package com.example.hypercoachinterface.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.hypercoachinterface.backend.App;
import com.example.hypercoachinterface.backend.api.model.Routine;
import com.example.hypercoachinterface.backend.repository.Status;
import com.example.hypercoachinterface.databinding.FragmentHomeBinding;
import com.example.hypercoachinterface.ui.home.adapter.ItemAdapter;
import com.example.hypercoachinterface.ui.home.adapter.RoutineSummary;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final String TAG = "HOME";
    private static final int NUMBER_PER_CATEGORY = 5;
    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private ItemAdapter adapter;
    private App app;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        app = (App) getActivity().getApplication();


        /* All Routines */
        binding.allRoutinesView.setLayoutManager(new LinearLayoutManager(
                this.getContext(),
                LinearLayoutManager.HORIZONTAL,
                false));

        List<RoutineSummary> allRoutines = new ArrayList<>();
        ItemAdapter allRoutinesAdapter = new ItemAdapter(allRoutines);

        app.getRoutineRepository().getRoutines().observe(getViewLifecycleOwner(), r -> {
            if(r.getStatus() == Status.SUCCESS) {
                for(Routine routine : r.getData()) {
                    allRoutines.add(new RoutineSummary(routine.getId(), 0, routine.getName()));
                }
                allRoutinesAdapter.notifyItemRangeInserted(0, r.getData().size());
            }
        });
        binding.allRoutinesView.setAdapter(allRoutinesAdapter);

        /* My Routines */
        binding.myRoutinesView.setLayoutManager(new LinearLayoutManager(
                this.getContext(),
                LinearLayoutManager.HORIZONTAL,
                false));

        List<RoutineSummary> myRoutines = new ArrayList<>();
        ItemAdapter myRoutinesAdapter = new ItemAdapter(myRoutines);

        app.getUserRepository().getUserRoutines().observe(getViewLifecycleOwner(), r -> {
            if(r.getStatus() == Status.SUCCESS) {
                for(Routine routine : r.getData().getContent()) {
                    myRoutines.add(new RoutineSummary(routine.getId(), 0, routine.getName()));
                }
                myRoutinesAdapter.notifyItemRangeInserted(0, r.getData().getContent().size());
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
        app.getRoutineRepository().getFavourites(0, NUMBER_PER_CATEGORY).observe(getViewLifecycleOwner(), r -> {
            if (r.getStatus() == Status.SUCCESS) {
                Log.d(TAG, "onCreateView: " + r.getData().size());
                for(Routine routine : r.getData()) {
                    favourites.add(new RoutineSummary(routine.getId(), 0, routine.getName()));
                }
                favouriteAdapter.notifyItemRangeInserted(0, r.getData().size());
            }
        });
        binding.favouritesRoutinesView.setAdapter(favouriteAdapter);



        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
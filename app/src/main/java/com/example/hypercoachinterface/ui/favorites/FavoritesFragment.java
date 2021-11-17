package com.example.hypercoachinterface.ui.favorites;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hypercoachinterface.R;
import com.example.hypercoachinterface.backend.App;
import com.example.hypercoachinterface.backend.api.model.Routine;
import com.example.hypercoachinterface.backend.repository.RoutineRepository;
import com.example.hypercoachinterface.backend.repository.Status;
import com.example.hypercoachinterface.backend.repository.UserRepository;
import com.example.hypercoachinterface.databinding.FragmentFavoritesBinding;
import com.example.hypercoachinterface.databinding.FragmentHomeBinding;
import com.example.hypercoachinterface.ui.favorites.adapter.FavItemAdapter;
import com.example.hypercoachinterface.ui.home.HomeViewModel;
import com.example.hypercoachinterface.ui.home.adapter.ItemAdapter;
import com.example.hypercoachinterface.ui.profile.UserViewModel;
import com.example.hypercoachinterface.viewmodel.RepositoryViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {

    private FavoritesViewModel favoritesViewModel;
    private FragmentFavoritesBinding binding;
    private final List<Routine> dataSet = new ArrayList<>();
    private FavItemAdapter adapter;
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


        binding.allFavouritesRoutinesView.setLayoutManager(new GridLayoutManager(
                this.getContext(),
                columns,
                GridLayoutManager.VERTICAL,
                false));

        /* Getting favourites from api */
        List<Routine> favourites = new ArrayList<>();
        FavItemAdapter adapter = new FavItemAdapter(favourites);

        favoritesViewModel.getFavourites().observe(getViewLifecycleOwner(), r -> {
            if (r.getStatus() == Status.SUCCESS) {
                int prevSize = favourites.size();
                favourites.clear();
                if (r.getData() != null) {
                    int pos = binding.allFavouritesRoutinesView.getVerticalScrollbarPosition();
                    favourites.addAll(r.getData());
                    adapter.notifyDataSetChanged();
                    binding.allFavouritesRoutinesView.setVerticalScrollbarPosition(pos);
                }
            }
        });

        binding.allFavouritesRoutinesView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (! binding.allFavouritesRoutinesView.canScrollVertically(1)) {
                    Log.d("scroll", "onScrollStateChanged: AAA");
                    favoritesViewModel.getMoreFavourites();
                    adapter.notifyDataSetChanged();
                }
            }
        });

        binding.allFavouritesRoutinesView.setAdapter(adapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

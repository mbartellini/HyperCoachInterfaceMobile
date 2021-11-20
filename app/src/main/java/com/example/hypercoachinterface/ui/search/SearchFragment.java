package com.example.hypercoachinterface.ui.search;

import static com.example.hypercoachinterface.ui.search.SearchDialog.FILTER_REQUEST_KEY;
import static com.example.hypercoachinterface.ui.search.SearchDialog.SORT_CRITERIA_BUNDLE_KEY;

import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hypercoachinterface.R;
import com.example.hypercoachinterface.backend.App;
import com.example.hypercoachinterface.backend.api.model.Routine;
import com.example.hypercoachinterface.backend.api.model.RoutineCategory;
import com.example.hypercoachinterface.backend.repository.Status;
import com.example.hypercoachinterface.databinding.FragmentSearchBinding;
import com.example.hypercoachinterface.ui.Difficulty;
import com.example.hypercoachinterface.ui.SortCriteria;
import com.example.hypercoachinterface.ui.adapter.ItemAdapter;
import com.example.hypercoachinterface.ui.adapter.RoutineSummary;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragment";
    private static final String FILTER_DIALOG_TAG = "Filter Dialog Tag";
    private App app;
    private SearchViewModel searchViewModel;
    private FragmentSearchBinding binding;
    private final List<RoutineCategory> categories = new ArrayList<>();
    private final List<RoutineSummary> dataSet = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        app = (App) requireActivity().getApplication();

        searchViewModel = new ViewModelProvider(this,
                new SearchViewModel.SearchViewModelFactory(app.getRoutineRepository(), app.getCategoryRepository())).get(SearchViewModel.class);

        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        /* Horizontal or Vertical Layout Management */
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

        binding.filterRoutinesView.setLayoutManager(new GridLayoutManager(
                this.getContext(),
                columns,
                GridLayoutManager.VERTICAL,
                false));

        /* Fetching categories from API */
        searchViewModel.getCategories().observe(getViewLifecycleOwner(), r -> {
            if (r.getStatus() == Status.SUCCESS) {
                binding.searchButton.setClickable(true);
                if (r.getData() != null)
                    categories.addAll(r.getData());
            } else {
                binding.searchButton.setClickable(false);
            }
        });

        /* Fetching routines from API */
        ItemAdapter adapter = new ItemAdapter(dataSet);

        searchViewModel.getSearches().observe(getViewLifecycleOwner(), r -> {
            if (r.getStatus() == Status.SUCCESS) {
                binding.searchProgressBar.setVisibility(View.GONE);
                binding.searchEmptyTextview.setVisibility(View.VISIBLE);
                dataSet.clear();
                if (r.getData() != null) {
                    if (!r.getData().isEmpty())
                        binding.searchEmptyTextview.setVisibility(View.GONE);
                    for(Routine routine : r.getData()) {
                        dataSet.add(RoutineSummary.fromRoutine(routine, 0));
                    }
                    adapter.notifyDataSetChanged();
                }
            } else if (r.getStatus() == Status.LOADING) {
                binding.searchEmptyTextview.setVisibility(View.GONE);
                binding.searchProgressBar.setVisibility(View.VISIBLE);
            } else if (r.getStatus() == Status.ERROR) {
                binding.searchEmptyTextview.setVisibility(View.GONE);
                binding.searchProgressBar.setVisibility(View.GONE);
            }
        });

        binding.filterRoutinesView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (! binding.filterRoutinesView.canScrollVertically(1)) {
                    Log.d(TAG, "onScrollStateChanged: Reached bottom of scrollview, requesting more routines");
                    searchViewModel.getMoreSearches();
                    Log.d(TAG, "onScrollStateChanged: there are " + dataSet.size() + " routines");
                }
            }
        });

        binding.filterRoutinesView.setAdapter(adapter);

        /* Search Dialog Handling */
        binding.searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSearchDialog();
            }
        });
        getParentFragmentManager().setFragmentResultListener(FILTER_REQUEST_KEY, this, (requestKey, result) -> {
            RoutineCategory selectedCategory = null;
            Difficulty selectedDifficulty = null;
            SortCriteria selectedSortCriteria = null;
            Boolean selectedSortSense = false;

            if (result.containsKey(SearchDialog.CATEGORY_BUNDLE_KEY))
                selectedCategory = categories.stream().filter(routineCategory1 -> routineCategory1.getId().equals(result.getInt(SearchDialog.CATEGORY_BUNDLE_KEY))).findFirst().get();
            if (result.containsKey(SearchDialog.DIFFICULTY_BUNDLE_KEY))
                selectedDifficulty = Difficulty.valueOf(result.getString(SearchDialog.DIFFICULTY_BUNDLE_KEY));
            if (result.containsKey(SearchDialog.SORT_CRITERIA_BUNDLE_KEY))
                selectedSortCriteria = SortCriteria.valueOf(result.getString(SearchDialog.SORT_CRITERIA_BUNDLE_KEY));
            if (result.containsKey(SearchDialog.SORT_SENSE_BUNDLE_KEY))
            selectedSortSense = result.getBoolean(SearchDialog.SORT_SENSE_BUNDLE_KEY);
            Log.d(TAG, String.format("onFragmentResult: Received response from SearchDialog: %s, %s, %s, %s",
                    selectedCategory, selectedDifficulty, selectedSortCriteria, selectedSortSense));
            searchViewModel.setRoutineCategory(selectedCategory);
            searchViewModel.setDifficulty(selectedDifficulty);
            searchViewModel.setSortCriteria(selectedSortCriteria);
            searchViewModel.setSortSense(selectedSortSense);
            searchViewModel.restart();
        });

        return root;
    }

    public void openSearchDialog() {
        SearchDialog filterDialog = new SearchDialog(categories, searchViewModel.getRoutineCategory(), searchViewModel.getDifficulty(), searchViewModel.getSortCriteria(), searchViewModel.getSortSense());
        filterDialog.show(getParentFragmentManager(), FILTER_DIALOG_TAG);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
package com.example.hypercoachinterface.ui.search;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.hypercoachinterface.R;
import com.example.hypercoachinterface.backend.api.model.Routine;
import com.example.hypercoachinterface.backend.api.model.RoutineCategory;
import com.example.hypercoachinterface.ui.Difficulty;
import com.example.hypercoachinterface.ui.SortCriteria;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

public class SearchDialog extends AppCompatDialogFragment {

    public static final String FILTER_REQUEST_KEY = "filterRequestKey";
    public static final String CATEGORY_BUNDLE_KEY = "categoryBundleKey";
    public static final String DIFFICULTY_BUNDLE_KEY = "difficultyBundleKey";
    public static final String SORT_CRITERIA_BUNDLE_KEY = "sortCriteriaBundleKey";
    public static final String SORT_SENSE_BUNDLE_KEY = "sortSenseBundleKey";
    private static final String TAG = "FilterDialog";

    private List<RoutineCategory> categories;
    private ChipGroup categoryChipGroup;
    private ChipGroup difficultyChipGroup;
    private ChipGroup orderChipGroup;
    private MaterialButtonToggleGroup sortSenseToggleGroup;

    Map<Integer, Object> idToObject = new HashMap<>();
    private RoutineCategory selectedCategory;
    private Difficulty selectedDifficulty;
    private SortCriteria selectedSortCriteria;
    private Boolean selectedSortSense;

    public SearchDialog(List<RoutineCategory> categories, RoutineCategory selectedCategory, Difficulty selectedDifficulty, SortCriteria selectedSortCriteria, Boolean selectedSortSense) {
        Log.d(TAG, "SearchDialog: categories " + categories.size());
        this.categories = categories;
        this.selectedCategory = selectedCategory;
        this.selectedDifficulty = selectedDifficulty;
        this.selectedSortCriteria = selectedSortCriteria;
        this.selectedSortSense = selectedSortSense != null ? selectedSortSense : false;
        Log.d(TAG, String.format("SearchDialog: %s, %s, %s, %s", selectedCategory, selectedDifficulty, selectedSortCriteria, selectedSortSense));
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.search_dialog, null);

        builder.setView(view)
            .setTitle(getString(R.string.search_dialog_title))
            .setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                Log.d(TAG, "onClick: NegativeButton");
                Objects.requireNonNull(getDialog()).dismiss();
            }).setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                Log.d(TAG, "onClick: PositiveButton");
                Bundle result = new Bundle();
                selectedCategory = (RoutineCategory) idToObject.getOrDefault(categoryChipGroup.getCheckedChipId(), null);
                selectedDifficulty = (Difficulty) idToObject.getOrDefault(difficultyChipGroup.getCheckedChipId(), null);
                selectedSortCriteria = (SortCriteria) idToObject.getOrDefault(orderChipGroup.getCheckedChipId(), null);
            Log.d(TAG, "onCreateDialog: SENDING CRITERIA " + selectedSortCriteria);
                selectedSortSense = sortSenseToggleGroup.getCheckedButtonId() == R.id.asc_button;
                if (selectedCategory != null) result.putInt(CATEGORY_BUNDLE_KEY, selectedCategory.getId());
                if (selectedDifficulty != null) result.putString(DIFFICULTY_BUNDLE_KEY, selectedDifficulty.toString());
                if (selectedSortCriteria != null) result.putString(SORT_CRITERIA_BUNDLE_KEY, selectedSortCriteria.toString());
                result.putBoolean(SORT_SENSE_BUNDLE_KEY, selectedSortSense);
                getParentFragmentManager().setFragmentResult(FILTER_REQUEST_KEY, result);
                Objects.requireNonNull(getDialog()).dismiss();
            });

        categoryChipGroup = view.findViewById(R.id.categories_chip_group);
        for (RoutineCategory routineCategory : categories) {
            Chip chip = (Chip) inflater.inflate(R.layout.chip, categoryChipGroup, false);
            chip.setId(View.generateViewId());
            chip.setText(getCategoryName(routineCategory));
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> chip.setCloseIconVisible(isChecked));
            categoryChipGroup.addView(chip);
            if (selectedCategory != null && routineCategory.getId().equals(selectedCategory.getId()))
                categoryChipGroup.check(chip.getId());
            idToObject.put(chip.getId(), routineCategory);
        }

        difficultyChipGroup = view.findViewById(R.id.difficulty_chip_group);
        for (Difficulty difficulty : Difficulty.values()) {
            Chip chip = (Chip) inflater.inflate(R.layout.chip, difficultyChipGroup, false);
            chip.setId(View.generateViewId());
            chip.setText(getString(difficulty.getStringId()));
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> chip.setCloseIconVisible(isChecked));
            difficultyChipGroup.addView(chip);
            if (difficulty.equals(selectedDifficulty))
                difficultyChipGroup.check(chip.getId());
            idToObject.put(chip.getId(), difficulty);
        }

        orderChipGroup = view.findViewById(R.id.order_chip_group);
        for (SortCriteria sortCriteria : SortCriteria.values()) {
            Chip chip = (Chip) inflater.inflate(R.layout.chip, orderChipGroup, false);
            chip.setId(View.generateViewId());
            chip.setText(getString(sortCriteria.getStringId()));
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> chip.setCloseIconVisible(isChecked));
            orderChipGroup.addView(chip);
            if (sortCriteria.equals(selectedSortCriteria))
                orderChipGroup.check(chip.getId());
            idToObject.put(chip.getId(), sortCriteria);
        }

        sortSenseToggleGroup = view.findViewById(R.id.asc_button_group);
        sortSenseToggleGroup.check(selectedSortSense ? R.id.asc_button : R.id.desc_button);

        return builder.create();
    }

    private String getCategoryName(RoutineCategory routineCategory) {
        switch (routineCategory.getId()) {
            case 1:
                return getResources().getString(R.string.hit);
            case 2:
                return getResources().getString(R.string.cardio);
            case 3:
                return getResources().getString(R.string.calisthenics);
            case 4:
                return getResources().getString(R.string.bodybuilding);
            default:
                return getResources().getString(R.string.no_category);
        }
    }

}

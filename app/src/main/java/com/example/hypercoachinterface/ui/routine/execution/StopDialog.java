package com.example.hypercoachinterface.ui.routine.execution;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.hypercoachinterface.R;
import com.example.hypercoachinterface.backend.App;
import com.example.hypercoachinterface.backend.api.model.RoutineCategory;
import com.example.hypercoachinterface.ui.Difficulty;
import com.example.hypercoachinterface.ui.SortCriteria;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StopDialog extends AppCompatDialogFragment {

    public static final String STOP_REQUEST_KEY = "stopRequestKey";
    private static final String TAG = "StopDialog";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.stop_execution_dialog, null);

        builder.setView(view)
                .setTitle(getString(R.string.stop_dialog_title))
                .setNegativeButton(getString(R.string.stop_execution), (dialog, which) -> {
                    Log.d(TAG, "onClick: StopExecution");
                    getParentFragmentManager().setFragmentResult(STOP_REQUEST_KEY, new Bundle());
                    Objects.requireNonNull(getDialog()).dismiss();
                }).setPositiveButton(getString(R.string.continue_execution), (dialog, which) -> {
                    Log.d(TAG, "onClick: ContinueExecution");
                    Objects.requireNonNull(getDialog()).dismiss();
                });

        return builder.create();
    }

}


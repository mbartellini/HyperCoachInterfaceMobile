package com.example.hypercoachinterface.ui.search;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.hypercoachinterface.R;
import com.google.android.material.chip.ChipGroup;

public class FilterDialog extends AppCompatDialogFragment {

    private static final String TAG = "FilterDialog";
    private ChipGroup chipGroup;
    private FilterDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.filter_dialog, null);

        builder.setView(view)
                .setTitle(getString(R.string.filter_by_text))
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "onClick: NegativeButton");
                        getDialog().dismiss();
                    }
                }).setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "onClick: PositiveButton");
                        listener.setFilter();
                        getDialog().dismiss();
                    }
                });

        chipGroup = view.findViewById(R.id.categories_chip_group);

        return builder.create();
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (FilterDialogListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getContext().toString() + " must implement FilterDialogListener");
        }
    }

    public interface FilterDialogListener {
        void setFilter();
    }

}

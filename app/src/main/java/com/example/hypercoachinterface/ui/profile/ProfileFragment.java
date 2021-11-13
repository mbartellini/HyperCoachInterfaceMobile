package com.example.hypercoachinterface.ui.profile;

import static com.example.hypercoachinterface.backend.repository.Status.SUCCESS;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.hypercoachinterface.backend.App;
import com.example.hypercoachinterface.backend.AppPreferences;
import com.example.hypercoachinterface.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    private FragmentProfileBinding binding;
    private Button logoutBtn;
    private App app;
    private static final String TAG = "logout";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        app = (App) getActivity().getApplication();

        logoutBtn = binding.logout;
        logoutBtn.setOnClickListener(v -> {
            app.getUserRepository().logout().observe(getViewLifecycleOwner(), r -> {
                if (r.getStatus() == SUCCESS) {
                    Log.d(TAG, "Success");
                }
                app.getPreferences().setAuthToken(null);
            });
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
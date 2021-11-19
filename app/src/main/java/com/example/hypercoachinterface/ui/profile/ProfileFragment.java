package com.example.hypercoachinterface.ui.profile;

import static com.example.hypercoachinterface.backend.repository.Status.LOADING;
import static com.example.hypercoachinterface.backend.repository.Status.SUCCESS;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.hypercoachinterface.R;
import com.example.hypercoachinterface.backend.App;
import com.example.hypercoachinterface.backend.AppPreferences;
import com.example.hypercoachinterface.ui.Utils;
import com.example.hypercoachinterface.ui.login.LoginActivity;
import com.example.hypercoachinterface.backend.api.model.Error;
import com.example.hypercoachinterface.backend.api.model.User;
import com.example.hypercoachinterface.backend.repository.Resource;
import com.example.hypercoachinterface.backend.repository.Status;
import com.example.hypercoachinterface.backend.repository.UserRepository;
import com.example.hypercoachinterface.databinding.FragmentProfileBinding;
import com.example.hypercoachinterface.ui.login.LoginActivity;
import com.example.hypercoachinterface.viewmodel.RepositoryViewModelFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class ProfileFragment extends Fragment {

    private UserViewModel userViewModel;
    private FragmentProfileBinding binding;
    private Button logoutBtn;
    private App app;
    private static final String TAG = "logout";
    private TextView usernameTextView, nameTextView, lastNameTextView, emailTextView, genderTextView, birthTextView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        app = (App) getActivity().getApplication();

        ViewModelProvider.Factory viewModelFactory = new RepositoryViewModelFactory<>(UserRepository.class, app.getUserRepository());
        userViewModel = new ViewModelProvider(this, viewModelFactory).get(UserViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        userViewModel.getUser().observe(getViewLifecycleOwner(), r-> {
            if (r.getStatus() == Status.SUCCESS) {
                binding.profileProgressBar.setVisibility(View.GONE);
                Log.d(TAG, "Success");
                updateProfile(r.getData());
            } else if (r.getStatus() == Status.LOADING) {
                binding.profileProgressBar.setVisibility(View.VISIBLE);
                defaultResourceHandler(r);
            } else if (r.getStatus() == Status.ERROR) {
                binding.profileProgressBar.setVisibility(View.GONE);
            }
        });

        logoutBtn = binding.logout;
        logoutBtn.setOnClickListener(v -> {
            app.getUserRepository().logout().observe(getViewLifecycleOwner(), r -> {
                if (r.getStatus() == SUCCESS) {
                    binding.profileProgressBar.setVisibility(View.GONE);
                    Log.d(TAG, "Success");
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    app.getPreferences().setAuthToken(null);
                    getActivity().finish();
                } else if (r.getStatus() == Status.LOADING) {
                    binding.logout.setClickable(false);
                    binding.profileProgressBar.setVisibility(View.VISIBLE);
                } else if (r.getStatus() == Status.ERROR) {
                    binding.logout.setClickable(true);
                    binding.profileProgressBar.setVisibility(View.GONE);
                }
            });
        });

        return root;
    }

    private void updateProfile(User data) {
        binding.username.setText(data.getUsername());
        binding.name.setText(data.getFirstName());
        binding.lastName.setText(data.getLastName());
        binding.email.setText(data.getEmail());
        binding.gender.setText(translateGender(data.getGender()));

        if (data.getMetadata() != null && !data.getMetadata().getImgSrc().equals("")) {
            Utils.setImageFromBase64(binding.imageView, data.getAvatarUrl());
        }

        DateFormat df = android.text.format.DateFormat.getDateFormat(getContext());
        binding.birth.setText(df.format(data.getBirthdate()));

        if (data.getMetadata() != null) {
            Utils.setImageFromBase64(binding.imageView, data.getMetadata().getImgSrc());
        }

    }

    private void defaultResourceHandler(Resource<?> resource) {
        switch (resource.getStatus()) {
            case LOADING:
                Log.d(TAG, "Loading");
                break;
            case ERROR:
                Error error = resource.getError();
                String message = error.getDescription() + " " + error.getCode();
                Log.d(TAG, message);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private String translateGender(String gender) {
        switch (gender) {
            case "male": return getString(R.string.male);
            case "female": return getString(R.string.female);
            default: return getString(R.string.other);
        }
    }
}
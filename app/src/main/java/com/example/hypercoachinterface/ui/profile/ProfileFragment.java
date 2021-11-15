package com.example.hypercoachinterface.ui.profile;

import static com.example.hypercoachinterface.backend.repository.Status.SUCCESS;

import android.app.Activity;
import android.content.Intent;
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

import com.example.hypercoachinterface.R;
import com.example.hypercoachinterface.backend.App;
import com.example.hypercoachinterface.backend.AppPreferences;
import com.example.hypercoachinterface.backend.api.model.Error;
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
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        usernameTextView = binding.username;
        nameTextView = binding.name;
        lastNameTextView = binding.lastName;
        emailTextView = binding.email;
        genderTextView = binding.gender;
        birthTextView = binding.birth;
        userViewModel.getUser().observe(getViewLifecycleOwner(), r-> {
            if (r.getStatus() == Status.SUCCESS) {
                Log.d(TAG, "Success");
                usernameTextView.setText(r.getData().getUsername());
                nameTextView.setText(r.getData().getFirstName());
                lastNameTextView.setText(r.getData().getLastName());
                emailTextView.setText(r.getData().getEmail());
                genderTextView.setText(translateGender(r.getData().getGender()));
                DateFormat df = android.text.format.DateFormat.getDateFormat(getContext());
                birthTextView.setText(df.format(r.getData().getBirthdate()));
            } else if (r.getStatus() == Status.LOADING) {
                defaultResourceHandler(r);
            }
        });

        logoutBtn = binding.logout;
        logoutBtn.setOnClickListener(v -> {
            app.getUserRepository().logout().observe(getViewLifecycleOwner(), r -> {
                if (r.getStatus() == SUCCESS) {
                    Log.d(TAG, "Success");
                    startActivity(new Intent(getContext(), LoginActivity.class));
                    app.getPreferences().setAuthToken(null);
                }
            });
        });

        return root;
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
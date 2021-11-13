package com.example.hypercoachinterface.ui.login;

import static com.example.hypercoachinterface.backend.repository.Status.ERROR;
import static com.example.hypercoachinterface.backend.repository.Status.LOADING;
import static com.example.hypercoachinterface.backend.repository.Status.SUCCESS;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hypercoachinterface.R;
import com.example.hypercoachinterface.backend.App;
import com.example.hypercoachinterface.backend.api.model.Credentials;
import com.example.hypercoachinterface.backend.api.model.Error;
import com.example.hypercoachinterface.backend.repository.Resource;
import com.example.hypercoachinterface.backend.repository.Status;
import com.example.hypercoachinterface.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    public static final String TAG = "UI";
    private ActivityLoginBinding binding;
    private App app;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        app = ((App)getApplication());

        Button loginBtn = findViewById(R.id.login);
        EditText username = binding.username, password = binding.password;
        loginBtn.setOnClickListener(v -> {
            Log.d(TAG, "Username: " + username.getText().toString());
            Log.d(TAG, "Password: " + password.getText().toString());
            Credentials credentials = new Credentials(username.getText().toString(), password.getText().toString());
            app.getUserRepository().login(credentials).observe(this, r -> {
                if (r.getStatus() == SUCCESS) {
                    Log.d(TAG, "Success");
                    binding.loading.setVisibility(View.GONE);
                    binding.result.setText("Success");
                    app.getPreferences().setAuthToken(r.getData().getToken());
                } else {
                    defaultResourceHandler(r);
                }
            });
        });

    }

    private void defaultResourceHandler(Resource<?> resource) {
        switch (resource.getStatus()) {
            case LOADING:
                Log.d(TAG, "Loading");
                binding.loading.setVisibility(View.VISIBLE);
                break;
            case ERROR:
                binding.loading.setVisibility(View.GONE);
                Error error = resource.getError();
                String message = error.getDescription() + " " + error.getCode();
                Log.d(TAG, message);
                binding.result.setText(message);
                break;
        }
    }



}
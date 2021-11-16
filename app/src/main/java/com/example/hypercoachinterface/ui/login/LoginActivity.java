package com.example.hypercoachinterface.ui.login;

import static com.example.hypercoachinterface.backend.repository.Status.ERROR;
import static com.example.hypercoachinterface.backend.repository.Status.LOADING;
import static com.example.hypercoachinterface.backend.repository.Status.SUCCESS;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hypercoachinterface.MainActivity;
import com.example.hypercoachinterface.R;
import com.example.hypercoachinterface.backend.App;
import com.example.hypercoachinterface.backend.api.model.Credentials;
import com.example.hypercoachinterface.backend.api.model.Error;
import com.example.hypercoachinterface.backend.repository.Resource;
import com.example.hypercoachinterface.backend.repository.Status;
import com.example.hypercoachinterface.databinding.ActivityLoginBinding;
import com.google.android.material.textfield.TextInputEditText;

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
        EditText username = findViewById(R.id.username), password = findViewById(R.id.password);
        loginBtn.setEnabled(true);
        loginBtn.setOnClickListener(v -> {
            Log.d(TAG, "Username: " + username.getText().toString());
            Log.d(TAG, "Password: " + password.getText().toString());
            Credentials credentials = new Credentials(username.getText().toString(), password.getText().toString());
            app.getUserRepository().login(credentials).observe(this, r -> {
                if (r.getStatus() == SUCCESS) {
                    Log.d(TAG, "Success");
                    binding.loading.setVisibility(View.GONE);
                    Toast.makeText(this, getResources().getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                    app.getPreferences().setAuthToken(r.getData().getToken());
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
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
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                break;
        }
    }



}
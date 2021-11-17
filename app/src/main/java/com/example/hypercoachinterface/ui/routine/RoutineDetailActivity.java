package com.example.hypercoachinterface.ui.routine;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.hypercoachinterface.R;

public class RoutineDetailActivity extends AppCompatActivity {

    private static final String TAG = "RoutineDetailActivity";
    private int routineId = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_detail);

        Bundle b = getIntent().getExtras();
        if(b != null)
            routineId = b.getInt("routineId");

        Log.d(TAG, "onCreate: " + String.valueOf(routineId));

    }
}
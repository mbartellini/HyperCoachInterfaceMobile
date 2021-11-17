package com.example.hypercoachinterface.ui.routine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.hypercoachinterface.R;
import com.example.hypercoachinterface.backend.App;
import com.example.hypercoachinterface.backend.api.model.Routine;
import com.example.hypercoachinterface.backend.api.model.RoutineCycle;
import com.example.hypercoachinterface.backend.repository.RoutineRepository;
import com.example.hypercoachinterface.backend.repository.Status;
import com.example.hypercoachinterface.databinding.ActivityMainBinding;
import com.example.hypercoachinterface.databinding.ActivityRoutineDetailBinding;
import com.example.hypercoachinterface.databinding.FragmentFavoritesBinding;
import com.example.hypercoachinterface.ui.Utils;
import com.example.hypercoachinterface.ui.adapter.ItemAdapter;
import com.example.hypercoachinterface.ui.adapter.RoutineSummary;
import com.example.hypercoachinterface.ui.favorites.FavoritesViewModel;
import com.example.hypercoachinterface.viewmodel.RepositoryViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class RoutineDetailActivity extends AppCompatActivity {

    private static final String TAG = "RoutineDetailActivity";
    private ActivityRoutineDetailBinding binding;
    private App app;
    private int routineId = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_detail);

        app = (App) getApplication();

        binding = ActivityRoutineDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle b = getIntent().getExtras();
        if (b == null) {
            // TODO: Handle error
            // Volver a la página principal mostrando un error de invalid id
        }
        routineId = b.getInt("routineId");
        Log.d(TAG, "onCreate: " + String.valueOf(routineId));

        List<RoutineCycle> cycles = new ArrayList<>();
        CycleAdapter adapter = new CycleAdapter(cycles);
        binding.cycleCardsView.setAdapter(adapter);

        app.getRoutineRepository().getRoutine(routineId).observe(this, r -> {
            if (r.getStatus() == Status.SUCCESS) {
                fillActivityData(r.getData());
                cycles.addAll(r.getData().getMetadata().getCycles());
                adapter.notifyItemRangeInserted(0, cycles.size());
            }
        });

    }

    private void fillActivityData(Routine routine) {
        binding.routineTitle.setText(routine.getName());
        binding.routineDetail.setText(routine.getDetail());
        binding.routineCategory.setText(routine.getRoutineCategory().getName());
        binding.routineDifficulty.setText(routine.getDifficulty());
        if (routine.getMetadata() != null && routine.getMetadata().getEquipment() != null)
            binding.routineEquipment.setText(routine.getMetadata().getEquipment().toString());
        binding.routineLikes.setText("TODO");
        if (routine.getMetadata() != null)
            Utils.setImageFromBase64(binding.routineImage, routine.getMetadata().getImgSrc());
    }
}
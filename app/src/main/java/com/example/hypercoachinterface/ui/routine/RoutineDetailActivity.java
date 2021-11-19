package com.example.hypercoachinterface.ui.routine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.hypercoachinterface.R;
import com.example.hypercoachinterface.backend.App;
import com.example.hypercoachinterface.backend.api.model.Exercise;
import com.example.hypercoachinterface.backend.api.model.Routine;
import com.example.hypercoachinterface.backend.api.model.RoutineCycle;
import com.example.hypercoachinterface.backend.api.model.RoutineExercise;
import com.example.hypercoachinterface.backend.repository.RoutineRepository;
import com.example.hypercoachinterface.backend.repository.Status;
import com.example.hypercoachinterface.databinding.ActivityMainBinding;
import com.example.hypercoachinterface.databinding.ActivityRoutineDetailBinding;
import com.example.hypercoachinterface.databinding.FragmentFavoritesBinding;
import com.example.hypercoachinterface.ui.Utils;
import com.example.hypercoachinterface.ui.adapter.ItemAdapter;
import com.example.hypercoachinterface.ui.adapter.RoutineSummary;
import com.example.hypercoachinterface.ui.favorites.FavoritesViewModel;
import com.example.hypercoachinterface.ui.routine.execution.ExecuteRoutineActivity;
import com.example.hypercoachinterface.viewmodel.RepositoryViewModelFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        if (b == null)
            invalidRoutineHandler();
        routineId = b.getInt("routineId");
        Log.d(TAG, "onCreate: " + String.valueOf(routineId));

        List<RoutineCycle> cycles = new ArrayList<>();
        Map<Integer, Exercise> exerciseMap = new HashMap<>();
        CycleAdapter adapter = new CycleAdapter(cycles, exerciseMap);
        binding.cycleCardsView.setAdapter(adapter);

        app.getRoutineRepository().getRoutine(routineId).observe(this, r -> {
            if (r.getStatus() == Status.SUCCESS) {
                if (r.getData() == null || r.getData().getMetadata() == null) {
                    invalidRoutineHandler();
                }
                fillActivityData(r.getData());
                cycles.addAll(r.getData().getMetadata().getCycles());
                adapter.notifyItemRangeInserted(0, cycles.size());
                for (int i = 0; i < cycles.size(); i++) {
                    for (RoutineExercise routineExercise : cycles.get(i).getExercises()) {
                        int finalI = i;
                        app.getExerciseRepository().getExercise(routineExercise.getId()).observe(this, r2 -> {
                            if (r2.getStatus() == Status.SUCCESS) {
                                exerciseMap.put(routineExercise.getId(), r2.getData());
                                adapter.notifyItemChanged(finalI);
                            }
                        });
                    }
                }

                binding.startRoutine.setOnClickListener(view -> {
                    Context context = binding.getRoot().getContext();
                    Intent intent = new Intent(context, ExecuteRoutineActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("routineId", this.routineId);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                    // context.finish();
                });

            }
        });

    }

    private void invalidRoutineHandler() {
        // TODO: Go back to previous or to home
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.routine_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.fav_btn) {
            // fav
        } else if (id == R.id.share_btn) {
            // share
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fillActivityData(Routine routine) {
        binding.routineTitle.setText(routine.getName());
        binding.routineDetail.setText(routine.getDetail());
        binding.routineCategoryChip.setText(routine.getRoutineCategory().getName());
        binding.routineDifficultyChip.setText(routine.getDifficulty());
        if (routine.getMetadata() != null) {
            Utils.setImageFromBase64(binding.routineImage, routine.getMetadata().getImgSrc());
            if (routine.getMetadata().getEquipment() != null) {
                if (routine.getMetadata().getEquipment()) {
                    binding.routineEquipmentChip.setText(getString(R.string.yes_equipment));
                } else {
                    binding.routineEquipmentChip.setText(getString(R.string.no_equipment));
                }
            }
        }
        app.getReviewRepository().getReviews(routineId).observe(this, r -> {
            if(r.getStatus() == Status.SUCCESS) {
                String favCount;
                if(r.getData().getTotalCount() == 0)
                    favCount = "0";
                else
                    favCount = r.getData().getContent().get(0).getReview();
                favCount += " " + new String(Character.toChars(0x2605));
                binding.routineFavsChip.setText(favCount);
            }
        });
    }
}
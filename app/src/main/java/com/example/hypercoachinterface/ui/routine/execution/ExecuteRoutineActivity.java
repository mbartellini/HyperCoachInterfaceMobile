package com.example.hypercoachinterface.ui.routine.execution;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.widget.ProgressBar;

import com.example.hypercoachinterface.R;
import com.example.hypercoachinterface.backend.App;
import com.example.hypercoachinterface.backend.api.model.Exercise;
import com.example.hypercoachinterface.backend.api.model.Routine;
import com.example.hypercoachinterface.backend.api.model.RoutineCycle;
import com.example.hypercoachinterface.backend.api.model.RoutineExercise;
import com.example.hypercoachinterface.backend.repository.Status;
import com.example.hypercoachinterface.databinding.ActivityExecuteRoutineBinding;
import com.example.hypercoachinterface.ui.Utils;
import com.example.hypercoachinterface.ui.routine.CycleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ExecuteRoutineActivity extends AppCompatActivity {

    private static final String COUNTER_NAME = "ExecuteRoutineActivity";
    private ActivityExecuteRoutineBinding binding;
    private App app;
    private int currentCycle = 0;
    private int currentExercise = 0;
    private int currentCycleReps = 1;
    private final List<RoutineCycle> cycles = new ArrayList<>();
    private final Map<Integer, Exercise> exerciseMap = new HashMap<>();
    private final CycleAdapter adapter = new CycleAdapter(cycles, exerciseMap, this);
    ProgressBar progressBar;
    Integer progress = 0, time = null;
    MyCountDownTimer myCountDownTimer = null;
    Boolean paused = false;


    private void invalidRoutineHandler() {
        // TODO: Go back to previous or to home
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_execute_routine);

        app = (App) getApplication();

        binding = ActivityExecuteRoutineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle b = getIntent().getExtras();
        if (b == null) {
            invalidRoutineHandler();
            finish();
            return;
        }
        int routineId = b.getInt("routineId");

        if (binding.cycleCardsView != null) binding.cycleCardsView.setAdapter(adapter);

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
                progressBar = binding.progressBar;
                myCountDownTimer = new MyCountDownTimer(COUNTER_NAME);
                if (cycles.size() > currentCycle) {
                    RoutineCycle cycle = cycles.get(currentCycle);
                    if (cycle != null && cycle.getExercises().size() > currentExercise) {
                        RoutineExercise ex = cycle.getExercises().get(currentExercise);
                        Exercise exercise = exerciseMap.getOrDefault(ex.getId(), null);
                        if (exercise != null) {
                            if (ex.getLimitType().equals("repeticiones") || ex.getLimitType().equals("repetitions")) {
                                binding.timer.setText(ex.getLimit() + R.string.reps);
                                paused = true;
                            } else {
                                binding.timer.setText(ex.getLimit());
                                time = ex.getLimit();
                                if (binding.progressBar != null) binding.progressBar.setMax(time);
                            }
                            if (binding.excerciseTitle != null) binding.excerciseTitle.setText(exercise.getName());
                            if (binding.cycleTitle != null) binding.cycleTitle.setText(cycle.getName());
                            if (binding.remainingCycles != null) binding.remainingCycles.setText(String.format("x%d rep%s.", cycle.getRepetitions(), cycle.getRepetitions() > 1 ? "s" : ""));
                            binding.stopButton.setOnClickListener(view -> {
                                // TODO: go back to routine view
                                finish();
                            });
                            currentCycleReps = cycle.getRepetitions();
                            binding.pauseButton.setOnClickListener(view -> {
                                paused = true;
                            });
                            binding.playButton.setOnClickListener(view -> {
                                paused = false;
                            });
                            binding.skipButton.setOnClickListener(view -> {
                                time = progress;
                            });

                            if (binding.excerciseExecImage != null) {
                                Utils.setImageFromBase64(binding.excerciseExecImage, exercise.getMetadata().getImg_src());
                            }
                            myCountDownTimer.start();
                        } else {
                            finish();
                        }
                    } else {
                        finish();
                    }
                } else {
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.routine_detail_menu, menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.fav_btn) {
//            // fav
//        } else if (id == R.id.share_btn) {
//            // share
//        } else if (id == android.R.id.home) {
//            finish();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    private void fillActivityData(Routine routine) {
//        binding.routineTitle.setText(routine.getName());

    }

    public class MyCountDownTimer extends Timer {

        public MyCountDownTimer(String name) {
            super(name);
        }

        public void start() {
            schedule(new TimerTask() {
                @Override
                public void run() {
                    if (!paused && time != Integer.MAX_VALUE) {
                        binding.timer.setText(time - progress);
                        progress+=1;
                        if (binding.progressBar != null) binding.progressBar.setProgress(progress);
                    }
                    if (time <= progress) {
                        progress = 0;
                        currentExercise += 1;
                        if (currentExercise > cycles.get(currentCycle).getExercises().size()) {
                            currentExercise = 0;
                            if (cycles.get(currentCycle).getRepetitions() > 1) {
                                currentCycleReps-=1;
                            } else {
                                currentCycle += 1;
                                currentCycleReps = cycles.get(currentCycle).getRepetitions();
                            }
                        }
                        if (cycles.size() > currentCycle) {
                            if (binding.cycleCardsView != null) binding.cycleCardsView.setAdapter(adapter);

                            RoutineCycle cycle = cycles.get(currentCycle);
                            if (cycle != null && cycle.getExercises().size() > currentExercise) {
                                RoutineExercise ex = cycle.getExercises().get(currentExercise);
                                Exercise exercise = exerciseMap.getOrDefault(ex.getId(), null);
                                if (exercise != null) {
                                    if (ex.getLimitType().equals("repeticiones") || ex.getLimitType().equals("repetitions")) {
                                        binding.timer.setText(ex.getLimit() + R.string.reps);
                                        paused = true;
                                        time = Integer.MAX_VALUE;
                                    } else {
                                        binding.timer.setText(ex.getLimit());
                                        time = ex.getLimit();
                                        if (binding.progressBar != null) binding.progressBar.setMax(time);
                                    }
                                    if (binding.excerciseTitle != null)
                                        binding.excerciseTitle.setText(exercise.getName());
                                    if (binding.cycleTitle != null)
                                        binding.cycleTitle.setText(cycle.getName());
                                    if (binding.remainingCycles != null)
                                        binding.remainingCycles.setText(String.format("x%d rep%s.", cycle.getRepetitions(), cycle.getRepetitions() > 1 ? "s" : ""));

                                    if (binding.excerciseExecImage != null) {
                                        Utils.setImageFromBase64(binding.excerciseExecImage, exercise.getMetadata().getImg_src());
                                    }
                                } else {
                                    finish();
                                }
                            } else {
                                finish();
                            }
                        } else {
                            finish();
                        }
                    }
                }
            }, 1000);  // 1 sec
        }

    }

}
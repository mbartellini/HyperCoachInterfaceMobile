package com.example.hypercoachinterface.ui.routine.execution;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
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
        if (cycles.size() == 0) {

            int routineId = b.getInt("routineId");

            if (binding.cycleCardsView != null) binding.cycleCardsView.setAdapter(adapter);

            app.getRoutineRepository().getRoutine(routineId).observe(this, r -> {
                if (r.getStatus() == Status.SUCCESS) {
                    if (r.getData() == null || r.getData().getMetadata() == null) {
                        invalidRoutineHandler();
                    }
                    cycles.addAll(r.getData().getMetadata().getCycles());
                    adapter.notifyItemRangeInserted(0, cycles.size());
                    for (int i = 0; i < cycles.size(); i++) {
                        for (RoutineExercise routineExercise : cycles.get(i).getExercises()) {
                            int finalI = i;
                            app.getExerciseRepository().getExercise(routineExercise.getId()).observe(this, r2 -> {
                                if (r2.getStatus() == Status.SUCCESS) {
                                    exerciseMap.put(routineExercise.getId(), r2.getData());
                                    adapter.notifyItemChanged(finalI);
                                    if (finalI == currentCycle) {
                                        fillActivityData(cycles.get(currentCycle), exerciseMap.get(cycles.get(currentCycle).getExercises().get(currentExercise).getId()), cycles.get(currentCycle).getExercises().get(currentExercise));
                                        myCountDownTimer.start();
                                    }
                                }
                            });
                        }
                    }
                }
            });
        } else {
            if (binding.cycleCardsView != null) binding.cycleCardsView.setAdapter(adapter);
            fillActivityData(cycles.get(currentCycle), exerciseMap.get(cycles.get(currentCycle).getExercises().get(currentExercise).getId()), cycles.get(0).getExercises().get(0));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.routine_detail_menu, menu);
        return true;
    }

    private void fillActivityData(RoutineCycle cycle, Exercise exercise, RoutineExercise routineExercise) {
        if (binding.excerciseTitle != null)
            binding.excerciseTitle.setText(exercise.getName());
        if (binding.cycleTitle != null)
            binding.cycleTitle.setText(cycle.getName());

        binding.stopButton.setOnClickListener(view -> {
            myCountDownTimer.cancel();
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

        if (routineExercise.getLimitType().equals("repeticiones") || routineExercise.getLimitType().equals("repetitions")) {
            binding.timer.setText(String.format("x%d rep%s.", routineExercise.getLimit(), cycle.getRepetitions() > 1 ? "s" : ""));
            paused = true;
            time = Integer.MAX_VALUE;
            progress = 0;
            if (binding.progressBar != null) binding.progressBar.setProgress(progress);
        } else {
            binding.timer.setText(routineExercise.getLimit().toString());
            time = routineExercise.getLimit();
            if (binding.progressBar != null) binding.progressBar.setMax(time);
            if (binding.progressBar != null) binding.progressBar.setProgress(progress);
        }

        if (binding.excerciseExecImage != null) {
            Utils.setImageFromBase64(binding.excerciseExecImage, exercise.getMetadata().getImg_src());
        }
        myCountDownTimer = new MyCountDownTimer(COUNTER_NAME);
    }

    public class MyCountDownTimer extends Timer {

        public MyCountDownTimer(String name) {
            super(name);
        }

        public void start() {
            schedule(new TimerTask() {
                @Override
                public void run() {
                    if (!paused && time != Integer.MAX_VALUE && !time.equals(progress)) {
                        binding.timer.setText(Integer.valueOf(time - progress).toString());
                        progress+=1;
                        if (binding.progressBar != null) binding.progressBar.setProgress(progress);
                    }
                    if (time <= progress) {
                        progress = 0;
                        currentExercise += 1;
                        if (currentExercise >= cycles.get(currentCycle).getExercises().size()) {
                            currentExercise = 0;
                            if (currentCycleReps > 1) {
                                currentCycleReps-=1;
                            } else {
                                currentCycle += 1;
                                if (currentCycle < cycles.size()) {
                                    currentCycleReps = cycles.get(currentCycle).getRepetitions();
                                }
                            }
                        }

                        if (cycles.size() <= currentCycle) {
                            myCountDownTimer.cancel();
                            finish();
                        } else {
                            RoutineCycle cycle = cycles.get(currentCycle);
                            if (cycle != null) {
                                RoutineExercise ex = cycle.getExercises().get(currentExercise);
                                Exercise exercise = exerciseMap.getOrDefault(ex.getId(), null);
                                if (exercise != null) {
                                    if (ex.getLimitType().equals("repeticiones") || ex.getLimitType().equals("repetitions")) {
                                        binding.timer.setText(String.format("x%d rep%s.", ex.getLimit(), cycle.getRepetitions() > 1 ? "s" : ""));
                                        paused = true;
                                        time = Integer.MAX_VALUE;
                                        progress = 0;
                                        if (binding.progressBar != null) binding.progressBar.setProgress(progress);
                                    } else {
                                        binding.timer.setText(ex.getLimit().toString());
                                        time = ex.getLimit();
                                        paused = false;
                                        progress = 0;
                                        if (binding.progressBar != null) binding.progressBar.setMax(time);
                                        if (binding.progressBar != null) binding.progressBar.setProgress(progress);
                                    }
                                    if (binding.excerciseTitle != null)
                                        binding.excerciseTitle.setText(exercise.getName());
                                    if (binding.cycleTitle != null)
                                        binding.cycleTitle.setText(cycle.getName());
                                    if (binding.remainingCycles != null)
                                        binding.remainingCycles.setText(String.format("x%d rep%s.", currentCycleReps, currentCycleReps > 1 ? "s" : ""));

                                    if (binding.excerciseExecImage != null) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Utils.setImageFromBase64(binding.excerciseExecImage, exercise.getMetadata().getImg_src());
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    }
                }
            }, 0, 1000);  // 1 sec
        }

    }

}
package com.example.hypercoachinterface.ui.routine.execution;

import static com.example.hypercoachinterface.ui.routine.execution.StopDialog.STOP_REQUEST_KEY;
import static com.example.hypercoachinterface.ui.search.SearchDialog.FILTER_REQUEST_KEY;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.hypercoachinterface.R;
import com.example.hypercoachinterface.backend.App;
import com.example.hypercoachinterface.backend.api.model.Error;
import com.example.hypercoachinterface.backend.api.model.Exercise;
import com.example.hypercoachinterface.backend.api.model.Routine;
import com.example.hypercoachinterface.backend.api.model.RoutineCategory;
import com.example.hypercoachinterface.backend.api.model.RoutineCycle;
import com.example.hypercoachinterface.backend.api.model.RoutineExercise;
import com.example.hypercoachinterface.backend.repository.Resource;
import com.example.hypercoachinterface.backend.repository.Status;
import com.example.hypercoachinterface.databinding.ActivityExecuteRoutineBinding;
import com.example.hypercoachinterface.ui.Difficulty;
import com.example.hypercoachinterface.ui.SortCriteria;
import com.example.hypercoachinterface.ui.Utils;
import com.example.hypercoachinterface.ui.routine.CycleAdapter;
import com.example.hypercoachinterface.ui.search.SearchDialog;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ExecuteRoutineActivity extends AppCompatActivity {

    private static final String COUNTER_NAME = "ExecuteRoutineActivity";
    private static final String STOP_DIALOG_TAG = "Stop Dialog Tag";
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

        if (savedInstanceState != null) {
            currentExercise = savedInstanceState.getInt("currentExercise");
            currentCycle = savedInstanceState.getInt("currentCycle");
            currentCycleReps = savedInstanceState.getInt("currentCycleReps");
            time = savedInstanceState.getInt("time");
            progress = savedInstanceState.getInt("progress");
            paused = savedInstanceState.getBoolean("paused");
        }

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
                                        fillActivityData(cycles.get(currentCycle), exerciseMap.get(cycles.get(currentCycle).getExercises().get(currentExercise).getId()), cycles.get(currentCycle).getExercises().get(currentExercise), savedInstanceState);
                                        myCountDownTimer.start();
                                    }
                                } else {
                                    defaultResourceHandler(r2);
                                }
                            });
                        }
                    }
                } else {
                    defaultResourceHandler(r);
                }
            });
        } else {
            fillActivityData(cycles.get(currentCycle), exerciseMap.get(cycles.get(currentCycle).getExercises().get(currentExercise).getId()), cycles.get(currentCycle).getExercises().get(currentExercise), savedInstanceState);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentCycle", currentCycle);
        outState.putInt("currentCycleReps", currentCycleReps);
        outState.putInt("currentExercise", currentExercise);
        outState.putInt("progress", progress);
        outState.putInt("time", time);
        outState.putBoolean("paused", paused);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.routine_detail_menu, menu);
        return true;
    }

    private void fillActivityData(RoutineCycle cycle, Exercise exercise, RoutineExercise routineExercise, Bundle savedInstanceState) {
        if (binding.excerciseTitle != null)
            binding.excerciseTitle.setText(exercise.getName());
        if (binding.cycleTitle != null)
            binding.cycleTitle.setText(cycle.getName());
        if (savedInstanceState == null) {
            currentCycleReps = cycle.getRepetitions();
        }
        if (binding.remainingCycles != null) {
            binding.remainingCycles.setText(String.format("x%d rep%s.", currentCycleReps, currentCycleReps > 1 ? "s" : ""));
        }

        binding.stopButton.setOnClickListener(view -> {
            new StopDialog().show(getSupportFragmentManager(), STOP_DIALOG_TAG);
        });
        getSupportFragmentManager().setFragmentResultListener(STOP_REQUEST_KEY, this, (requestKey, result) -> {
            myCountDownTimer.cancel();
            finish();
        });
        binding.pauseButton.setOnClickListener(view -> {
            binding.playButton.setBackgroundResource(0);
            binding.pauseButton.setBackgroundResource(R.color.secondary_grey);
            paused = true;
        });
        binding.playButton.setOnClickListener(view -> {
            binding.playButton.setBackgroundResource(R.color.secondary_grey);
            binding.pauseButton.setBackgroundResource(0);
            paused = false;
        });
        binding.skipButton.setOnClickListener(view -> {
            time = progress;
        });

        if (routineExercise.getLimitType().equals("repeticiones") || routineExercise.getLimitType().equals("repetitions")) {
            binding.timer.setText(String.format("x%d rep%s.", routineExercise.getLimit(), cycle.getRepetitions() > 1 ? "s" : ""));
            if (savedInstanceState == null) {
                paused = true;
                time = Integer.MAX_VALUE;
                progress = 0;
            }
            if (binding.progressBar != null) binding.progressBar.setProgress(progress);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    binding.playButton.setVisibility(View.INVISIBLE);
                    binding.pauseButton.setVisibility(View.INVISIBLE);
                }
            });
        } else {
            binding.timer.setText(Integer.valueOf(time - progress).toString());
            if (savedInstanceState == null) {
                time = routineExercise.getLimit();
                paused = false;
                progress = 0;
            }
            if (binding.progressBar != null) binding.progressBar.setMax(time);
            if (binding.progressBar != null) binding.progressBar.setProgress(progress);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    binding.playButton.setVisibility(View.VISIBLE);
                    binding.pauseButton.setVisibility(View.VISIBLE);
                }
            });
        }

        if (binding.excerciseExecImage != null) {
            if (exercise.getMetadata() != null && !exercise.getMetadata().getImg_src().equals(""))
                Utils.setImageFromBase64(binding.excerciseExecImage, exercise.getMetadata().getImg_src());
            else
                binding.excerciseExecImage.setImageResource(R.mipmap.hci);
        }
        myCountDownTimer = new MyCountDownTimer(COUNTER_NAME);
        if (paused) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    binding.playButton.setBackgroundResource(R.color.secondary_grey);
                    binding.pauseButton.setBackgroundResource(0);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    binding.pauseButton.setBackgroundResource(R.color.secondary_grey);
                    binding.playButton.setBackgroundResource(0);
                }
            });
        }
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
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                binding.playButton.setVisibility(View.INVISIBLE);
                                                binding.pauseButton.setVisibility(View.INVISIBLE);
                                            }
                                        });
                                    } else {
                                        binding.timer.setText(ex.getLimit().toString());
                                        time = ex.getLimit();
                                        paused = false;
                                        progress = 0;
                                        if (binding.progressBar != null) binding.progressBar.setMax(time);
                                        if (binding.progressBar != null) binding.progressBar.setProgress(progress);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                binding.playButton.setVisibility(View.VISIBLE);
                                                binding.pauseButton.setVisibility(View.VISIBLE);
                                            }
                                        });
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
                                                if (exercise.getMetadata() != null && !exercise.getMetadata().getImg_src().equals(""))
                                                    Utils.setImageFromBase64(binding.excerciseExecImage, exercise.getMetadata().getImg_src());
                                                else
                                                    binding.excerciseExecImage.setImageResource(R.mipmap.hci);
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

    public void openStopDialog() {
        StopDialog stopDialog = new StopDialog();
        stopDialog.show(getSupportFragmentManager(), STOP_DIALOG_TAG);
    }

    private void defaultResourceHandler(Resource<?> resource) {
        if (resource.getStatus() == Status.ERROR) {
            Error error = resource.getError();
            if (error.getCode() == Error.LOCAL_UNEXPECTED_ERROR) {
                binding.getRoot().removeAllViews();
                Snackbar snackbar = Snackbar.make(this, binding.getRoot(), getResources().getString(R.string.no_connection), Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction(R.string.retry, v -> {
                    finish();
                    startActivity(getIntent());
                });
                snackbar.show();
                return;
            } else if (error.getCode() == Error.NOT_FOUND_ERROR) {
                invalidRoutineHandler();
                return;
            }
            String message = Utils.getErrorMessage(this, error.getCode());
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

}
package com.example.hypercoachinterface.ui.routine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.hypercoachinterface.MainActivity;
import com.example.hypercoachinterface.R;
import com.example.hypercoachinterface.backend.App;
import com.example.hypercoachinterface.backend.api.model.Error;
import com.example.hypercoachinterface.backend.api.model.Exercise;
import com.example.hypercoachinterface.backend.api.model.Review;
import com.example.hypercoachinterface.backend.api.model.Routine;
import com.example.hypercoachinterface.backend.api.model.RoutineCycle;
import com.example.hypercoachinterface.backend.api.model.RoutineExercise;
import com.example.hypercoachinterface.backend.repository.Resource;
import com.example.hypercoachinterface.backend.repository.RoutineRepository;
import com.example.hypercoachinterface.backend.repository.Status;
import com.example.hypercoachinterface.databinding.ActivityMainBinding;
import com.example.hypercoachinterface.databinding.ActivityRoutineDetailBinding;
import com.example.hypercoachinterface.databinding.FragmentFavoritesBinding;
import com.example.hypercoachinterface.ui.Utils;
import com.example.hypercoachinterface.ui.adapter.ItemAdapter;
import com.example.hypercoachinterface.ui.adapter.RoutineSummary;
import com.example.hypercoachinterface.ui.favorites.FavoritesViewModel;
import com.example.hypercoachinterface.ui.login.LoginActivity;
import com.example.hypercoachinterface.ui.routine.execution.ExecuteRoutineActivity;
import com.example.hypercoachinterface.viewmodel.RepositoryViewModelFactory;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoutineDetailActivity extends AppCompatActivity {

    private static final String TAG = "RoutineDetailActivity";
    private ActivityRoutineDetailBinding binding;
    private App app;
    private int routineId = -1;
    private MenuItem fav, unfav, share;
    private boolean fromLink;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_detail);

        app = (App) getApplication();

        if (app.getPreferences().getAuthToken() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        binding = ActivityRoutineDetailBinding.inflate(getLayoutInflater());


        Bundle b = getIntent().getExtras();
        fromLink = getIntent().getData() != null;

        if(fromLink) {
            routineId = Integer.parseInt(getIntent().getData().getQueryParameter("id"));
        } else {
            routineId = b.getInt("routineId");
        }
        Log.d(TAG, "onCreate: " + String.valueOf(routineId));

        List<RoutineCycle> cycles = new ArrayList<>();
        Map<Integer, Exercise> exerciseMap = new HashMap<>();
        CycleAdapter adapter = new CycleAdapter(cycles, exerciseMap, this);
        binding.cycleCardsView.setAdapter(adapter);

        app.getRoutineRepository().getRoutine(routineId).observe(this, r -> {
            if (r.getStatus() == Status.SUCCESS) {
                binding.routineProgressBar.setVisibility(View.GONE);
                binding.routineDescription.setVisibility(View.VISIBLE);
                fillActivityData(r.getData());
                if (r.getData() == null || r.getData().getMetadata() == null) {
                    invalidRoutineHandler();
                    return;
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
                            } else {
                                defaultResourceHandler(r);
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

            } else if (r.getStatus() == Status.LOADING) {
                binding.routineProgressBar.setVisibility(View.VISIBLE);
                binding.routineDescription.setVisibility(View.GONE);
            } else if (r.getStatus() == Status.ERROR) {
                binding.routineProgressBar.setVisibility(View.GONE);
                binding.routineDescription.setVisibility(View.GONE);
                defaultResourceHandler(r);
            }
        });

        setContentView(binding.getRoot());


    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        fav = menu.findItem(R.id.fav_btn);
        unfav = menu.findItem(R.id.unfav_btn);
        share = menu.findItem(R.id.share_btn);
        app.getRoutineRepository().getFavourites(0, 100).observe(this, r -> {
            if (r.getStatus() == Status.SUCCESS) {
                boolean found = false;
                for (Routine routine : r.getData()) {
                    if (routine.getId() == routineId) {
                        found = true;
                        unfav.setVisible(true);
                        break;
                    }
                }
                fav.setVisible(!found);
            } else {
                defaultResourceHandler(r);
            }
        });


        return true;
    }

    private void invalidRoutineHandler() {
        Toast.makeText(this, getResources().getString(R.string.invalid_routine), Toast.LENGTH_SHORT).show();
        Log.d(TAG, "invalidRoutineHandler: Invalid Routine (wasn't created with the website)");
        if(!fromLink) {
            finish();
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
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
            app.getRoutineRepository().postFavourite(routineId).observe(this, r -> {
                if (r.getStatus() == Status.SUCCESS) {
                    Log.d(TAG, String.format("Routine %d marked as fav", routineId));
                } else {
                    defaultResourceHandler(r);
                }
            });
            app.getReviewRepository().getReviews(routineId).observe(this, r -> {
                if (r.getStatus() == Status.SUCCESS) {
                    int favCount;
                    if (r.getData().getTotalCount() == 0)
                        favCount = 0;
                    else
                        favCount = Integer.parseInt(r.getData().getContent().get(0).getReview());
                    final int newCount = favCount + 1;
                    app.getReviewRepository().addReview(routineId, new Review(0, Integer.toString(favCount + 1))).observe(this, r2 -> {
                        if (r2.getStatus() == Status.SUCCESS) {
                            binding.routineFavsChip.setText(String.format("%d %s", newCount, new String(Character.toChars(0x2605))));
                        } else {
                            defaultResourceHandler(r2);
                        }
                    });
                    invalidateOptionsMenu();
                } else {
                    defaultResourceHandler(r);
                }
            });

        } else if (id == R.id.unfav_btn) {
            app.getRoutineRepository().deleteFavourite(routineId).observe(this, r -> {
                if (r.getStatus() == Status.SUCCESS) {
                    Log.d(TAG, String.format("Routine %d marked as not fav", routineId));
                } else {
                    defaultResourceHandler(r);
                }
            });
            app.getReviewRepository().getReviews(routineId).observe(this, r -> {
                if (r.getStatus() == Status.SUCCESS) {
                    int favCount;
                    if (r.getData().getTotalCount() == 0)
                        favCount = 0;
                    else
                        favCount = Integer.parseInt(r.getData().getContent().get(0).getReview());
                    if (favCount <= 0) favCount = 1;
                    final int newCount = favCount - 1;
                    app.getReviewRepository().addReview(routineId, new Review(0, Integer.toString(favCount - 1))).observe(this, r2 -> {
                        if (r2.getStatus() == Status.SUCCESS)
                            binding.routineFavsChip.setText(String.format("%d %s", newCount, new String(Character.toChars(0x2605))));
                    });
                    invalidateOptionsMenu();
                } else {
                    defaultResourceHandler(r);
                }
            });
        } else if (id == R.id.share_btn) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "MyGym");
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("\n%s \uD83D\uDCAA \n\n", getResources().getString(R.string.share_text)));
            sb.append("http://hypercoachinterface.com/routine?id=" + routineId + "\n\n");

            String shareMessage = sb.toString();
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, ""));
        } else if (id == android.R.id.home) {
            if(!fromLink) {
                finish();
            } else {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fillActivityData(Routine routine) {
        binding.routineTitle.setText(routine.getName());
        binding.routineDetail.setText(routine.getDetail());
        binding.routineCategoryChip.setText(translateCategory(routine.getRoutineCategory().getId()));
        binding.routineDifficultyChip.setText(translateDifficulty(routine.getDifficulty()));
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
            if (r.getStatus() == Status.SUCCESS) {
                String favCount;
                if (r.getData().getTotalCount() == 0)
                    favCount = "0";
                else
                    favCount = r.getData().getContent().get(0).getReview();
                favCount += " " + new String(Character.toChars(0x2605));
                binding.routineFavsChip.setText(favCount);
            } else {
                defaultResourceHandler(r);
            }
        });
    }

    private String translateDifficulty(String difficulty) {
        switch (difficulty) {
            case "beginner":
                return getResources().getString(R.string.beginner);
            case "intermediate":
                return getResources().getString(R.string.intermediate);
            case "advanced":
                return getResources().getString(R.string.advanced);
            default:
                return difficulty;
        }
    }

    private String translateCategory(int category) {
        switch (category) {
            case 1:
                return getResources().getString(R.string.hit);
            case 2:
                return getResources().getString(R.string.cardio);
            case 3:
                return getResources().getString(R.string.calisthenics);
            case 4:
                return getResources().getString(R.string.bodybuilding);
            default:
                return getResources().getString(R.string.no_category);
        }
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
            Log.d(TAG, message);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }
}
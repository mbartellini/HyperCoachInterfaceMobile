package com.example.hypercoachinterface.backend;

import android.app.Application;
import android.content.Context;

import com.example.hypercoachinterface.backend.repository.CategoryRepository;
import com.example.hypercoachinterface.backend.repository.ExerciseRepository;
import com.example.hypercoachinterface.backend.repository.ReviewRepository;
import com.example.hypercoachinterface.backend.repository.RoutineRepository;
import com.example.hypercoachinterface.backend.repository.SportRepository;
import com.example.hypercoachinterface.backend.repository.UserRepository;

public class App extends Application {

    private static Context context;

    private AppPreferences preferences;
    private UserRepository userRepository;
    private SportRepository sportRepository;
    private ExerciseRepository exerciseRepository;
    private RoutineRepository routineRepository;
    private ReviewRepository reviewRepository;
    private CategoryRepository categoryRepository;

    public ExerciseRepository getExerciseRepository() {
        return exerciseRepository;
    }

    public AppPreferences getPreferences() {
        return preferences;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public SportRepository getSportRepository() {
        return sportRepository;
    }

    public RoutineRepository getRoutineRepository() {
        return routineRepository;
    }

    public ReviewRepository getReviewRepository() {
        return reviewRepository;
    }

    public CategoryRepository getCategoryRepository() {
        return categoryRepository;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        this.context = getApplicationContext();

        preferences = new AppPreferences(this);
        userRepository = new UserRepository(this);
        sportRepository = new SportRepository(this);
        reviewRepository = new ReviewRepository(this);
        routineRepository = new RoutineRepository(this);
        exerciseRepository = new ExerciseRepository(this);
        categoryRepository = new CategoryRepository(this);

    }

    public static Context getContext() {
        return context;
    }
}

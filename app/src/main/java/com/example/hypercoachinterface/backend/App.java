package com.example.hypercoachinterface.backend;

import android.app.Application;

import com.example.hypercoachinterface.backend.repository.ExerciseRepository;
import com.example.hypercoachinterface.backend.repository.ReviewRepository;
import com.example.hypercoachinterface.backend.repository.RoutineRepository;
import com.example.hypercoachinterface.backend.repository.SportRepository;
import com.example.hypercoachinterface.backend.repository.UserRepository;

public class App extends Application {

    private AppPreferences preferences;
    private UserRepository userRepository;
    private SportRepository sportRepository;
    private ExerciseRepository exerciseRepository;
    private RoutineRepository routineRepository;
    private ReviewRepository reviewRepository;

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

    @Override
    public void onCreate() {
        super.onCreate();

        preferences = new AppPreferences(this);
        userRepository = new UserRepository(this);
        sportRepository = new SportRepository(this);
        reviewRepository = new ReviewRepository(this);
        routineRepository = new RoutineRepository(this);
        exerciseRepository = new ExerciseRepository(this);

    }
}

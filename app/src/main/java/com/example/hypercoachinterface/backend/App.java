package com.example.hypercoachinterface.backend;

import android.app.Application;

import com.example.hypercoachinterface.backend.repository.ExerciseRepository;
import com.example.hypercoachinterface.backend.repository.SportRepository;
import com.example.hypercoachinterface.backend.repository.UserRepository;

public class App extends Application {

    private AppPreferences preferences;
    private UserRepository userRepository;
    private SportRepository sportRepository;
    private ExerciseRepository exerciseRepository;

    public ExerciseRepository getExerciseRepository() {
        return exerciseRepository;
    }

    public void setExerciseRepository(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    public AppPreferences getPreferences() { return preferences; }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public SportRepository getSportRepository() {
        return sportRepository;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        preferences = new AppPreferences(this);
        userRepository = new UserRepository(this);
        sportRepository = new SportRepository(this);
        exerciseRepository = new ExerciseRepository(this);
    }
}

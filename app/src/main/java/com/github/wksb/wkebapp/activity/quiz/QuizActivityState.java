package com.github.wksb.wkebapp.activity.quiz;

import android.transition.Scene;
import android.transition.Transition;
import android.view.View;

/**
 * Created by Michael on 04.01.2016.
 */
public interface QuizActivityState {

    void init();

    void onBackPressed();

    void onBtnClickedAnswer(View view);

    Scene getScene();

    Transition getEnterTransition();

    void transitionTo(QuizActivityState newState);
}

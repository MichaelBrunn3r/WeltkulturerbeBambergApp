package com.github.wksb.wkebapp.activity.welcomepage;

import android.content.Intent;
import android.view.View;

import com.github.wksb.wkebapp.R;
import com.github.wksb.wkebapp.activity.AboutActivity;
import com.github.wksb.wkebapp.activity.InstructionsActivity;
import com.github.wksb.wkebapp.activity.QuizActivity;
import com.github.wksb.wkebapp.activity.navigation.Route;

/**
 * Created by Admin on 06/12/2015.
 */
public class QuizInProgress implements WelcomePageActivityState {

    WelcomePageActivity welcomePageActivity;

    public QuizInProgress(WelcomePageActivity welcomePageActivity) {
        this.welcomePageActivity = welcomePageActivity;
    }

    @Override
    public void onActivityStart() {
        welcomePageActivity.setContentView(R.layout.activity_welcome_page_app);
        welcomePageActivity.findViewById(R.id.button_welcome_start).setVisibility(View.INVISIBLE);
        welcomePageActivity.findViewById(R.id.button_welcome_restart).setVisibility(View.VISIBLE);
        welcomePageActivity.findViewById(R.id.button_welcome_continue).setVisibility(View.VISIBLE);
    }

    @Override
    public void initState() {

    }

    @Override
    public void onBtnClickedStart(View view) {

    }

    @Override
    public void onBtnClickedAbout(View view) {
        Intent startAboutActivity = new Intent(welcomePageActivity, AboutActivity.class);
        welcomePageActivity.startActivity(startAboutActivity);
    }

    @Override
    public void onBtnClickedContinue(View view) {
        Intent startCurrentQuiz = new Intent(welcomePageActivity, QuizActivity.class);
        startCurrentQuiz.putExtra(QuizActivity.TAG_QUIZ_ID, Route.get().getCurrentQuizId());
        welcomePageActivity.startActivity(startCurrentQuiz);
    }

    @Override
    public void onBtnClickedRestartTour(View view) {
        // Start InstructionsActivity
        Intent startInstructionActivity = new Intent(welcomePageActivity, InstructionsActivity.class);
        welcomePageActivity.startActivity(startInstructionActivity);
    }
}

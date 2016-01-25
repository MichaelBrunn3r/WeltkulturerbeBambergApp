package com.github.wksb.wkebapp.activity.welcomepage;

import android.content.Intent;
import android.view.View;

import com.github.wksb.wkebapp.R;
import com.github.wksb.wkebapp.activity.EndActivity;
import com.github.wksb.wkebapp.activity.InstructionsActivity;

/**
 * Created by Michael on 25.01.2016.
 */
public class TourIsFinished implements WelcomePageActivityState {

    /**
     * The overlying {@link WelcomePageActivity}
     */
    private WelcomePageActivity welcomePageActivity;

    /**
     * @param welcomePageActivity The overlying {@link WelcomePageActivity}
     */
    public TourIsFinished (WelcomePageActivity welcomePageActivity) {
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
    public void onBtnClickedContinue(View view) {
        Intent startEnd = new Intent(welcomePageActivity, EndActivity.class);
        welcomePageActivity.startActivity(startEnd);
    }

    @Override
    public void onBtnClickedRestartTour(View view) {
        // Start InstructionsActivity
        Intent startInstructionActivity = new Intent(welcomePageActivity, InstructionsActivity.class);
        welcomePageActivity.startActivity(startInstructionActivity);
    }
}

package com.github.wksb.wkebapp.activity.welcomepage;

import android.content.Intent;
import android.view.View;

import com.github.wksb.wkebapp.R;
import com.github.wksb.wkebapp.activity.InstructionsActivity;

/**
 * State for the {@link WelcomePageActivity} when no guided tour is in progress (not started yet)
 *
 * @author Projekt-Seminar "Schnitzeljagd World-heritage" 2015/2016 des Clavius Gymnasiums Bamberg
 * @version 1.0
 * @since 2015-10-15
 */
public class NoTourInProgress implements WelcomePageActivityState{

    /**
     * The overlying {@link WelcomePageActivity}
     */
    private WelcomePageActivity welcomePageActivity;

    /**
     * @param welcomePageActivity The overlying {@link WelcomePageActivity}
     */
    public NoTourInProgress(WelcomePageActivity welcomePageActivity) {
        this.welcomePageActivity = welcomePageActivity;
    }

    @Override
    public void onActivityStart() {
        welcomePageActivity.setContentView(R.layout.activity_welcome_page_app);
        welcomePageActivity.findViewById(R.id.button_welcome_start).setVisibility(View.VISIBLE);
        welcomePageActivity.findViewById(R.id.button_welcome_restart).setVisibility(View.INVISIBLE);
        welcomePageActivity.findViewById(R.id.button_welcome_continue).setVisibility(View.INVISIBLE);
    }

    @Override
    public void initState() {
    }

    @Override
    public void onBtnClickedStart(View view) {
        Intent startInstructionActivity = new Intent(welcomePageActivity, InstructionsActivity.class);
        welcomePageActivity.startActivity(startInstructionActivity);
    }

    @Override
    public void onBtnClickedContinue(View view) {
    }

    @Override
    public void onBtnClickedRestartTour(View view) {
    }
}

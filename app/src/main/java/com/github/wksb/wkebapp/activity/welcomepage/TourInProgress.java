package com.github.wksb.wkebapp.activity.welcomepage;

import android.content.Intent;
import android.view.View;

import com.github.wksb.wkebapp.R;
import com.github.wksb.wkebapp.activity.InstructionsActivity;
import com.github.wksb.wkebapp.activity.navigation.NavigationActivity;

/**
 * State for the {@link WelcomePageActivity} when the guided tour is in progress (started)
 *
 * @author Projekt-Seminar "Schnitzeljagd World-heritage" 2015/2016 des Clavius Gymnasiums Bamberg
 * @version 1.0
 * @since 2015-10-16
 */
public class TourInProgress implements WelcomePageActivityState{

    /**
     * The overlying {@link WelcomePageActivity}
     */
    private WelcomePageActivity welcomePageActivity;

    /**
     * @param welcomePageActivity The overlying {@link WelcomePageActivity}
     */
    public TourInProgress (WelcomePageActivity welcomePageActivity) {
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
        Intent startRoute = new Intent(welcomePageActivity, NavigationActivity.class);
        welcomePageActivity.startActivity(startRoute);
    }

    @Override
    public void onBtnClickedRestartTour(View view) {
        // Start InstructionsActivity
        Intent startInstructionActivity = new Intent(welcomePageActivity, InstructionsActivity.class);
        welcomePageActivity.startActivity(startInstructionActivity);
    }
}
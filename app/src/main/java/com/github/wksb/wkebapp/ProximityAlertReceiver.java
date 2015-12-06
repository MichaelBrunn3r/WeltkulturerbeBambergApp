package com.github.wksb.wkebapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;

import com.github.wksb.wkebapp.activity.QuizActivity;
import com.github.wksb.wkebapp.activity.navigation.Route;
import com.github.wksb.wkebapp.utilities.DebugUtils;

//TODO Documentation
/**
 * Created by michael on 12.06.15.
 */
public class ProximityAlertReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean entering = intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false);

        if (entering) {
            DebugUtils.toast(context, "Im entering " + intent.getStringExtra("name"));
            if (intent.getIntExtra("quiz-id", -1) == Route.getCurrentQuizId(context)) {
                QuizActivity.setProgressState(context, QuizActivity.IS_IN_PROGRESS);

                Intent startQuizActivity = new Intent(context, QuizActivity.class);
                startQuizActivity.putExtra(QuizActivity.TAG_QUIZ_ID, Route.getCurrentQuizId(context));
                context.startActivity(startQuizActivity);
            }
        } else {
            DebugUtils.toast(context, "Im doing something funny :D");
        }
    }
}
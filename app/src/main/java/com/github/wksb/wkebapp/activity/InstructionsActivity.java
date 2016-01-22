package com.github.wksb.wkebapp.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.github.wksb.wkebapp.R;
import com.github.wksb.wkebapp.activity.navigation.NavigationActivity;
import com.github.wksb.wkebapp.activity.navigation.Route;
import com.github.wksb.wkebapp.activity.quiz.QuizActivity;
import com.github.wksb.wkebapp.contentprovider.WeltkulturerbeContentProvider;
import com.github.wksb.wkebapp.database.RouteSegmentsTable;
import com.github.wksb.wkebapp.database.RoutesTable;
import com.github.wksb.wkebapp.utilities.DebugUtils;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * This activity shows a greeting and instructions how to use the World-heritage-Application.
 *
 * @author Projekt-Seminar "Schnitzeljagd World-heritage" 2015/2016 des Clavius Gymnasiums Bamberg
 * @version 1.0
 * @since 2015-06-04
 */
public class InstructionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        // Set up theActionBar
        setUpActionBar();
    }

    private void setUpActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.actionbar);
        setSupportActionBar(toolbar);

        // Use Custom ActionBar Layout and Display BackButton
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_HOME_AS_UP);

        // Set Custom ActionBar Layout
        getSupportActionBar().setCustomView(R.layout.actionbar_title);
        ((TextView)findViewById(R.id.textview_actionbar_title)).setText(getResources().getString(R.string.tv_instruc_title));
    }

    //TODO Documentation
    public void onBtnClickShortRoute(View view) {
        resetPreviousTour();

        getSharedPreferences("TOUR", MODE_PRIVATE).edit().putString("ROUTE_NAME", "short").commit();
        Route.get().init();

        Intent startShortRoute = new Intent(this, NavigationActivity.class);
        startActivity(startShortRoute);
    }

    //TODO Documentation
    public void onBtnClickLongRoute(View view) {
        resetPreviousTour();

        getSharedPreferences("TOUR", MODE_PRIVATE).edit().putString("ROUTE_NAME", "long").commit();
        Route.get().init();

        Intent startLongRoute = new Intent(this, NavigationActivity.class);
        startActivity(startLongRoute);
    }

    private void resetPreviousTour() {
        Route.get().reset();
        QuizActivity.reset();
    }
}
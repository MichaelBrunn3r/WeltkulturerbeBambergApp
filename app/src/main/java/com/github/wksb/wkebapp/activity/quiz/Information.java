package com.github.wksb.wkebapp.activity.quiz;

import android.os.Bundle;
import android.text.Html;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.wksb.wkebapp.CustomHtmlTagHandler;
import com.github.wksb.wkebapp.Edge;
import com.github.wksb.wkebapp.R;
import com.github.wksb.wkebapp.SlideTransition;

/**
 * Created by Michael on 04.01.2016.
 */
public class Information implements QuizActivityState {

    private boolean initialised = false;

    private QuizActivity quizActivity;

    // Definition of the different Views represented inside the Layout
    private ImageView mImageViewImage;
    private TextView mTextViewInformation;

    public Information(QuizActivity quizActivity) {
        this.quizActivity = quizActivity;
    }

    @Override
    public void init() {
        if (!initialised) {
            setUpLayout();
            populateLayout();
            initialised = true;
        }
    }

    @Override
    public void onBackPressed() {
        quizActivity.setActivityState(new Quiz(quizActivity));
    }

    @Override
    public void onBtnClickedAnswer(View view) {

    }

    @Override
    public Scene getScene() {
        return Scene.getSceneForLayout((ViewGroup)quizActivity.findViewById(R.id.relativelayout_quiz_scene_root), R.layout.activity_quiz_content_information, quizActivity);
    }

    @Override
    public Transition getEnterTransition() {
        Transition slideInInformation = new SlideTransition(Edge.BOTTOM);
        slideInInformation.addTarget(R.id.textview_quiz_information_text);
        slideInInformation.addTarget(R.id.scrollView_quiz_information);

        return slideInInformation;
    }

    @Override
    public void transitionTo(QuizActivityState newState) {
        Scene scene = newState.getScene();
        Transition enterTransition = newState.getEnterTransition();

        Transition exitTransition = new SlideTransition(Edge.BOTTOM);
        exitTransition.addTarget(R.id.textview_quiz_information_text);

        TransitionSet transition = new TransitionSet();
        transition.addTransition(exitTransition);
        transition.addTransition(enterTransition);
        transition.setOrdering(TransitionSet.ORDERING_SEQUENTIAL);

        TransitionManager.go(scene, transition);
    }

    private void setUpLayout() {
        ViewGroup sceneRoot = (ViewGroup)quizActivity.findViewById(R.id.relativelayout_quiz_scene_root);
        sceneRoot.removeAllViews();
        quizActivity.getLayoutInflater().inflate(R.layout.activity_quiz_content_information, sceneRoot);

        // Initialise the Views
        mImageViewImage = (ImageView) quizActivity.findViewById(R.id.imageview_quiz_background_image);
        mTextViewInformation = (TextView) quizActivity.findViewById(R.id.textview_quiz_information_text);
    }

    /**
     * Populate the Views in the Layout with the Data of the InformationWrapper
     */
    private void populateLayout() {
        mTextViewInformation.setText(Html.fromHtml(quizActivity.getInformationWrapper().getInfoText(), null, new CustomHtmlTagHandler()));
        mImageViewImage.setImageBitmap(quizActivity.getInformationWrapper().getImage());
    }
}

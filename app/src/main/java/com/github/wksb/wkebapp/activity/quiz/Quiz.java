package com.github.wksb.wkebapp.activity.quiz;

import android.support.v4.app.NavUtils;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.wksb.wkebapp.Edge;
import com.github.wksb.wkebapp.R;
import com.github.wksb.wkebapp.SlideTransition;
import com.github.wksb.wkebapp.activity.navigation.Route;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Michael on 04.01.2016.
 */
public class Quiz implements QuizActivityState{

    private boolean initialised = false;

    private QuizActivity quizActivity;

    // Definition of the different Views represented inside the Layout
    private ImageView mImageViewBackgroundImage;
    private TextView mTextViewActionbarTitle;
    private TextView mTextViewQuestion;

    // Definition of the Buttons of the Quiz. These Buttons are not represented inside the Layout
    private Button mBtn_quiz_solution;
    private Button mBtn_quiz_wrongAnswer1;
    private Button mBtn_quiz_wrongAnswer2;
    private Button mBtn_quiz_wrongAnswer3;

    public Quiz(QuizActivity quizActivity) {
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
        NavUtils.navigateUpFromSameTask(quizActivity); // Navigate to the Parent Activity
    }

    @Override
    public void onBtnClickedAnswer(View view) {
        if (quizActivity.getQuizWrapper().isRightAnswer((String)((Button)view).getText())) {
            mBtn_quiz_solution.setBackgroundColor(quizActivity.getResources().getColor(R.color.green));
        } else {
            mBtn_quiz_solution.setBackgroundColor(quizActivity.getResources().getColor(R.color.green));
            view.setBackgroundColor(quizActivity.getResources().getColor(R.color.red));
        }

        View.OnClickListener goToInformation = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quizActivity.setActivityState(new Information(quizActivity));
            }
        };

        mBtn_quiz_solution.setOnClickListener(goToInformation);
        mBtn_quiz_wrongAnswer1.setOnClickListener(goToInformation);
        mBtn_quiz_wrongAnswer2.setOnClickListener(goToInformation);
        mBtn_quiz_wrongAnswer3.setOnClickListener(goToInformation);

        if (quizActivity.getQuizWrapper().getQuizId() == Route.getCurrentQuizId()) { // Check if this Quiz is the current Quiz that has to solved to progress in the Tour
            Route.get().addProgress(1); // Increment the current Progress by 1 TODO Move to Route class
            Route.setArrivedAtCurrentDestination(false); // User has not arrived at the next Waypoint, yet
            QuizActivity.lockQuiz(); // This Quiz is finished. Lock the next Quiz
        }
    }

    @Override
    public Scene getScene() {
        return Scene.getSceneForLayout((ViewGroup)quizActivity.findViewById(R.id.relativelayout_quiz_scene_root), R.layout.activity_quiz_content_quiz, quizActivity);
    }

    @Override
    public Transition getEnterTransition() {
        Transition slideInQuestion = new SlideTransition(Edge.RIGHT);
        slideInQuestion.addTarget(R.id.textview_quiz_question);

        Transition slideInButtons = new SlideTransition(Edge.BOTTOM);
        slideInButtons.addTarget(R.id.button_quiz_answer1);
        slideInButtons.addTarget(R.id.button_quiz_answer2);
        slideInButtons.addTarget(R.id.button_quiz_answer3);
        slideInButtons.addTarget(R.id.button_quiz_answer4);

        TransitionSet enterTransition = new TransitionSet();
        enterTransition.addTransition(slideInQuestion);
        enterTransition.addTransition(slideInButtons);
        enterTransition.setOrdering(TransitionSet.ORDERING_TOGETHER);

        return enterTransition;
    }

    @Override
    public void transitionTo(QuizActivityState newState) {
        Scene scene = newState.getScene();
        Transition enterTransition = newState.getEnterTransition();

        Transition slideOutQuestion = new SlideTransition(Edge.RIGHT);
        slideOutQuestion.addTarget(R.id.textview_quiz_question);

        Transition slideOutButtons = new SlideTransition(Edge.BOTTOM);
        slideOutButtons.addTarget(R.id.button_quiz_answer1);
        slideOutButtons.addTarget(R.id.button_quiz_answer2);
        slideOutButtons.addTarget(R.id.button_quiz_answer3);
        slideOutButtons.addTarget(R.id.button_quiz_answer4);

        TransitionSet exitTransition = new TransitionSet();
        exitTransition.addTransition(slideOutQuestion);
        exitTransition.addTransition(slideOutButtons);
        exitTransition.setOrdering(TransitionSet.ORDERING_TOGETHER);

        TransitionSet transition = new TransitionSet();
        transition.addTransition(exitTransition);
        transition.addTransition(enterTransition);
        transition.setOrdering(TransitionSet.ORDERING_SEQUENTIAL);

        TransitionManager.go(scene, transition);
    }

    // TODO Description
    void setUpLayout() {
        ViewGroup sceneRoot = (ViewGroup)quizActivity.findViewById(R.id.relativelayout_quiz_scene_root);
        sceneRoot.removeAllViews();
        quizActivity.getLayoutInflater().inflate(R.layout.activity_quiz_content_quiz, sceneRoot);

        // Initialise the Views
        mImageViewBackgroundImage = (ImageView) quizActivity.findViewById(R.id.imageview_quiz_background_image);
        mTextViewQuestion = (TextView) quizActivity.findViewById(R.id.textview_quiz_question);
        mTextViewActionbarTitle = (TextView) quizActivity.findViewById(R.id.textview_actionbar_title);
    }

    /**
     * Populate the Views in the Layout with the Data of the QuizWrapper
     */
    private void populateLayout() {
        // Set the Text of the Station to the location of the current Quiz
        mTextViewActionbarTitle.setText(quizActivity.getQuizWrapper().getLocation());

        //Set the Text of the Question TextView to the question of the current Quiz
        mTextViewQuestion.setText(quizActivity.getQuizWrapper().getQuestion());

        mImageViewBackgroundImage.setImageBitmap(quizActivity.getInformationWrapper().getImage());

        //Get the List of all wrong answers to the current Quiz and shuffle them
        List<String> wrongAnswers = new ArrayList<>(quizActivity.getQuizWrapper().getWrongAnswers()); // Copy the Answers since we later delete them out of the List
        Collections.shuffle(wrongAnswers);

        // Get the solution of the current Quiz
        String solution = quizActivity.getQuizWrapper().getSolution();

        // Shuffel the Button IDs of the Quiz Buttons
        List<Integer> btnIds = new ArrayList<>();
        btnIds.add(R.id.button_quiz_answer1);
        btnIds.add(R.id.button_quiz_answer2);
        btnIds.add(R.id.button_quiz_answer3);
        btnIds.add(R.id.button_quiz_answer4);
        Collections.shuffle(btnIds);

        // Assign the Quiz Buttons in the Layout to their representations in the Activity
        mBtn_quiz_solution = (Button) quizActivity.findViewById(btnIds.remove(0));
        mBtn_quiz_wrongAnswer1 = (Button) quizActivity.findViewById(btnIds.remove(0));
        mBtn_quiz_wrongAnswer2 = (Button) quizActivity.findViewById(btnIds.remove(0));
        mBtn_quiz_wrongAnswer3 = (Button) quizActivity.findViewById(btnIds.remove(0));

        // Set the Texts of the Buttons to the different answers to the current Quiz
        mBtn_quiz_solution.setText(solution);
        mBtn_quiz_wrongAnswer1.setText(wrongAnswers.remove(0));
        mBtn_quiz_wrongAnswer2.setText(wrongAnswers.remove(0));
        mBtn_quiz_wrongAnswer3.setText(wrongAnswers.remove(0));
    }
}
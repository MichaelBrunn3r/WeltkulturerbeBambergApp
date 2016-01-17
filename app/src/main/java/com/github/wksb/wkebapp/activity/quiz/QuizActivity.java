package com.github.wksb.wkebapp.activity.quiz;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.github.wksb.wkebapp.App;
import com.github.wksb.wkebapp.R;
import com.github.wksb.wkebapp.contentprovider.WeltkulturerbeContentProvider;
import com.github.wksb.wkebapp.database.InformationTable;
import com.github.wksb.wkebapp.database.QuizzesTable;
import com.github.wksb.wkebapp.utilities.DebugUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class QuizActivity extends AppCompatActivity {

    private QuizActivityState mState;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({QUIZ_LOCKED, QUIZ_UNLOCKED})
    public @interface NEXT_QUIZ_STATE {}
    /** A Quiz is in Progress */
    public static final int QUIZ_LOCKED = 1;
    /** No Quiz is in Progress */
    public static final int QUIZ_UNLOCKED = 2;
    /** The default Value if a Quiz is in Progress */
    private static final int DEFAULT_LOCK_STATE = QUIZ_LOCKED;

    // Definition of the Tags used in Intents send to this Activity
    public static final String TAG_PACKAGE = QuizActivity.class.getPackage().getName();
    /** This TAG tags the ID of the Quiz, which is to be loaded, within an Intent send to this Activity*/
    public static final String TAG_QUIZ_ID = TAG_PACKAGE + "quiz_id";
    /** FLAG for the Quiz ID within an Intent send to this Activity, tagged with the TAG {@link QuizActivity#TAG_QUIZ_ID}, which indicates a Quiz with this ID doesn't exist*/
    public static final int FLAG_NO_QUIZ_ID_PASSED = -1;

    private QuizWrapper mQuizWrapper;
    private InformationWrapper mInformationWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        createDataWrappers();

        setUpActionBar();

        setActivityState(new Quiz(this));
    }

    public void setActivityState(QuizActivityState newState) {
        boolean changeState = mState != null;
        if (changeState) {
            mState.transitionTo(newState);
        }
        mState = newState;
        mState.init();
    }

    @Override
    public void onBackPressed() {
        mState.onBackPressed();
    }

    public void onBtnClickedAnswer(View view) {
        mState.onBtnClickedAnswer(view);
    }

    private void createDataWrappers() {
        mQuizWrapper = createQuizWrapperByID(getQuizIDFromIntent());
        mInformationWrapper = createInformationWrapperById(mQuizWrapper.getInfoID());
    }

    /**
     * Get the QuizId from the Starting Intent of this Activity
     * @return returns IntExtra with the Quiz ID from the starting Intent
     */
    private int getQuizIDFromIntent(){
        int quizId = getIntent().getIntExtra(TAG_QUIZ_ID, FLAG_NO_QUIZ_ID_PASSED);
        if (quizId == FLAG_NO_QUIZ_ID_PASSED) throw new IllegalArgumentException("No QuizID in Starting Intent");
        return quizId;
    }

    public QuizWrapper getQuizWrapper() {
        return mQuizWrapper;
    }

    public InformationWrapper getInformationWrapper() {
        return mInformationWrapper;
    }

    /**
     * This Method queries the SQLite Database for quizzes with the committed ID and returns it as a {@link com.github.wksb.wkebapp.activity.quiz.QuizActivity.QuizWrapper}
     * @param quizId The QuizID of the quiz to load
     * @return {@link com.github.wksb.wkebapp.activity.quiz.QuizActivity.QuizWrapper} with the first found query result
     */
    private QuizWrapper createQuizWrapperByID(int quizId){
        String[] projection = {QuizzesTable.COLUMN_QUIZ_ID, QuizzesTable.COLUMN_LOCATION, QuizzesTable.COLUMN_QUESTION, QuizzesTable.COLUMN_SOLUTION, QuizzesTable.COLUMN_WRONG_ANSWER_1,
                QuizzesTable.COLUMN_WRONG_ANSWER_2, QuizzesTable.COLUMN_WRONG_ANSWER_3, QuizzesTable.COLUMN_INFO_ID};
        String selection = QuizzesTable.COLUMN_QUIZ_ID + "=?";
        String[] selectionArgs = {Integer.toString(quizId)};
        String sortOrder = null;
        Cursor cursor = getContentResolver().query(WeltkulturerbeContentProvider.URI_TABLE_QUIZZES, projection, selection, selectionArgs, sortOrder);

        if (cursor.isBeforeFirst()) cursor.moveToNext();
        String location = cursor.getString(cursor.getColumnIndex(QuizzesTable.COLUMN_LOCATION));
        String question = cursor.getString(cursor.getColumnIndex(QuizzesTable.COLUMN_QUESTION));
        String solution = cursor.getString(cursor.getColumnIndex(QuizzesTable.COLUMN_SOLUTION));
        String wrongAnswer1 = cursor.getString(cursor.getColumnIndex(QuizzesTable.COLUMN_WRONG_ANSWER_1));
        String wrongAnswer2 = cursor.getString(cursor.getColumnIndex(QuizzesTable.COLUMN_WRONG_ANSWER_2));
        String wrongAnswer3 = cursor.getString(cursor.getColumnIndex(QuizzesTable.COLUMN_WRONG_ANSWER_3));
        int infoID = cursor.getInt(cursor.getColumnIndex(QuizzesTable.COLUMN_INFO_ID));

        return new QuizWrapper(quizId, location, question, solution, new String[]{wrongAnswer1, wrongAnswer2, wrongAnswer3}, infoID);
    }

    private InformationWrapper createInformationWrapperById(int informationId) {
        String[] projection = {InformationTable.COLUMN_INFORMATION_ID, InformationTable.COLUMN_IMAGE_PATH, InformationTable.COLUMN_INFO_TEXT};
        String selection = InformationTable.COLUMN_INFORMATION_ID + "=?";
        String[] selectionArgs = {Integer.toString(informationId)};

        Cursor cursor = getContentResolver().query(WeltkulturerbeContentProvider.URI_TABLE_INFORMATION, projection, selection, selectionArgs, null);
        cursor.moveToFirst();

        String imagePath = cursor.getString(cursor.getColumnIndex(InformationTable.COLUMN_IMAGE_PATH));
        String infoText = cursor.getString(cursor.getColumnIndex(InformationTable.COLUMN_INFO_TEXT));

        return new InformationWrapper(informationId, imagePath, infoText);
    }

    public void setUpActionBar() {
        Toolbar actionbar = (Toolbar) findViewById(R.id.actionbar);
        setSupportActionBar(actionbar);

        if (getSupportActionBar() != null) {
            // Use Custom ActionBar Layout and Display BackButton
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_HOME_AS_UP);

            // Set Custom ActionBar Layout
            getSupportActionBar().setCustomView(R.layout.actionbar_title);
        } else {
            DebugUtils.toast("Error while loading the SupportActionbar");
        }
    }

    public static boolean nextQuizIsInUnlocked() {
        return App.get().getSharedPreferences("TOUR", MODE_PRIVATE).getInt("NEXT_QUIZ_IS_UNLOCKED", DEFAULT_LOCK_STATE) == QUIZ_UNLOCKED;
    }

    public static void unlockQuiz() {
        App.get().getSharedPreferences("TOUR", MODE_PRIVATE).edit().putInt("NEXT_QUIZ_IS_UNLOCKED", QUIZ_UNLOCKED).commit();
    }

    public static void lockQuiz() {
        App.get().getSharedPreferences("TOUR", MODE_PRIVATE).edit().putInt("NEXT_QUIZ_IS_UNLOCKED", QUIZ_LOCKED).commit();
    }

    /**
     * Reset all Settings of the QuizActivity
     */
    public static void reset() {
        lockQuiz();
    }

    /**
     * This class wraps the Data of the Quiz from the SQL-Database
     */
    public class QuizWrapper {

        private int quizID;
        private final String location;
        private final String question;
        private final String solution;
        private final int infoID;
        private final List<String> wrongAnswers;

        public QuizWrapper(int quizID, String location, String question, String solution, String[] wrong_answers, int infoID){
            this.quizID = quizID;
            this.location = location;
            this.question = question;
            this.solution = solution;
            this.infoID = infoID;
            this.wrongAnswers = new ArrayList<>(Arrays.asList(wrong_answers));
        }

        public boolean isRightAnswer(String answer) {
            return answer.equalsIgnoreCase(solution);
        }

        public int getQuizId(){
            return this.quizID;
        }

        public String getLocation() {
            return this.location;
        }

        public String getQuestion(){
            return this.question;
        }

        public String getSolution(){
            return this.solution;
        }

        public int getInfoID() {
            return infoID;
        }

        public List<String> getWrongAnswers(){
            return this.wrongAnswers;
        }
    }

    /**
     * This class wraps the Data of the Information from the SQL-Database
     */
    public class InformationWrapper {

        private final int id;
        private Bitmap image;
        private final String infoText;

        public InformationWrapper(int id, String imagePath, String infoText) {
            this.id = id;
            this.infoText = infoText;

            try {
                InputStream open = getAssets().open(imagePath);
                image = BitmapFactory.decodeStream(open);
            } catch (IOException | IllegalArgumentException e) {
                e.printStackTrace();
            }

            if (image == null) {
                image = BitmapFactory.decodeResource(getResources(), R.drawable.dummy_image);
            }
        }

        public int getId() {
            return id;
        }

        public Bitmap getImage() {
            return image;
        }

        public String getInfoText() {
            return infoText;
        }
    }
}
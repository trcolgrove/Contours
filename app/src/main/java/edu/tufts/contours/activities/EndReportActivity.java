package edu.tufts.contours.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.trcolgrove.contours.R;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import edu.tufts.contours.data.DataManager;
import edu.tufts.contours.data.ScoreSet;
import edu.tufts.contours.data.ScoreSingle;
import edu.tufts.contours.data.ServerUtil;
import edu.tufts.contours.data.SurveyResponse;


public class EndReportActivity extends AppCompatActivity {

    private static final String TAG = "EndReportActivity";
    private RadioGroup surveyRg; //radio group for survey questions

    private final int WRITE_EXTERNAL_STORAGE_ID = 9;
    private TextSwitcher questionText; //text representing the current survey question

    private String[] surveyQuestions; //An array of the questions to be asked.

    private int surveyIndex = 0; // The index of the survey question being displayed

    private Button nextButton; // Element representing the nextButton in the survey panel

    private Date completionDate = new Date();

    private ScoreSet results;

    private ServerUtil serverUtil;
    private DataManager dm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_report);

        serverUtil = new ServerUtil(getApplicationContext());
        dm = new DataManager(getApplicationContext());
        setScoreValues();

        surveyQuestions = getResources().getStringArray(R.array.survey_questions);

        questionText = (TextSwitcher) findViewById(R.id.question_text);
        questionText.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView myText = new TextView(EndReportActivity.this);
                myText.setTypeface(null, Typeface.BOLD);
                myText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36);
                myText.setTextColor(Color.WHITE);
                return myText;
            }
        });
        questionText.setText(surveyQuestions[surveyIndex]);
        Animation in = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
        questionText.setInAnimation(in);
        questionText.setOutAnimation(out);

        surveyRg = (RadioGroup) findViewById(R.id.survey_rg);
        surveyRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (group.getCheckedRadioButtonId() != -1) {
                    nextButton.setEnabled(true);
                }
            }
        });
        nextButton = (Button) findViewById(R.id.next_button);
        nextButton.setEnabled(false);

        performIntroAnimations();
    }

    private void performIntroAnimations() {
        RelativeLayout totalScoreLayout = (RelativeLayout) findViewById(R.id.total_score_layout);
        RelativeLayout totalTimeLayout = (RelativeLayout) findViewById(R.id.total_time_layout);
        RelativeLayout notesHitLayout = (RelativeLayout) findViewById(R.id.notes_hit_layout);
        RelativeLayout accuracyLayout = (RelativeLayout) findViewById(R.id.accuracy_layout);
        TextView successText = (TextView) findViewById(R.id.success_text);

        int animDuration = 1000;
        int animDelayInterval = 500;
        int translationOffset = -1500;

        totalScoreLayout.setTranslationX(translationOffset);
        totalTimeLayout.setTranslationX(translationOffset);
        notesHitLayout.setTranslationX(translationOffset);
        accuracyLayout.setTranslationX(translationOffset);

        totalScoreLayout.animate().translationX(0).setDuration(animDuration).
                setStartDelay(animDelayInterval).setInterpolator(new OvershootInterpolator());
        totalTimeLayout.animate().translationX(0).setDuration(animDuration).
                setStartDelay(animDelayInterval*2).setInterpolator(new OvershootInterpolator());
        notesHitLayout.animate().translationX(0).setDuration(animDuration).
                setStartDelay(animDelayInterval*3).setInterpolator(new OvershootInterpolator());
        accuracyLayout.animate().translationX(0).setDuration(animDuration).
                setStartDelay(animDelayInterval * 4).setInterpolator(new OvershootInterpolator());
        successText.animate().alpha(1f).setDuration(2000);
    }

    /**
     * Private method to set the score values based the intent passed from the TrainingActivity
     * Should be called in onCreate()
     */
    private void setScoreValues() {

        int totalScore = getIntent().getIntExtra("total_score", -1);
        TextView totalScoreValue = (TextView) findViewById(R.id.total_score_value);
        totalScoreValue.setText(Integer.toString(totalScore));

        long totalTime = getIntent().getLongExtra("total_time", -1);
        TextView totalTimeValue = (TextView) findViewById(R.id.total_time_value);
        Date date = new Date(totalTime);
        DateFormat formatter = new SimpleDateFormat("mm:ss:SSS", Locale.ENGLISH);
        String dateFormatted = formatter.format(date);
        totalTimeValue.setText(dateFormatted);

        int notesHit = getIntent().getIntExtra("notes_hit", -1);
        TextView notesHitValue = (TextView) findViewById(R.id.notes_hit_value);
        notesHitValue.setText(Integer.toString(notesHit));

        int notesMissed = getIntent().getIntExtra("notes_missed", -1);
        float accuracy = (((float)notesHit)/(notesMissed+notesHit)) * 100;
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        TextView accuracyValue = (TextView) findViewById(R.id.accuracy_value);
        accuracyValue.setText(df.format(accuracy) + "%");

        int longestStreak = getIntent().getIntExtra("longest_streak", -1);

        int averageStreak = getIntent().getIntExtra("average_streak", -1);

        String difficulty = getIntent().getStringExtra("difficulty");
        int intervalSize = getIntent().getIntExtra("interval_size", -1);

        String singles = getIntent().getStringExtra("indiv_contour_info");
        ArrayList<ScoreSingle> contourSingles = new Gson().fromJson(singles,
                new TypeToken<ArrayList<ScoreSingle>>() {}.getType());

        results = new ScoreSet(dm.getUserAlias(), difficulty, intervalSize + 1, totalScore, totalTime,
                notesHit, notesMissed, longestStreak, averageStreak, completionDate, contourSingles);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case WRITE_EXTERNAL_STORAGE_ID: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    return;
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_end_report, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Display the next research survey question
     */
    private void displayNextSurveyQuestion() {
        surveyIndex++;
        questionText.setText(surveyQuestions[surveyIndex]);
        nextButton.setEnabled(false);
        if(surveyIndex == surveyQuestions.length - 1) {
            nextButton.setText("DONE>");
        }
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        WRITE_EXTERNAL_STORAGE_ID);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    /**
     * Click function for the survey's next button
     * @param view
     */
    public void onNextButtonClicked(View view) throws IOException {
        int rbId = surveyRg.getCheckedRadioButtonId();
        RadioButton selected = (RadioButton) findViewById(rbId);
        String response = selected.getText().toString();
        results.getSurveyResponses().add(new SurveyResponse(surveyQuestions[surveyIndex], response));

        if (surveyIndex < surveyQuestions.length - 1) {
            displayNextSurveyQuestion();
            surveyRg.clearCheck();
            nextButton.setEnabled(false);
        } else {
            int permission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if(permission == PackageManager.PERMISSION_GRANTED) {
                serverUtil.postScoreSet(results);
                dm.writeScoreSetToExternalStorage(results, results.getUser_id());
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                this.finish();
            } else {
                checkPermissions();
            }
        }
    }
}

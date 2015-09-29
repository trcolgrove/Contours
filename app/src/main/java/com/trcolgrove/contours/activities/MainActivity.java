package com.trcolgrove.contours.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.trcolgrove.contours.R;
import com.trcolgrove.contours.contoursGame.DataManager;
import com.trcolgrove.contours.contoursGame.ServerUtil;
import com.trcolgrove.contours.contoursGame.TrainingActivity;

/**
 * MainActivity
 *
 * The main activity for the contours app.
 * Displays a simple interface that presents the user with
 * the option to play the game at various difficulty settings
 */
public class MainActivity extends ActionBarActivity {

    private RelativeLayout difficultyMenu; // Select difficulty menu
    private LinearLayout intervalMenu;
    private Intent trainingIntent;
    private int difficultyButton;
    RelativeLayout soundMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        difficultyMenu = (RelativeLayout) findViewById(R.id.difficulty_menu);
        intervalMenu = (LinearLayout) findViewById(R.id.intervals);
        soundMenu = (RelativeLayout) findViewById(R.id.sound_menu);
        TextView aliasText = (TextView) findViewById(R.id.alias_text);

        DataManager dm = new DataManager(getApplicationContext());
        String alias = dm.getUserAlias();

        trainingIntent = new Intent(getApplicationContext(), TrainingActivity.class);


        if(alias == null) {
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
            this.finish();
        }


        aliasText.setText("Alias: " + alias);
        ServerUtil serverUtil = new ServerUtil(getApplicationContext());
        serverUtil.uploadPendingData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void playButtonClicked(View view) {
        difficultyMenu.setAlpha(0);
        difficultyMenu.setVisibility(View.VISIBLE);
        difficultyMenu.animate().alpha(1);
    }

    /* Select Difficulty Buttons */

    public void difficultyButtonClicked(View view) {
        String difficulty = ((Button)view).getText().toString();
        trainingIntent.putExtra("difficulty", difficulty);
        switch(difficulty) {
            case "easy":
                difficultyButton = R.id.easy_button;
                break;
            case "medium":
                difficultyButton = R.id.medium_button;
                break;
            case "hard":
                difficultyButton = R.id.hard_button;
                break;
        }
        showIntervalMenu();
    }

    private void showIntervalMenu() {
        intervalMenu.setAlpha(0);

        RelativeLayout.LayoutParams lp =
                (RelativeLayout.LayoutParams)intervalMenu.getLayoutParams();
        lp.addRule(RelativeLayout.ALIGN_BOTTOM, 0);
        lp.addRule(RelativeLayout.ALIGN_LEFT, 0);
        lp.addRule(RelativeLayout.ALIGN_RIGHT, 0);

        difficultyMenu.updateViewLayout(intervalMenu, lp);

        lp.addRule(RelativeLayout.ALIGN_LEFT, difficultyButton);
        lp.addRule(RelativeLayout.ALIGN_RIGHT, difficultyButton);

        intervalMenu.setVisibility(View.VISIBLE);
        intervalMenu.animate().alpha(1);
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

    public void intervalButtonClicked(View view) {
        String intervalSize = ((Button)view).getText().toString();
        trainingIntent.putExtra("interval_size", intervalSize);
        showSoundMenu();
    }


    public void showSoundMenu() {
        difficultyMenu.setVisibility(View.GONE);
        soundMenu.setAlpha(0);
        soundMenu.setVisibility(View.VISIBLE);
        soundMenu.animate().alpha(1);
    }

    public void selectSoundBtnPress(View view) {
        String sound = ((Button)view).getText().toString();
        trainingIntent.putExtra("sound", sound);
        startActivity(trainingIntent);
        this.finish();
    }


}

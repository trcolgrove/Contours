package edu.tufts.contours.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Environment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.trcolgrove.daoentries.DaoMaster;
import com.trcolgrove.daoentries.DaoSession;
import com.trcolgrove.daoentries.StoredSet;
import com.trcolgrove.daoentries.StoredSetDao;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * Singleton class implementing simple localized data storage
 * through greenDao implementation.
 *
 * Handles set up of orm and basic storage needs
 * Access is provided to scoreSetDao and surveyResponse for
 * making queries
 */
public class DataManager {

    private static final String TAG = "DataManager";

    private Context context;
    public StoredSetDao storedSetDao;

    public static final String PREFS_NAME = "PrefsFile";


    public DataManager(Context context) {
        this.context = context;
    }

    public void storeStoredSet(StoredSet s) {
        new AsyncTask<StoredSet, Void, Void> (){
            @Override
            protected Void doInBackground(StoredSet... params) {
                DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "scores-db", null);
                SQLiteDatabase db = helper.getWritableDatabase();
                DaoMaster daoMaster = new DaoMaster(db);
                DaoSession daoSession = daoMaster.newSession();
                StoredSetDao storedSetDao = daoSession.getStoredSetDao();
                try {
                    storedSetDao.insertOrReplace(params[0]);
                } finally {
                    daoMaster.getDatabase().close();
                    daoSession.getDatabase().close();
                    db.close();
                    helper.close();
                    daoSession.clear();
                    db=null;
                    daoSession=null;
                }
                return null;
            }
        }.execute(s);
    }

    public String getUserAlias() {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, 0);
        return preferences.getString("alias", null);
    }

    public void setUserAlias(String alias) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("alias", alias);
        editor.apply();
    }

    private Map<String, String> getObjectAsMap(Object object) {
        Gson gson = new Gson();
        String objectJson = gson.toJson(object);
        Type stringStringMap = new TypeToken<Map<String, String>>(){}.getType();
        return gson.fromJson(objectJson, stringStringMap);
    }

    private Map<String,String> getScoreSetAsMap(ScoreSet scoreSet) {
        Map<String, String> scoreSetMap = getObjectAsMap(scoreSet);
        Map<String, String> surveyResponseMap = scoreSet.getSurveyResponsesAsMap();
        scoreSetMap.putAll(surveyResponseMap);
        return scoreSetMap;
    }

    private File getResultsDir(String userId) {
        File externalStorage = Environment.getExternalStorageDirectory();
        File dir = new File(externalStorage.getAbsolutePath() + "/test_results/" + userId + "/");

        dir.mkdirs();
        return dir;
    }

    public void writeScoreSetToExternalStorage(ScoreSet scoreSet, String userId) throws IOException {
        Gson gson = new Gson();
        ArrayList<String> headers = new ArrayList<>(ScoreSingle.FIELD_NAMES);

        DateFormat df = new SimpleDateFormat("MM_dd_yyyy_HH_mm_ss");
        String now = df.format(new Date());

        File resultsDir = getResultsDir(userId);
        File detailedResults = new File(resultsDir, "test_result" + now + ".csv");
        File userOverviewResults = new File(resultsDir, userId + "overall_results.csv");

        CSVWriter csvWriter = new CSVWriter(headers, detailedResults, false);
        csvWriter.writeHeaders(headers);

        for(ScoreSingle scoreSingle: scoreSet.getSingles()) {
            Map<String, String> scoreData = getObjectAsMap(scoreSingle);
            csvWriter.writeRow(scoreData);
        }
        csvWriter.close();

        Map<String, String> scoreSetMap = getScoreSetAsMap(scoreSet);
        ArrayList<String> scoreSetHeaders = new ArrayList<>(ScoreSet.FIELD_NAMES);
        scoreSetHeaders.addAll(scoreSet.getSurveyResponsesAsMap().keySet());

        csvWriter = new CSVWriter(scoreSetHeaders, userOverviewResults, true);
        if(userOverviewResults.length() == 0) {
            csvWriter.writeHeaders(scoreSetHeaders);
        }
        csvWriter.writeRow(scoreSetMap);
        csvWriter.close();
    }

}

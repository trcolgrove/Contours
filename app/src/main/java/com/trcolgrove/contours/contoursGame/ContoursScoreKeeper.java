package com.trcolgrove.contours.contoursGame;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.IntDef;

import com.trcolgrove.contours.events.ScoreEvent;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import de.greenrobot.event.EventBus;

/**
 * Created by Thomas on 7/14/15.
 */
public class ContoursScoreKeeper implements ScoreKeeper {

    private int score;
    private int multiplier;
    private long baseTime;
    private long timeSinceContourStart;

    private int notesHit = 0;
    private int notesMissed = 0;

    private int streak = 0;
    private int longestStreak = 0;
    private int numStreaks = 1;

    @IntDef({NOTE_HIT, NOTE_MISS, CONTOUR_COMPLETE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface GameEvent {}
    public static final int NOTE_MISS = 0;
    public static final int NOTE_HIT = 1;
    public static final int CONTOUR_COMPLETE = 2;

    public ContoursScoreKeeper(long baseTime) {
        this.score = 0;
        this.multiplier = 1;
        this.baseTime = baseTime;
        timeSinceContourStart = baseTime;
    }

    public void updateScore(@GameEvent int gameInfo) {
        int scoreIncrement = 0;
        if(gameInfo == NOTE_HIT) {
            scoreIncrement = noteHit();
            score += scoreIncrement;
        } else if(gameInfo == NOTE_MISS) {
            scoreIncrement = noteMiss();
            score += scoreIncrement;
        } else if(gameInfo == CONTOUR_COMPLETE) {
            scoreIncrement = contourComplete();
            score += scoreIncrement;
        }
        EventBus.getDefault().post(new ScoreEvent(score, multiplier, scoreIncrement));
    }

    private int noteMiss() {
        multiplier = 1;
        notesMissed++;
        streak = 0;
        return -100;
    }

    private int noteHit() {
        int scoreIncrement = (100 * multiplier);
        notesHit++;
        streak++;
        if(streak > longestStreak) {
            longestStreak = streak;
        }
        return scoreIncrement;
    }

    private int contourComplete() {
        noteHit();
        int scoreIncrement = Math.max(0, ((int)(((10000)) -
                ((SystemClock.elapsedRealtime() - timeSinceContourStart)/1000)*250) * multiplier));
        incrementMultiplier();
        timeSinceContourStart = SystemClock.elapsedRealtime();
        return scoreIncrement;
    }

    private void incrementMultiplier() {
        if(multiplier < 8) {
            multiplier++;
        }
    }

    public int getScore() {
        return this.score;
    }

    public int getMultiplier() {
        return this.multiplier;
    }

    public int getNotesHit() {
        return notesHit;
    }

    public int getNotesMissed() {
        return notesMissed;
    }

    public int getStreak() {
        return streak;
    }

    public int getLongestStreak() {
        return longestStreak;
    }

    public int getAverageStreak() {
        return ((notesHit+notesMissed)/(notesMissed+1));
    }

    /**
     * Returns the information kept in the ScoreKeeper object
     * as a Map. For example, the map might contain
     * the pair "total_score" -> 9000(int) "total_time"->3.56(Duration)
     *
     * @return a map representing the score keepers metrics and their values
     */
    public Bundle getScoreBundle() {
        Bundle scoreBundle = new Bundle();

        scoreBundle.putInt("average_streak", getAverageStreak());
        scoreBundle.putInt("total_score", score);
        scoreBundle.putLong("total_time", SystemClock.elapsedRealtime() - baseTime);
        scoreBundle.putInt("longest_streak", longestStreak);
        scoreBundle.putInt("notes_hit", notesHit);
        scoreBundle.putInt("notes_missed", notesMissed);

        return scoreBundle;
    }

}

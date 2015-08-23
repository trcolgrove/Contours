package com.trcolgrove.daoentries;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "SCORE_SET".
 */
public class ScoreSet {

    private Long id;
    private String difficulty;
    private int total_score;
    private Long elapsed_time;
    private Integer notes_hit;
    private Integer notes_missed;
    private Integer longest_streak;
    private Integer average_streak;
    private java.util.Date date;
    private boolean uploaded;

    public ScoreSet() {
    }

    public ScoreSet(Long id) {
        this.id = id;
    }

    public ScoreSet(Long id, String difficulty, int total_score, Long elapsed_time, Integer notes_hit, Integer notes_missed, Integer longest_streak, Integer average_streak, java.util.Date date, boolean uploaded) {
        this.id = id;
        this.difficulty = difficulty;
        this.total_score = total_score;
        this.elapsed_time = elapsed_time;
        this.notes_hit = notes_hit;
        this.notes_missed = notes_missed;
        this.longest_streak = longest_streak;
        this.average_streak = average_streak;
        this.date = date;
        this.uploaded = uploaded;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public int getTotal_score() {
        return total_score;
    }

    public void setTotal_score(int total_score) {
        this.total_score = total_score;
    }

    public Long getElapsed_time() {
        return elapsed_time;
    }

    public void setElapsed_time(Long elapsed_time) {
        this.elapsed_time = elapsed_time;
    }

    public Integer getNotes_hit() {
        return notes_hit;
    }

    public void setNotes_hit(Integer notes_hit) {
        this.notes_hit = notes_hit;
    }

    public Integer getNotes_missed() {
        return notes_missed;
    }

    public void setNotes_missed(Integer notes_missed) {
        this.notes_missed = notes_missed;
    }

    public Integer getLongest_streak() {
        return longest_streak;
    }

    public void setLongest_streak(Integer longest_streak) {
        this.longest_streak = longest_streak;
    }

    public Integer getAverage_streak() {
        return average_streak;
    }

    public void setAverage_streak(Integer average_streak) {
        this.average_streak = average_streak;
    }

    public java.util.Date getDate() {
        return date;
    }

    public void setDate(java.util.Date date) {
        this.date = date;
    }

    public boolean getUploaded() {
        return uploaded;
    }

    public void setUploaded(boolean uploaded) {
        this.uploaded = uploaded;
    }

}

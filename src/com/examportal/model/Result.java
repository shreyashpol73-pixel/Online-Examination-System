package com.examportal.model;

import java.sql.Timestamp;

/**
 * Model class representing an Exam Result record for a student.
 * Includes score, status, timing, and transient fields for UI and reporting displays.
 */
public class Result {

    private int resultId;
    private int userId;
    private int examId;
    private int score;
    private int totalMarks;
    private double percentage;
    private String status; // "PASS" or "FAIL"
    private int timeTakenSeconds;
    private Timestamp attemptedAt;

    // Transient fields for display purposes
    private String examTitle;
    private String studentName;

    /**
     * Default no-arg constructor.
     */
    public Result() {
    }

    /**
     * Parameterized constructor without resultId and attemptedAt (used when scoring and saving a result).
     *
     * @param userId user who completed the exam
     * @param examId exam identifier
     * @param score score achieved
     * @param totalMarks maximum possible marks
     * @param percentage percentage scored
     * @param status "PASS" or "FAIL"
     * @param timeTakenSeconds time taken in seconds
     */
    public Result(int userId, int examId, int score, int totalMarks, double percentage, String status, int timeTakenSeconds) {
        this.userId = userId;
        this.examId = examId;
        this.score = score;
        this.totalMarks = totalMarks;
        this.percentage = percentage;
        this.status = status;
        this.timeTakenSeconds = timeTakenSeconds;
    }

    /**
     * Full parameterized constructor matching DB columns.
     *
     * @param resultId unique primary key
     * @param userId user who completed the exam
     * @param examId exam identifier
     * @param score score achieved
     * @param totalMarks maximum possible marks
     * @param percentage percentage scored
     * @param status "PASS" or "FAIL"
     * @param timeTakenSeconds time taken in seconds
     * @param attemptedAt timestamp of exam completion
     */
    public Result(int resultId, int userId, int examId, int score, int totalMarks, double percentage, String status, int timeTakenSeconds, Timestamp attemptedAt) {
        this.resultId = resultId;
        this.userId = userId;
        this.examId = examId;
        this.score = score;
        this.totalMarks = totalMarks;
        this.percentage = percentage;
        this.status = status;
        this.timeTakenSeconds = timeTakenSeconds;
        this.attemptedAt = attemptedAt;
    }

    /**
     * Full parameterized constructor including joined display fields.
     */
    public Result(int resultId, int userId, int examId, int score, int totalMarks, double percentage, String status, int timeTakenSeconds, Timestamp attemptedAt, String examTitle, String studentName) {
        this.resultId = resultId;
        this.userId = userId;
        this.examId = examId;
        this.score = score;
        this.totalMarks = totalMarks;
        this.percentage = percentage;
        this.status = status;
        this.timeTakenSeconds = timeTakenSeconds;
        this.attemptedAt = attemptedAt;
        this.examTitle = examTitle;
        this.studentName = studentName;
    }

    public int getResultId() {
        return resultId;
    }

    public void setResultId(int resultId) {
        this.resultId = resultId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getExamId() {
        return examId;
    }

    public void setExamId(int examId) {
        this.examId = examId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(int totalMarks) {
        this.totalMarks = totalMarks;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTimeTakenSeconds() {
        return timeTakenSeconds;
    }

    public void setTimeTakenSeconds(int timeTakenSeconds) {
        this.timeTakenSeconds = timeTakenSeconds;
    }

    public Timestamp getAttemptedAt() {
        return attemptedAt;
    }

    public void setAttemptedAt(Timestamp attemptedAt) {
        this.attemptedAt = attemptedAt;
    }

    public String getExamTitle() {
        return examTitle;
    }

    public void setExamTitle(String examTitle) {
        this.examTitle = examTitle;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    @Override
    public String toString() {
        return "Result{" +
                "resultId=" + resultId +
                ", userId=" + userId +
                ", examId=" + examId +
                ", score=" + score +
                ", totalMarks=" + totalMarks +
                ", percentage=" + percentage +
                ", status='" + status + '\'' +
                ", timeTakenSeconds=" + timeTakenSeconds +
                ", attemptedAt=" + attemptedAt +
                ", examTitle='" + examTitle + '\'' +
                ", studentName='" + studentName + '\'' +
                '}';
    }
}

package com.examportal.model;

import java.sql.Timestamp;

/**
 * Model class representing an Exam entity created by administrators and attempted by students.
 */
public class Exam {

    private int examId;
    private String title;
    private String description;
    private int durationMinutes;
    private int totalMarks;
    private int passingMarks;
    private boolean isActive;
    private int createdBy;
    private Timestamp createdAt;

    /**
     * Default no-arg constructor.
     */
    public Exam() {
        this.durationMinutes = 30;
        this.isActive = true;
    }

    /**
     * Parameterized constructor without examId and createdAt (used during exam creation).
     *
     * @param title exam title
     * @param description brief syllabus or exam guidelines
     * @param durationMinutes time limit in minutes
     * @param totalMarks maximum possible score
     * @param passingMarks minimum score to pass
     * @param isActive whether the exam is active for taking
     * @param createdBy user_id of the creator (Admin)
     */
    public Exam(String title, String description, int durationMinutes, int totalMarks, int passingMarks, boolean isActive, int createdBy) {
        this.title = title;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.totalMarks = totalMarks;
        this.passingMarks = passingMarks;
        this.isActive = isActive;
        this.createdBy = createdBy;
    }

    /**
     * Full parameterized constructor.
     *
     * @param examId unique identifier
     * @param title exam title
     * @param description brief syllabus or exam guidelines
     * @param durationMinutes time limit in minutes
     * @param totalMarks maximum possible score
     * @param passingMarks minimum score to pass
     * @param isActive whether the exam is active for taking
     * @param createdBy user_id of the creator (Admin)
     * @param createdAt timestamp when the exam was created
     */
    public Exam(int examId, String title, String description, int durationMinutes, int totalMarks, int passingMarks, boolean isActive, int createdBy, Timestamp createdAt) {
        this.examId = examId;
        this.title = title;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.totalMarks = totalMarks;
        this.passingMarks = passingMarks;
        this.isActive = isActive;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public int getExamId() {
        return examId;
    }

    public void setExamId(int examId) {
        this.examId = examId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public int getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(int totalMarks) {
        this.totalMarks = totalMarks;
    }

    public int getPassingMarks() {
        return passingMarks;
    }

    public void setPassingMarks(int passingMarks) {
        this.passingMarks = passingMarks;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Exam{" +
                "examId=" + examId +
                ", title='" + title + '\'' +
                ", durationMinutes=" + durationMinutes +
                ", totalMarks=" + totalMarks +
                ", passingMarks=" + passingMarks +
                ", isActive=" + isActive +
                ", createdBy=" + createdBy +
                ", createdAt=" + createdAt +
                '}';
    }
}

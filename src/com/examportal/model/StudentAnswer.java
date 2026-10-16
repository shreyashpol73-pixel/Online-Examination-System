package com.examportal.model;

/**
 * Model class representing a student's answer to an individual exam question.
 */
public class StudentAnswer {

    private int answerId;
    private int resultId;
    private int questionId;
    private String selectedAnswer;
    private boolean isCorrect;

    /**
     * Default no-arg constructor.
     */
    public StudentAnswer() {
    }

    /**
     * Parameterized constructor without answerId (for recording responses during exam submission).
     *
     * @param resultId foreign key to results table
     * @param questionId foreign key to questions table
     * @param selectedAnswer user-submitted response
     * @param isCorrect flag indicating if submission matches question's correct answer
     */
    public StudentAnswer(int resultId, int questionId, String selectedAnswer, boolean isCorrect) {
        this.resultId = resultId;
        this.questionId = questionId;
        this.selectedAnswer = selectedAnswer;
        this.isCorrect = isCorrect;
    }

    /**
     * Parameterized constructor without answerId and resultId (convenient when preparing answers before result is inserted).
     *
     * @param questionId foreign key to questions table
     * @param selectedAnswer user-submitted response
     * @param isCorrect flag indicating correctness
     */
    public StudentAnswer(int questionId, String selectedAnswer, boolean isCorrect) {
        this.questionId = questionId;
        this.selectedAnswer = selectedAnswer;
        this.isCorrect = isCorrect;
    }

    /**
     * Full parameterized constructor.
     *
     * @param answerId unique primary key
     * @param resultId foreign key to results table
     * @param questionId foreign key to questions table
     * @param selectedAnswer user-submitted response
     * @param isCorrect flag indicating correctness
     */
    public StudentAnswer(int answerId, int resultId, int questionId, String selectedAnswer, boolean isCorrect) {
        this.answerId = answerId;
        this.resultId = resultId;
        this.questionId = questionId;
        this.selectedAnswer = selectedAnswer;
        this.isCorrect = isCorrect;
    }

    public int getAnswerId() {
        return answerId;
    }

    public void setAnswerId(int answerId) {
        this.answerId = answerId;
    }

    public int getResultId() {
        return resultId;
    }

    public void setResultId(int resultId) {
        this.resultId = resultId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getSelectedAnswer() {
        return selectedAnswer;
    }

    public void setSelectedAnswer(String selectedAnswer) {
        this.selectedAnswer = selectedAnswer;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    @Override
    public String toString() {
        return "StudentAnswer{" +
                "answerId=" + answerId +
                ", resultId=" + resultId +
                ", questionId=" + questionId +
                ", selectedAnswer='" + selectedAnswer + '\'' +
                ", isCorrect=" + isCorrect +
                '}';
    }
}

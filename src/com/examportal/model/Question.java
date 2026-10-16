package com.examportal.model;

/**
 * Model class representing a Question belonging to an Exam.
 * Supports types: MCQ, TRUE_FALSE, and FILL_BLANK.
 */
public class Question {

    public static final String TYPE_MCQ = "MCQ";
    public static final String TYPE_TRUE_FALSE = "TRUE_FALSE";
    public static final String TYPE_FILL_BLANK = "FILL_BLANK";

    private int questionId;
    private int examId;
    private String questionText;
    private String questionType; // MCQ, TRUE_FALSE, FILL_BLANK
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctAnswer;
    private int marks;

    /**
     * Default no-arg constructor.
     */
    public Question() {
        this.marks = 1;
        this.questionType = TYPE_MCQ;
    }

    /**
     * Parameterized constructor without questionId (for adding a new question).
     *
     * @param examId foreign key to exams table
     * @param questionText the question content
     * @param questionType type of question (MCQ, TRUE_FALSE, FILL_BLANK)
     * @param optionA choice A
     * @param optionB choice B
     * @param optionC choice C
     * @param optionD choice D
     * @param correctAnswer correct option letter or text
     * @param marks marks allotted to this question
     */
    public Question(int examId, String questionText, String questionType, String optionA, String optionB, String optionC, String optionD, String correctAnswer, int marks) {
        this.examId = examId;
        this.questionText = questionText;
        this.questionType = questionType;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAnswer = correctAnswer;
        this.marks = marks;
    }

    /**
     * Full parameterized constructor.
     *
     * @param questionId unique primary key
     * @param examId foreign key to exams table
     * @param questionText the question content
     * @param questionType type of question (MCQ, TRUE_FALSE, FILL_BLANK)
     * @param optionA choice A
     * @param optionB choice B
     * @param optionC choice C
     * @param optionD choice D
     * @param correctAnswer correct option letter or text
     * @param marks marks allotted to this question
     */
    public Question(int questionId, int examId, String questionText, String questionType, String optionA, String optionB, String optionC, String optionD, String correctAnswer, int marks) {
        this.questionId = questionId;
        this.examId = examId;
        this.questionText = questionText;
        this.questionType = questionType;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAnswer = correctAnswer;
        this.marks = marks;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getExamId() {
        return examId;
    }

    public void setExamId(int examId) {
        this.examId = examId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public int getMarks() {
        return marks;
    }

    public void setMarks(int marks) {
        this.marks = marks;
    }

    @Override
    public String toString() {
        return "Question{" +
                "questionId=" + questionId +
                ", examId=" + examId +
                ", questionType='" + questionType + '\'' +
                ", questionText='" + questionText + '\'' +
                ", correctAnswer='" + correctAnswer + '\'' +
                ", marks=" + marks +
                '}';
    }
}

package com.examportal.dao;

import com.examportal.db.DatabaseConnection;
import com.examportal.model.Result;
import com.examportal.model.StudentAnswer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Result and StudentAnswer entities.
 * Handles transactional result submission, score tracking, and performance queries.
 */
public class ResultDAO {

    private static final Logger LOGGER = Logger.getLogger(ResultDAO.class.getName());

    /**
     * Saves an exam result and all associated student answers in a single database transaction.
     * If any part fails, the entire transaction is rolled back.
     *
     * @param result the Result object containing score and exam details
     * @param answers list of StudentAnswer items submitted for this exam
     * @return true if both result and all answers were committed successfully; false otherwise
     */
    public boolean saveResult(Result result, List<StudentAnswer> answers) {
        String insertResultSql = "INSERT INTO results (user_id, exam_id, score, total_marks, percentage, status, time_taken_seconds) " +
                                 "VALUES (?, ?, ?, ?, ?, ?, ?)";
        String insertAnswerSql = "INSERT INTO student_answers (result_id, question_id, selected_answer, is_correct) " +
                                 "VALUES (?, ?, ?, ?)";

        Connection conn = DatabaseConnection.getInstance().getConnection();
        if (conn == null) {
            LOGGER.severe("Database connection is unavailable for saveResult.");
            return false;
        }

        boolean originalAutoCommit = true;
        try {
            originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false); // Begin transaction

            int generatedResultId = -1;
            try (PreparedStatement pstmtResult = conn.prepareStatement(insertResultSql, Statement.RETURN_GENERATED_KEYS)) {
                pstmtResult.setInt(1, result.getUserId());
                pstmtResult.setInt(2, result.getExamId());
                pstmtResult.setInt(3, result.getScore());
                pstmtResult.setInt(4, result.getTotalMarks());
                pstmtResult.setDouble(5, result.getPercentage());
                pstmtResult.setString(6, result.getStatus());
                pstmtResult.setInt(7, result.getTimeTakenSeconds());

                int affectedRows = pstmtResult.executeUpdate();
                if (affectedRows == 0) {
                    conn.rollback();
                    return false;
                }

                try (ResultSet rs = pstmtResult.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedResultId = rs.getInt(1);
                        result.setResultId(generatedResultId);
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }

            // Insert each student answer linked to the generated result_id
            if (answers != null && !answers.isEmpty()) {
                try (PreparedStatement pstmtAnswer = conn.prepareStatement(insertAnswerSql)) {
                    for (StudentAnswer ans : answers) {
                        pstmtAnswer.setInt(1, generatedResultId);
                        pstmtAnswer.setInt(2, ans.getQuestionId());
                        pstmtAnswer.setString(3, ans.getSelectedAnswer());
                        pstmtAnswer.setBoolean(4, ans.isCorrect());
                        pstmtAnswer.addBatch();
                    }
                    pstmtAnswer.executeBatch();
                }
            }

            conn.commit(); // Commit transaction
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Transaction failed while saving exam result. Rolling back...", e);
            try {
                conn.rollback();
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Failed to rollback transaction", ex);
            }
            return false;
        } finally {
            try {
                conn.setAutoCommit(originalAutoCommit);
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Failed to reset connection autoCommit property", e);
            }
        }
    }

    /**
     * Retrieves all exam results for a specific student, joined with exam title.
     *
     * @param userId unique identifier of the user
     * @return List of Result objects for the user
     */
    public List<Result> getResultsByUserId(int userId) {
        List<Result> list = new ArrayList<>();
        String sql = "SELECT r.result_id, r.user_id, r.exam_id, r.score, r.total_marks, r.percentage, " +
                     "r.status, r.time_taken_seconds, r.attempted_at, " +
                     "e.title AS exam_title, u.full_name AS student_name " +
                     "FROM results r " +
                     "JOIN exams e ON r.exam_id = e.exam_id " +
                     "JOIN users u ON r.user_id = u.user_id " +
                     "WHERE r.user_id = ? " +
                     "ORDER BY r.attempted_at DESC";

        Connection conn = DatabaseConnection.getInstance().getConnection();
        if (conn == null) {
            LOGGER.severe("Database connection is unavailable for getResultsByUserId.");
            return list;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToResult(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving results for user ID: " + userId, e);
        }
        return list;
    }

    /**
     * Retrieves all student results for a specific exam, joined with student name.
     *
     * @param examId unique identifier of the exam
     * @return List of Result objects for the exam
     */
    public List<Result> getResultsByExamId(int examId) {
        List<Result> list = new ArrayList<>();
        String sql = "SELECT r.result_id, r.user_id, r.exam_id, r.score, r.total_marks, r.percentage, " +
                     "r.status, r.time_taken_seconds, r.attempted_at, " +
                     "e.title AS exam_title, u.full_name AS student_name " +
                     "FROM results r " +
                     "JOIN exams e ON r.exam_id = e.exam_id " +
                     "JOIN users u ON r.user_id = u.user_id " +
                     "WHERE r.exam_id = ? " +
                     "ORDER BY r.attempted_at DESC";

        Connection conn = DatabaseConnection.getInstance().getConnection();
        if (conn == null) {
            LOGGER.severe("Database connection is unavailable for getResultsByExamId.");
            return list;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, examId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToResult(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving results for exam ID: " + examId, e);
        }
        return list;
    }

    /**
     * Retrieves all results across all exams and users, joined with user full name and exam title.
     *
     * @return List of all Result objects
     */
    public List<Result> getAllResults() {
        List<Result> list = new ArrayList<>();
        String sql = "SELECT r.result_id, r.user_id, r.exam_id, r.score, r.total_marks, r.percentage, " +
                     "r.status, r.time_taken_seconds, r.attempted_at, " +
                     "e.title AS exam_title, u.full_name AS student_name " +
                     "FROM results r " +
                     "JOIN exams e ON r.exam_id = e.exam_id " +
                     "JOIN users u ON r.user_id = u.user_id " +
                     "ORDER BY r.attempted_at DESC";

        Connection conn = DatabaseConnection.getInstance().getConnection();
        if (conn == null) {
            LOGGER.severe("Database connection is unavailable for getAllResults.");
            return list;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToResult(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all exam results", e);
        }
        return list;
    }

    /**
     * Checks if a given student has already attempted a particular exam.
     *
     * @param userId unique user identifier
     * @param examId unique exam identifier
     * @return true if an attempt record exists; false otherwise
     */
    public boolean hasStudentAttemptedExam(int userId, int examId) {
        String sql = "SELECT 1 FROM results WHERE user_id = ? AND exam_id = ? LIMIT 1";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        if (conn == null) {
            LOGGER.severe("Database connection is unavailable for hasStudentAttemptedExam.");
            return false;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, examId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking attempt status for user ID: " + userId + ", exam ID: " + examId, e);
        }
        return false;
    }

    /**
     * Retrieves the individual question answers submitted for a specific exam attempt.
     *
     * @param resultId unique result identifier
     * @return List of StudentAnswer records
     */
    public List<StudentAnswer> getAnswersByResultId(int resultId) {
        List<StudentAnswer> list = new ArrayList<>();
        String sql = "SELECT answer_id, result_id, question_id, selected_answer, is_correct " +
                     "FROM student_answers WHERE result_id = ? ORDER BY answer_id ASC";

        Connection conn = DatabaseConnection.getInstance().getConnection();
        if (conn == null) {
            LOGGER.severe("Database connection is unavailable for getAnswersByResultId.");
            return list;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, resultId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    StudentAnswer answer = new StudentAnswer();
                    answer.setAnswerId(rs.getInt("answer_id"));
                    answer.setResultId(rs.getInt("result_id"));
                    answer.setQuestionId(rs.getInt("question_id"));
                    answer.setSelectedAnswer(rs.getString("selected_answer"));
                    answer.setCorrect(rs.getBoolean("is_correct"));
                    list.add(answer);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving student answers for result ID: " + resultId, e);
        }
        return list;
    }

    /**
     * Helper method to map a joined ResultSet row to a Result entity.
     *
     * @param rs active ResultSet positioned at the desired record
     * @return populated Result object
     * @throws SQLException on column reading error
     */
    private Result mapResultSetToResult(ResultSet rs) throws SQLException {
        Result r = new Result();
        r.setResultId(rs.getInt("result_id"));
        r.setUserId(rs.getInt("user_id"));
        r.setExamId(rs.getInt("exam_id"));
        r.setScore(rs.getInt("score"));
        r.setTotalMarks(rs.getInt("total_marks"));
        r.setPercentage(rs.getDouble("percentage"));
        r.setStatus(rs.getString("status"));
        r.setTimeTakenSeconds(rs.getInt("time_taken_seconds"));
        r.setAttemptedAt(rs.getTimestamp("attempted_at"));
        r.setExamTitle(rs.getString("exam_title"));
        r.setStudentName(rs.getString("student_name"));
        return r;
    }
}

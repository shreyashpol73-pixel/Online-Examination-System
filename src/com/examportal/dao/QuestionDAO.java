package com.examportal.dao;

import com.examportal.db.DatabaseConnection;
import com.examportal.model.Question;

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
 * Data Access Object for Question entity.
 * Handles adding, modifying, deleting, and querying exam questions.
 */
public class QuestionDAO {

    private static final Logger LOGGER = Logger.getLogger(QuestionDAO.class.getName());

    /**
     * Adds a new question to an exam.
     *
     * @param question Question object to persist
     * @return true if inserted successfully; false otherwise
     */
    public boolean addQuestion(Question question) {
        String sql = "INSERT INTO questions (exam_id, question_text, question_type, option_a, option_b, option_c, option_d, correct_answer, marks) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        if (conn == null) {
            LOGGER.severe("Database connection is unavailable for addQuestion.");
            return false;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, question.getExamId());
            pstmt.setString(2, question.getQuestionText());
            pstmt.setString(3, question.getQuestionType());
            pstmt.setString(4, question.getOptionA());
            pstmt.setString(5, question.getOptionB());
            pstmt.setString(6, question.getOptionC());
            pstmt.setString(7, question.getOptionD());
            pstmt.setString(8, question.getCorrectAnswer());
            pstmt.setInt(9, question.getMarks());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        question.setQuestionId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting question into exam ID: " + question.getExamId(), e);
        }
        return false;
    }

    /**
     * Updates an existing question's properties and options.
     *
     * @param question Question object with updated properties
     * @return true if updated successfully; false otherwise
     */
    public boolean updateQuestion(Question question) {
        String sql = "UPDATE questions SET exam_id = ?, question_text = ?, question_type = ?, " +
                     "option_a = ?, option_b = ?, option_c = ?, option_d = ?, correct_answer = ?, marks = ? " +
                     "WHERE question_id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        if (conn == null) {
            LOGGER.severe("Database connection is unavailable for updateQuestion.");
            return false;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, question.getExamId());
            pstmt.setString(2, question.getQuestionText());
            pstmt.setString(3, question.getQuestionType());
            pstmt.setString(4, question.getOptionA());
            pstmt.setString(5, question.getOptionB());
            pstmt.setString(6, question.getOptionC());
            pstmt.setString(7, question.getOptionD());
            pstmt.setString(8, question.getCorrectAnswer());
            pstmt.setInt(9, question.getMarks());
            pstmt.setInt(10, question.getQuestionId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating question ID: " + question.getQuestionId(), e);
        }
        return false;
    }

    /**
     * Deletes a question by its primary key ID.
     *
     * @param questionId unique question identifier
     * @return true if deleted successfully; false otherwise
     */
    public boolean deleteQuestion(int questionId) {
        String sql = "DELETE FROM questions WHERE question_id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        if (conn == null) {
            LOGGER.severe("Database connection is unavailable for deleteQuestion.");
            return false;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, questionId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting question ID: " + questionId, e);
        }
        return false;
    }

    /**
     * Retrieves all questions associated with a given exam ID.
     *
     * @param examId unique identifier of the exam
     * @return List of Question objects belonging to the exam
     */
    public List<Question> getQuestionsByExamId(int examId) {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT question_id, exam_id, question_text, question_type, option_a, option_b, option_c, option_d, correct_answer, marks " +
                     "FROM questions WHERE exam_id = ? ORDER BY question_id ASC";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        if (conn == null) {
            LOGGER.severe("Database connection is unavailable for getQuestionsByExamId.");
            return questions;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, examId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    questions.add(mapResultSetToQuestion(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving questions for exam ID: " + examId, e);
        }
        return questions;
    }

    /**
     * Retrieves a single question by its unique ID.
     *
     * @param questionId unique ID of the question
     * @return Question object if found; null otherwise
     */
    public Question getQuestionById(int questionId) {
        String sql = "SELECT question_id, exam_id, question_text, question_type, option_a, option_b, option_c, option_d, correct_answer, marks " +
                     "FROM questions WHERE question_id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        if (conn == null) {
            LOGGER.severe("Database connection is unavailable for getQuestionById.");
            return null;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, questionId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToQuestion(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving question ID: " + questionId, e);
        }
        return null;
    }

    /**
     * Counts the total number of questions currently created for an exam.
     *
     * @param examId unique identifier of the exam
     * @return count of questions, or 0 if none or on error
     */
    public int getQuestionCount(int examId) {
        String sql = "SELECT COUNT(*) FROM questions WHERE exam_id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        if (conn == null) {
            LOGGER.severe("Database connection is unavailable for getQuestionCount.");
            return 0;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, examId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting questions for exam ID: " + examId, e);
        }
        return 0;
    }

    /**
     * Helper method to map a ResultSet row to a Question entity.
     *
     * @param rs active ResultSet positioned at the desired record
     * @return populated Question object
     * @throws SQLException on column reading error
     */
    private Question mapResultSetToQuestion(ResultSet rs) throws SQLException {
        Question q = new Question();
        q.setQuestionId(rs.getInt("question_id"));
        q.setExamId(rs.getInt("exam_id"));
        q.setQuestionText(rs.getString("question_text"));
        q.setQuestionType(rs.getString("question_type"));
        q.setOptionA(rs.getString("option_a"));
        q.setOptionB(rs.getString("option_b"));
        q.setOptionC(rs.getString("option_c"));
        q.setOptionD(rs.getString("option_d"));
        q.setCorrectAnswer(rs.getString("correct_answer"));
        q.setMarks(rs.getInt("marks"));
        return q;
    }
}

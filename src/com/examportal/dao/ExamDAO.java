package com.examportal.dao;

import com.examportal.db.DatabaseConnection;
import com.examportal.model.Exam;

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
 * Data Access Object for Exam entity.
 * Provides CRUD operations and status toggling for exams.
 */
public class ExamDAO {

    private static final Logger LOGGER = Logger.getLogger(ExamDAO.class.getName());

    /**
     * Inserts a new exam into the database and returns the generated exam_id.
     *
     * @param exam Exam object containing details to insert
     * @return the generated exam_id, or -1 if the insertion failed
     */
    public int addExam(Exam exam) {
        String sql = "INSERT INTO exams (title, description, duration_minutes, total_marks, passing_marks, is_active, created_by) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        if (conn == null) {
            LOGGER.severe("Database connection is unavailable for addExam.");
            return -1;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, exam.getTitle());
            pstmt.setString(2, exam.getDescription());
            pstmt.setInt(3, exam.getDurationMinutes());
            pstmt.setInt(4, exam.getTotalMarks());
            pstmt.setInt(5, exam.getPassingMarks());
            pstmt.setBoolean(6, exam.isActive());
            if (exam.getCreatedBy() > 0) {
                pstmt.setInt(7, exam.getCreatedBy());
            } else {
                pstmt.setNull(7, java.sql.Types.INTEGER);
            }

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int generatedId = rs.getInt(1);
                        exam.setExamId(generatedId);
                        return generatedId;
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting exam: " + exam.getTitle(), e);
        }
        return -1;
    }

    /**
     * Updates an existing exam in the database.
     *
     * @param exam Exam object containing updated details
     * @return true if updated successfully; false otherwise
     */
    public boolean updateExam(Exam exam) {
        String sql = "UPDATE exams SET title = ?, description = ?, duration_minutes = ?, " +
                     "total_marks = ?, passing_marks = ?, is_active = ? WHERE exam_id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        if (conn == null) {
            LOGGER.severe("Database connection is unavailable for updateExam.");
            return false;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, exam.getTitle());
            pstmt.setString(2, exam.getDescription());
            pstmt.setInt(3, exam.getDurationMinutes());
            pstmt.setInt(4, exam.getTotalMarks());
            pstmt.setInt(5, exam.getPassingMarks());
            pstmt.setBoolean(6, exam.isActive());
            pstmt.setInt(7, exam.getExamId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating exam ID: " + exam.getExamId(), e);
        }
        return false;
    }

    /**
     * Deletes an exam by its ID. Due to CASCADE constraints, questions and results will be cascade deleted.
     *
     * @param examId unique ID of the exam to delete
     * @return true if deleted successfully; false otherwise
     */
    public boolean deleteExam(int examId) {
        String sql = "DELETE FROM exams WHERE exam_id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        if (conn == null) {
            LOGGER.severe("Database connection is unavailable for deleteExam.");
            return false;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, examId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting exam ID: " + examId, e);
        }
        return false;
    }

    /**
     * Retrieves an exam by its ID.
     *
     * @param examId unique ID of the exam
     * @return Exam object if found; null otherwise
     */
    public Exam getExamById(int examId) {
        String sql = "SELECT exam_id, title, description, duration_minutes, total_marks, passing_marks, is_active, created_by, created_at " +
                     "FROM exams WHERE exam_id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        if (conn == null) {
            LOGGER.severe("Database connection is unavailable for getExamById.");
            return null;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, examId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToExam(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving exam with ID: " + examId, e);
        }
        return null;
    }

    /**
     * Retrieves all exams in the database, ordered by creation descending.
     *
     * @return List of all Exam objects
     */
    public List<Exam> getAllExams() {
        List<Exam> exams = new ArrayList<>();
        String sql = "SELECT exam_id, title, description, duration_minutes, total_marks, passing_marks, is_active, created_by, created_at " +
                     "FROM exams ORDER BY exam_id DESC";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        if (conn == null) {
            LOGGER.severe("Database connection is unavailable for getAllExams.");
            return exams;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                exams.add(mapResultSetToExam(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all exams", e);
        }
        return exams;
    }

    /**
     * Retrieves all active exams (is_active = TRUE) for students to take.
     *
     * @return List of active Exam objects
     */
    public List<Exam> getActiveExams() {
        List<Exam> exams = new ArrayList<>();
        String sql = "SELECT exam_id, title, description, duration_minutes, total_marks, passing_marks, is_active, created_by, created_at " +
                     "FROM exams WHERE is_active = TRUE ORDER BY exam_id DESC";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        if (conn == null) {
            LOGGER.severe("Database connection is unavailable for getActiveExams.");
            return exams;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                exams.add(mapResultSetToExam(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving active exams", e);
        }
        return exams;
    }

    /**
     * Toggles the active status of an exam.
     *
     * @param examId unique ID of the exam
     * @param active desired active state
     * @return true if updated successfully; false otherwise
     */
    public boolean toggleExamStatus(int examId, boolean active) {
        String sql = "UPDATE exams SET is_active = ? WHERE exam_id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        if (conn == null) {
            LOGGER.severe("Database connection is unavailable for toggleExamStatus.");
            return false;
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, active);
            pstmt.setInt(2, examId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error toggling status for exam ID: " + examId, e);
        }
        return false;
    }

    /**
     * Helper method to map a ResultSet row to an Exam entity.
     *
     * @param rs active ResultSet positioned at the desired record
     * @return populated Exam object
     * @throws SQLException on column reading error
     */
    private Exam mapResultSetToExam(ResultSet rs) throws SQLException {
        Exam exam = new Exam();
        exam.setExamId(rs.getInt("exam_id"));
        exam.setTitle(rs.getString("title"));
        exam.setDescription(rs.getString("description"));
        exam.setDurationMinutes(rs.getInt("duration_minutes"));
        exam.setTotalMarks(rs.getInt("total_marks"));
        exam.setPassingMarks(rs.getInt("passing_marks"));
        exam.setActive(rs.getBoolean("is_active"));
        exam.setCreatedBy(rs.getInt("created_by"));
        exam.setCreatedAt(rs.getTimestamp("created_at"));
        return exam;
    }
}

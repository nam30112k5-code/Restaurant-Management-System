package dal;

import model.Feedback;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FeedbackDAO extends DBContext {

    private PreparedStatement stm;
    private ResultSet rs;

    // 1. Gửi đánh giá mới
    public void insert(Feedback f) {
        String SQLStatement = "INSERT INTO Feedbacks (content, rating, guestId, appointmentId) VALUES (?, ?, ?, ?)";
        try {
            stm = connection.prepareStatement(SQLStatement);
            stm.setString(1, f.getContent());
            stm.setInt(2, f.getRating());
            stm.setInt(3, f.getGuestId());
            stm.setString(4, f.getAppointmentId());
            stm.executeUpdate();
        } catch (SQLException e) {
        }
    }

    // 2. Kiểm tra xem đơn này đã được đánh giá chưa (tránh spam)
    public boolean hasFeedback(String appointmentId) {
        String SQLStatement = "SELECT COUNT(*) FROM Feedbacks WHERE appointmentId = ?";
        try {
            stm = connection.prepareStatement(SQLStatement);
            stm.setString(1, appointmentId);
            rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
        }
        return false;
    }

    // 3. Lấy danh sách Feedback cho Admin xem
    public List<Feedback> getAll() {
        List<Feedback> list = new ArrayList<>();
        String SQLStatement = "SELECT f.*, g.name, t.tableName "
                + "FROM Feedbacks f "
                + "JOIN Guests g ON f.guestId = g.guestId "
                + "JOIN Appointments a ON f.appointmentId = a.appointmentId "
                + "JOIN Tables t ON a.tableId = t.tableId";
        try {
            stm = connection.prepareStatement(SQLStatement);
            rs = stm.executeQuery();
            while (rs.next()) {
                Feedback f = new Feedback();
                f.setFeedbackId(rs.getInt("feedbackId"));
                f.setContent(rs.getString("content"));
                f.setRating(rs.getInt("rating"));
                f.setGuestName(rs.getString("name"));
                f.setTableName(rs.getString("tableName"));
                list.add(f);
            }
        } catch (SQLException e) {
        }
        return list;
    }
}

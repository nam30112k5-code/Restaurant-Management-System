package dal;

import model.Appointment;
import model.Table;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO extends DBContext {

    private PreparedStatement stm;
    private ResultSet rs;

    // 1. Thêm mới đơn đặt bàn
    public boolean insert(Appointment app) {
        String SQLStatement = "INSERT INTO Appointments (guestId, tableId, startTime, endTime, date, status, createBy) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            stm = connection.prepareStatement(SQLStatement);
            stm.setInt(1, app.getGuestId());
            stm.setInt(2, app.getTableId());
            stm.setTime(3, app.getStartTime());
            stm.setTime(4, app.getEndTime());
            stm.setDate(5, app.getDate());
            stm.setString(6, app.getStatus());
            stm.setString(7, app.getCreateBy());
            int rows = stm.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    // 2. Tìm danh sách Bàn Trống theo Ngày và Giờ
    public List<Table> getAvailableTables(Date date, Time start, Time end) {
        List<Table> list = new ArrayList<>();
        String SQLStatement = "SELECT * FROM [Tables] "
                + "WHERE tableId NOT IN ("
                + "    SELECT tableId FROM [Appointments] "
                + "    WHERE [date] = ? "
                + "    AND status <> 'cancelled' "
                // Ép kiểu tham số truyền vào thành TIME chuẩn của SQL Server
                + "    AND (startTime < CAST(? AS TIME) AND endTime > CAST(? AS TIME)) "
                + ")";
        try {
            stm = connection.prepareStatement(SQLStatement);
            stm.setDate(1, date);
            stm.setTime(2, end);
            stm.setTime(3, start);
            rs = stm.executeQuery();
            while (rs.next()) {
                Table t = new Table(rs.getInt("tableId"), rs.getString("tableName"));
                list.add(t);
            }
        } catch (SQLException e) {
        }
        return list;
    }

    // 3. Lấy lịch sử đặt bàn của một khách (KÈM TRẠNG THÁI RATED)
    public List<Appointment> getHistoryByGuest(int guestId) {
        List<Appointment> list = new ArrayList<>();
        String SQLStatement = "SELECT a.*, t.tableName, "
                + "CASE WHEN f.feedbackId IS NOT NULL THEN 1 ELSE 0 END AS isRated "
                + "FROM Appointments a "
                + "JOIN Tables t ON a.tableId = t.tableId "
                + "LEFT JOIN Feedbacks f ON a.appointmentId = f.appointmentId "
                + "WHERE a.guestId = ? "
                + "ORDER BY a.date DESC, a.startTime DESC";
        try {
            stm = connection.prepareStatement(SQLStatement);
            stm.setInt(1, guestId);
            rs = stm.executeQuery();
            while (rs.next()) {
                Appointment a = new Appointment();
                a.setAppointmentId(rs.getString("appointmentId"));
                a.setGuestId(rs.getInt("guestId"));
                a.setTableId(rs.getInt("tableId"));
                a.setStartTime(rs.getTime("startTime"));
                a.setEndTime(rs.getTime("endTime"));
                a.setDate(rs.getDate("date"));
                a.setStatus(rs.getString("status"));
                a.setTableName(rs.getString("tableName"));
                a.setRated(rs.getInt("isRated") == 1);
                list.add(a);
            }
        } catch (SQLException e) {
        }
        return list;
    }

    // 4. Lấy TẤT CẢ đơn đặt bàn (Dành cho Staff/Admin quản lý)
    public List<Appointment> getAllAppointments() {
        List<Appointment> list = new ArrayList<>();
        String SQLStatement = "SELECT a.*, g.name AS guestName, t.tableName "
                + "FROM Appointments a "
                + "JOIN Guests g ON a.guestId = g.guestId "
                + "JOIN Tables t ON a.tableId = t.tableId "
                + "ORDER BY a.date DESC, a.startTime DESC";
        try {
            stm = connection.prepareStatement(SQLStatement);
            rs = stm.executeQuery();
            while (rs.next()) {
                Appointment a = new Appointment();
                a.setAppointmentId(rs.getString("appointmentId"));
                a.setGuestId(rs.getInt("guestId"));
                a.setGuestName(rs.getString("guestName"));
                a.setTableId(rs.getInt("tableId"));
                a.setTableName(rs.getString("tableName"));
                a.setDate(rs.getDate("date"));
                a.setStartTime(rs.getTime("startTime"));
                a.setEndTime(rs.getTime("endTime"));
                a.setStatus(rs.getString("status"));
                list.add(a);
            }
        } catch (SQLException e) {
        }
        return list;
    }

    // 5. Cập nhật trạng thái (Approve, Cancel, Complete)
    public void updateStatus(String appointmentId, String status) {
        String SQLStatement = "UPDATE Appointments SET status = ? WHERE appointmentId = ?";
        try {
            stm = connection.prepareStatement(SQLStatement);
            stm.setString(1, status);
            stm.setString(2, appointmentId);
            stm.executeUpdate();
        } catch (SQLException e) {
        }
    }

    // 6. Kiểm tra khách có đơn đang chờ xác nhận (Pending) hay không
    public boolean hasPendingBooking(int guestId) {
        String SQLStatement = "SELECT COUNT(*) FROM Appointments WHERE guestId = ? AND status = 'pending'";
        try {
            stm = connection.prepareStatement(SQLStatement);
            stm.setInt(1, guestId);
            rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
        }
        return false;
    }

    // 7. Khách tự hủy đơn (Chỉ update trạng thái thành cancelled)
    public void cancelBooking(String appointmentId) {
        String SQLStatement = "UPDATE Appointments SET status = 'cancelled' WHERE appointmentId = ?";
        try {
            stm = connection.prepareStatement(SQLStatement);
            stm.setString(1, appointmentId);
            stm.executeUpdate();
        } catch (SQLException e) {
        }
    }

    // 8. Lấy danh sách bàn kèm trạng thái (Booked hay chưa) theo khung giờ
    public List<Table> getTablesStatus(Date date, Time viewStart, Time viewEnd) {
        List<Table> list = new ArrayList<>();
        if (connection == null) {
            return list;
        }
        String SQLStatement = "SELECT t.tableId, t.tableName, "
                + "       a.startTime, a.endTime, g.name AS guestName "
                + "FROM Tables t "
                + "LEFT JOIN Appointments a ON t.tableId = a.tableId "
                + "    AND a.date = ? "
                + "    AND a.status IN ('pending', 'confirmed') "
                + "    AND ("
                + "       (a.startTime < a.endTime AND a.startTime < CAST(? AS TIME) AND a.endTime > CAST(? AS TIME)) "
                + "       OR "
                + "       (a.startTime > a.endTime AND ("
                + "           a.startTime < CAST(? AS TIME) OR a.endTime > CAST(? AS TIME) OR CAST(? AS TIME) > CAST(? AS TIME)"
                + "       ))"
                + "    ) "
                + "LEFT JOIN Guests g ON a.guestId = g.guestId";
        try {
            stm = connection.prepareStatement(SQLStatement);
            stm.setDate(1, date);
            stm.setTime(2, viewEnd);
            stm.setTime(3, viewStart);
            stm.setTime(4, viewEnd);
            stm.setTime(5, viewStart);
            stm.setTime(6, viewStart);
            stm.setTime(7, viewEnd);
            rs = stm.executeQuery();
            while (rs.next()) {
                Table t = new Table();
                t.setTableId(rs.getInt("tableId"));
                t.setTableName(rs.getString("tableName"));
                if (rs.getTime("startTime") != null) {
                    t.setBooked(true);
                    t.setCurrentGuestName(rs.getString("guestName"));
                    t.setTimeRange(rs.getTime("startTime") + " - " + rs.getTime("endTime"));
                } else {
                    t.setBooked(false);
                }
                list.add(t);
            }
        } catch (SQLException e) {
        }
        return list;
    }

    // 9. Thêm hàm update thông tin đặt bàn
    public boolean updateBookingInfo(String appointmentId, Date date, Time start, Time end) {
        String SQLStatement = "UPDATE Appointments SET date = ?, startTime = ?, endTime = ? WHERE appointmentId = ?";
        try {
            stm = connection.prepareStatement(SQLStatement);
            stm.setDate(1, date);
            stm.setTime(2, start);
            stm.setTime(3, end);
            stm.setString(4, appointmentId);
            return stm.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }
}

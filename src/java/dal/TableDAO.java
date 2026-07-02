package dal;

import model.Table;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TableDAO extends DBContext {

    private PreparedStatement stm;
    private ResultSet rs;

    // 1. Lấy tất cả bàn
    public List<Table> getAll() {
        List<Table> list = new ArrayList<>();
        String SQLStatement = "SELECT * FROM Tables";
        try {
            stm = connection.prepareStatement(SQLStatement);
            rs = stm.executeQuery();
            while (rs.next()) {
                list.add(new Table(rs.getInt("tableId"), rs.getString("tableName")));
            }
        } catch (SQLException e) {
        }
        return list;
    }

    // 2. Lấy bàn theo ID
    public Table getById(int id) {
        String SQLStatement = "SELECT * FROM Tables WHERE tableId = ?";
        try {
            stm = connection.prepareStatement(SQLStatement);
            stm.setInt(1, id);
            rs = stm.executeQuery();
            if (rs.next()) {
                return new Table(rs.getInt("tableId"), rs.getString("tableName"));
            }
        } catch (SQLException e) {
        }
        return null;
    }

    // 3. Thêm bàn mới
    public void insert(Table t) {
        String SQLStatement = "INSERT INTO Tables (tableName) VALUES (?)";
        try {
            stm = connection.prepareStatement(SQLStatement);
            stm.setString(1, t.getTableName());
            stm.executeUpdate();
        } catch (SQLException e) {
        }
    }

    // 4. Sửa tên bàn
    public void update(Table t) {
        String SQLStatement = "UPDATE Tables SET tableName = ? WHERE tableId = ?";
        try {
            stm = connection.prepareStatement(SQLStatement);
            stm.setString(1, t.getTableName());
            stm.setInt(2, t.getTableId());
            stm.executeUpdate();
        } catch (SQLException e) {
        }
    }

    // 5. Xóa bàn (Nếu bàn đã có Booking thì sẽ lỗi FK, xử lý try-catch ở Controller)
    public void delete(int id) {
        String SQLStatement = "DELETE FROM Tables WHERE tableId = ?";
        try {
            stm = connection.prepareStatement(SQLStatement);
            stm.setInt(1, id);
            stm.executeUpdate();
        } catch (SQLException e) {
        }
    }

    // 6. Lấy danh sách bàn kèm trạng thái THỰC TẾ (Real-time)
    public java.util.List<Table> getAllWithRealTimeStatus() {
        java.util.List<Table> list = new java.util.ArrayList<>();
        String SQLStatement = "SELECT t.tableId, t.tableName, "
                + "       a.startTime, a.endTime, g.name AS guestName "
                + "FROM Tables t "
                + "LEFT JOIN Appointments a ON t.tableId = a.tableId "
                + "    AND a.date = CAST(GETDATE() AS DATE) "
                + "    AND a.status IN ('pending', 'confirmed') "
                + // Chỉ tính đơn đang hoạt động
                "    AND (CAST(GETDATE() AS TIME) BETWEEN a.startTime AND a.endTime) "
                + "LEFT JOIN Guests g ON a.guestId = g.guestId";
        try {
            stm = connection.prepareStatement(SQLStatement);
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
                    t.setCurrentGuestName("-");
                    t.setTimeRange("-");
                }
                list.add(t);
            }
        } catch (java.sql.SQLException e) {
        }
        return list;
    }
}

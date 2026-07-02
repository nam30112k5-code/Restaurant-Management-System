package dal;

import model.Guest;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GuestDAO extends DBContext {

    private PreparedStatement stm;
    private ResultSet rs;

    // 1. Lấy guest theo username (login)
    public Guest getByUsername(String username) {
        String SQLStatement = "SELECT * FROM Guests WHERE username = ? AND isActive = 1";
        try {
            stm = connection.prepareStatement(SQLStatement);
            stm.setString(1, username);
            rs = stm.executeQuery();
            if (rs.next()) {
                Guest g = new Guest();
                g.setGuestId(rs.getInt("guestId"));
                g.setUsername(rs.getString("username"));
                g.setPassword(rs.getString("password"));
                g.setName(rs.getString("name"));
                g.setIdentityNumber(rs.getString("identityNumber"));
                g.setPhoneNumber(rs.getString("phoneNumber"));
                g.setDateOfBirth(rs.getDate("dateOfBirth"));
                g.setGender(rs.getString("gender"));
                g.setIsActive(rs.getBoolean("isActive"));
                return g;
            }
        } catch (SQLException e) {
        }
        return null;
    }

    // 2. Đăng ký khách mới
    public void insert(Guest g) {
        String SQLStatement = "INSERT INTO Guests (username, password, name, identityNumber, phoneNumber, dateOfBirth, gender, isActive) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, 1)";
        try {
            stm = connection.prepareStatement(SQLStatement);
            stm.setString(1, g.getUsername());
            stm.setString(2, g.getPassword());
            stm.setString(3, g.getName());
            stm.setString(4, g.getIdentityNumber());
            stm.setString(5, g.getPhoneNumber());
            stm.setDate(6, g.getDateOfBirth());
            stm.setString(7, g.getGender());
            stm.executeUpdate();
        } catch (SQLException e) {
        }
    }

    // 3. Lấy tất cả khách hàng (Cho Admin)
    public java.util.List<Guest> getAll() {
        java.util.List<Guest> list = new java.util.ArrayList<>();
        String SQLStatement = "SELECT * FROM Guests";
        try {
            stm = connection.prepareStatement(SQLStatement);
            rs = stm.executeQuery();
            while (rs.next()) {
                Guest g = new Guest();
                g.setGuestId(rs.getInt("guestId"));
                g.setUsername(rs.getString("username"));
                g.setName(rs.getString("name"));
                g.setIdentityNumber(rs.getString("identityNumber"));
                g.setPhoneNumber(rs.getString("phoneNumber"));
                g.setDateOfBirth(rs.getDate("dateOfBirth"));
                g.setGender(rs.getString("gender"));
                g.setIsActive(rs.getBoolean("isActive"));
                list.add(g);
            }
        } catch (SQLException e) {
        }
        return list;
    }

    // 4. Đổi trạng thái Active/Inactive (Khóa/Mở khóa)
    public void updateStatus(int guestId, boolean status) {
        String SQLStatement = "UPDATE Guests SET isActive = ? WHERE guestId = ?";
        try {
            stm = connection.prepareStatement(SQLStatement);
            stm.setBoolean(1, status);
            stm.setInt(2, guestId);
            stm.executeUpdate();
        } catch (SQLException e) {
        }
    }

    // 5. Lấy thông tin khách theo ID (Để hiện tên lên form đổi pass)
    public Guest getById(int id) {
        String SQLStatement = "SELECT * FROM Guests WHERE guestId = ?";
        try {
            stm = connection.prepareStatement(SQLStatement);
            stm.setInt(1, id);
            rs = stm.executeQuery();
            if (rs.next()) {
                Guest g = new Guest();
                g.setGuestId(rs.getInt("guestId"));
                g.setUsername(rs.getString("username"));
                g.setName(rs.getString("name"));
                return g;
            }
        } catch (SQLException e) {
        }
        return null;
    }

    // 6. Admin đổi mật khẩu cho khách
    public void updatePassword(int guestId, String newHashPass) {
        String SQLStatement = "UPDATE Guests SET password = ? WHERE guestId = ?";
        try {
            stm = connection.prepareStatement(SQLStatement);
            stm.setString(1, newHashPass);
            stm.setInt(2, guestId);
            stm.executeUpdate();
        } catch (SQLException e) {
        }
    }

    // 7. Xóa tài khoản Guest (Xóa theo thứ tự: Feedback -> Appointment -> Guest)
    public void delete(int guestId) {
        String sqlDeleteFeedback = "DELETE FROM Feedbacks WHERE guestId = ?";
        String sqlDeleteAppointment = "DELETE FROM Appointments WHERE guestId = ?";
        String sqlDeleteGuest = "DELETE FROM Guests WHERE guestId = ?";
        try {
            if (connection != null) {
                connection.setAutoCommit(false);
            }
            // BƯỚC 1: Xóa Feedback trước (Bảng con cấp thấp nhất)
            stm = connection.prepareStatement(sqlDeleteFeedback);
            stm.setInt(1, guestId);
            stm.executeUpdate();
            stm.close();
            // BƯỚC 2: Xóa Lịch sử đặt bàn (Bảng con cấp giữa)
            stm = connection.prepareStatement(sqlDeleteAppointment);
            stm.setInt(1, guestId);
            stm.executeUpdate();
            stm.close();
            // BƯỚC 3: Xóa Khách hàng (Bảng cha)
            stm = connection.prepareStatement(sqlDeleteGuest);
            stm.setInt(1, guestId);
            stm.executeUpdate();
            // Commit
            connection.commit();
            System.out.println("Deleted guest ID: " + guestId);
        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
            }
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                }
                if (stm != null && !stm.isClosed()) {
                    stm.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    // 8. Cập nhật thông tin cá nhân Guest
    public void updateProfile(Guest g) {
        String SQLStatement = "UPDATE Guests SET name=?, identityNumber=?, phoneNumber=?, dateOfBirth=?, gender=? WHERE guestId=?";
        try {
            stm = connection.prepareStatement(SQLStatement);
            stm.setString(1, g.getName());
            stm.setString(2, g.getIdentityNumber());
            stm.setString(3, g.getPhoneNumber());
            stm.setDate(4, g.getDateOfBirth());
            stm.setString(5, g.getGender());
            stm.setInt(6, g.getGuestId());
            stm.executeUpdate();
        } catch (SQLException ex) {
        }
    }

    // 9. Đổi mật khẩu Guest (Khách tự đổi)
    public void changePassword(int id, String newPass) {
        String SQLStatement = "UPDATE Guests SET password=? WHERE guestId=?";
        try {
            stm = connection.prepareStatement(SQLStatement);
            stm.setString(1, newPass);
            stm.setInt(2, id);
            stm.executeUpdate();
        } catch (SQLException ex) {
        }
    }

    // 10. Tạo nhanh khách vãng lai
    public int createWalkInGuest(String name, String phone) {
        Guest exist = getByPhone(phone);
        if (exist != null) {
            return exist.getGuestId();
        }
        String SQLStatement = "INSERT INTO Guests (username, password, name, phoneNumber, isActive, identityNumber, dateOfBirth, gender) "
                + "VALUES (?, ?, ?, ?, 1, ?, '2000-01-01', 'male')";
        try {
            String dummyUser = "G" + phone;
            String dummyPass = "$2a$12$M8Lm19Q5YsADHL96QBiEZOQgyhk40HQ4aOAXMR44men1ApcCINMaa"; // Pass: 123
            stm = connection.prepareStatement(SQLStatement, java.sql.Statement.RETURN_GENERATED_KEYS);
            stm.setString(1, dummyUser);
            stm.setString(2, dummyPass);
            stm.setString(3, name);
            stm.setString(4, phone);
            stm.setString(5, phone);
            int affectedRows = stm.executeUpdate();
            if (affectedRows > 0) {
                rs = stm.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
        }
        return -1;
    }

    // 11. Tìm guest theo SĐT (hàm hỗ trợ)
    public Guest getByPhone(String phone) {
        String SQLStatement = "SELECT * FROM Guests WHERE phoneNumber = ?";
        try {
            stm = connection.prepareStatement(SQLStatement);
            stm.setString(1, phone);
            rs = stm.executeQuery();
            if (rs.next()) {
                Guest g = new Guest();
                g.setGuestId(rs.getInt("guestId"));
                g.setName(rs.getString("name"));
                g.setUsername(rs.getString("username"));
                return g;
            }
        } catch (SQLException e) {
        }
        return null;
    }
}

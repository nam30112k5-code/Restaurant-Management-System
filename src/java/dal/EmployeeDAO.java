package dal;

import model.Employee;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EmployeeDAO extends DBContext {

    private PreparedStatement stm;
    private ResultSet rs;

    // 1. Lấy nhân viên theo username (login) 
    public Employee getByUsername(String username) {
        String SQLStatement = "SELECT * FROM Employees WHERE username = ? AND isActive = 1";
        try {
            stm = connection.prepareStatement(SQLStatement);
            stm.setString(1, username);
            rs = stm.executeQuery();
            if (rs.next()) {
                Employee emp = new Employee();
                emp.setEmployeeId(rs.getInt("employeeId"));
                emp.setUsername(rs.getString("username"));
                emp.setPassword(rs.getString("password"));
                emp.setIdentityNumber(rs.getString("identityNumber"));
                emp.setPhoneNumber(rs.getString("phoneNumber"));
                emp.setDateOfBirth(rs.getDate("dateOfBirth"));
                emp.setGender(rs.getString("gender"));
                emp.setIsActive(rs.getBoolean("isActive"));
                emp.setRoleId(rs.getInt("roleId"));
                return emp;
            }
        } catch (SQLException e) {
        }
        return null;
    }

    // 2. Lấy tất cả nhân viên
    public java.util.List<Employee> getAll() {
        java.util.List<Employee> list = new java.util.ArrayList<>();
        String SQLStatement = "SELECT * FROM Employees";
        try {
            stm = connection.prepareStatement(SQLStatement);
            rs = stm.executeQuery();
            while (rs.next()) {
                Employee emp = new Employee();
                emp.setEmployeeId(rs.getInt("employeeId"));
                emp.setUsername(rs.getString("username"));
                emp.setIdentityNumber(rs.getString("identityNumber"));
                emp.setPhoneNumber(rs.getString("phoneNumber"));
                emp.setDateOfBirth(rs.getDate("dateOfBirth"));
                emp.setGender(rs.getString("gender"));
                emp.setIsActive(rs.getBoolean("isActive"));
                emp.setRoleId(rs.getInt("roleId"));
                list.add(emp);
            }
        } catch (SQLException e) {
        }
        return list;
    }

    // 3. Lấy nhân viên theo ID (Để hiển thị lên form sửa)
    public Employee getById(int id) {
        String SQLStatement = "SELECT * FROM Employees WHERE employeeId = ?";
        try {
            stm = connection.prepareStatement(SQLStatement);
            stm.setInt(1, id);
            rs = stm.executeQuery();
            if (rs.next()) {
                Employee emp = new Employee();
                emp.setEmployeeId(rs.getInt("employeeId"));
                emp.setUsername(rs.getString("username"));
                emp.setIdentityNumber(rs.getString("identityNumber"));
                emp.setPhoneNumber(rs.getString("phoneNumber"));
                emp.setDateOfBirth(rs.getDate("dateOfBirth"));
                emp.setGender(rs.getString("gender"));
                emp.setIsActive(rs.getBoolean("isActive"));
                emp.setRoleId(rs.getInt("roleId"));
                return emp;
            }
        } catch (SQLException e) {
        }
        return null;
    }

    // 4. Thêm mới nhân viên
    public void insert(Employee e) {
        String SQLStatement = "INSERT INTO Employees (username, password, identityNumber, phoneNumber, dateOfBirth, gender, roleId, isActive) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            stm = connection.prepareStatement(SQLStatement);
            stm.setString(1, e.getUsername());
            stm.setString(2, e.getPassword());
            stm.setString(3, e.getIdentityNumber());
            stm.setString(4, e.getPhoneNumber());
            stm.setDate(5, e.getDateOfBirth());
            stm.setString(6, e.getGender());
            stm.setInt(7, e.getRoleId());
            stm.setBoolean(8, e.isIsActive());
            stm.executeUpdate();
        } catch (SQLException ex) {
        }
    }

    // 5. Cập nhật nhân viên
    public void update(Employee e) {
        String SQLStatement = "UPDATE Employees SET identityNumber=?, phoneNumber=?, dateOfBirth=?, gender=?, roleId=?, isActive=? "
                + "WHERE employeeId=?";
        try {
            stm = connection.prepareStatement(SQLStatement);
            stm.setString(1, e.getIdentityNumber());
            stm.setString(2, e.getPhoneNumber());
            stm.setDate(3, e.getDateOfBirth());
            stm.setString(4, e.getGender());
            stm.setInt(5, e.getRoleId());
            stm.setBoolean(6, e.isIsActive());
            stm.setInt(7, e.getEmployeeId());
            stm.executeUpdate();
        } catch (SQLException ex) {
        }
    }

        // 6. Xóa nhân viên (Xử lý khóa ngoại trước khi xóa)
    public void delete(int id) {
        String sqlGetUsername = "SELECT username FROM Employees WHERE employeeId = ?";
        String sqlUpdateAppointments = "UPDATE Appointments SET createBy = NULL WHERE createBy = ?";
        String sqlDeleteEmployee = "DELETE FROM Employees WHERE employeeId = ?";

        try {
            if (connection != null) connection.setAutoCommit(false);
            String username = null;
            stm = connection.prepareStatement(sqlGetUsername);
            stm.setInt(1, id);
            rs = stm.executeQuery();
            if (rs.next()) {
                username = rs.getString("username");
            }
            if (rs != null) rs.close();
            if (stm != null) stm.close();
            if (username != null) {
                stm = connection.prepareStatement(sqlUpdateAppointments);
                stm.setString(1, username);
                stm.executeUpdate();
                stm.close();
                stm = connection.prepareStatement(sqlDeleteEmployee);
                stm.setInt(1, id);
                stm.executeUpdate();
                connection.commit();
            }

        } catch (SQLException e) {
            try {
                if (connection != null) connection.rollback(); // Hoàn tác nếu lỗi
            } catch (SQLException ex) {
            }
        } finally {
            try {
                if (connection != null) connection.setAutoCommit(true); 
                if (stm != null && !stm.isClosed()) stm.close();
            } catch (SQLException e) {
            }
        }
    }

    // 7. Cập nhật thông tin cá nhân (Không cho sửa Username, Role)
    public void updateProfile(Employee e) {
        String SQLStatement = "UPDATE Employees SET identityNumber=?, phoneNumber=?, dateOfBirth=?, gender=? WHERE employeeId=?";
        try {
            stm = connection.prepareStatement(SQLStatement);
            stm.setString(1, e.getIdentityNumber());
            stm.setString(2, e.getPhoneNumber());
            stm.setDate(3, e.getDateOfBirth());
            stm.setString(4, e.getGender());
            stm.setInt(5, e.getEmployeeId());
            stm.executeUpdate();
        } catch (SQLException ex) {
        }
    }

    // 8. Đổi mật khẩu
    public void changePassword(int id, String newPass) {
        String SQLStatement = "UPDATE Employees SET password=? WHERE employeeId=?";
        try {
            stm = connection.prepareStatement(SQLStatement);
            stm.setString(1, newPass);
            stm.setInt(2, id);
            stm.executeUpdate();
        } catch (SQLException ex) {
        }
    }
}

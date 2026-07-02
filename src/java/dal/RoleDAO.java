package dal;

import java.util.ArrayList;
import java.util.List;
import model.Role;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RoleDAO extends DBContext {

    private PreparedStatement stm;
    private ResultSet rs;

    // 1. Lấy tất cả role
    public List<Role> getAll() {
        List<Role> list = new ArrayList<>();
        String SQLStatement = "SELECT * FROM Roles";
        try {
            stm = connection.prepareStatement(SQLStatement);
            rs = stm.executeQuery();
            while (rs.next()) {
                list.add(new Role(rs.getInt("roleId"), rs.getString("roleName")));
            }
        } catch (Exception e) {
        }
        return list;
    }
}

package util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {

    // Hàm kiểm tra mật khẩu
    public static boolean checkPassword(String rawPassword, String hashedPassword) {
        if (hashedPassword == null || rawPassword == null) {
            return false;
        }
        return BCrypt.checkpw(rawPassword, hashedPassword);
    }

    // Hàm mã hóa mật khẩu để lưu mới
    public static String hashPassword(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt(12));
    }
}

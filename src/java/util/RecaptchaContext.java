package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RecaptchaContext {

    protected String secretKey;
    protected String verifyUrl;

    //Hàm đọc cấu hình reCAPTCHA từ file Recaptcha.properties
    public RecaptchaContext() {
        try {
            Properties properties = new Properties();
            InputStream inputStream = getClass()
                    .getClassLoader()
                    .getResourceAsStream("../Recaptcha.properties");
            properties.load(inputStream);
            secretKey = properties.getProperty("secretKey");
            verifyUrl = properties.getProperty("verifyUrl");
        } catch (IOException ex) {
            Logger.getLogger(RecaptchaContext.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }
}

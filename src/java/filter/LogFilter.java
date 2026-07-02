/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Filter.java to edit this template
 */
package filter;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import model.Employee;
import model.Guest;

/**
 *
 * @author Nguyen Dang Hung
 */
public class LogFilter implements Filter {

    private static final boolean debug = true;
    private FilterConfig filterConfig = null;

    public LogFilter() {
    }

    private void doBeforeProcessing(ServletRequest request, ServletResponse response)
            throws IOException, ServletException {
        if (debug) {
            log("LogFilter:DoBeforeProcessing");
        }
    }

    private void doAfterProcessing(ServletRequest request, ServletResponse response)
            throws IOException, ServletException {
        if (debug) {
            log("LogFilter:DoAfterProcessing");
        }
    }

    private String logFilePath;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        // 1. Xác định vị trí lưu file log
        String contextPath = filterConfig.getServletContext().getRealPath("/");
        File logDir = new File(contextPath + "WEB-INF" + File.separator + "logs");
        // Nếu thư mục chưa có thì tạo mới
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
        this.logFilePath = logDir.getAbsolutePath() + File.separator + "system_log.txt";
        logToFile("[SYSTEM] --- Server Started / LogFilter Initialized ---");
        System.out.println(">> Log file created at: " + this.logFilePath);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;

        // 2. Lấy thông tin request
        String url = req.getRequestURI();
        String method = req.getMethod();
        String ipAddress = req.getRemoteAddr();
        // Bỏ qua tài nguyên tĩnh để file log không bị spam
        if (url.contains("/assets/") || url.endsWith(".css") || url.endsWith(".js") || url.endsWith(".png") || url.endsWith(".jpg")) {
            chain.doFilter(request, response);
            return;
        }

        // 3. Xác định người dùng
        HttpSession session = req.getSession(false);
        String userInfo = "Visitor";
        if (session != null && session.getAttribute("account") != null) {
            Object account = session.getAttribute("account");
            String role = (String) session.getAttribute("role");
            if ("employee".equals(role)) {
                Employee emp = (Employee) account;
                String roleName = (emp.getRoleId() == 1) ? "ADMIN" : "STAFF";
                userInfo = roleName + "-" + emp.getUsername();
            } else if ("guest".equals(role)) {
                Guest g = (Guest) account;
                userInfo = "GUEST-" + g.getUsername();
            }
        }

        // 4. Tạo nội dung log
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(new Date());
        String logMessage = String.format("[%s] IP:%-15s | User:%-20s | %s %s",
                time, ipAddress, userInfo, method, url);

        // 5. Ghi vào file (và vẫn in ra console để dễ debug)
        logToFile(logMessage);
        chain.doFilter(request, response);
    }

    // Hàm hỗ trợ ghi file (synchronized để tránh xung đột khi nhiều người truy cập cùng lúc)
    private synchronized void logToFile(String message) {
        // Tham số 'true' trong FileWriter nghĩa là ghi nối tiếp (append), không ghi đè
        try (PrintWriter writer = new PrintWriter(new FileWriter(logFilePath, true))) {
            writer.println(message);
        } catch (IOException e) {
            System.err.println("Không thể ghi log ra file: " + e.getMessage());
        }
    }

    @Override
    public void destroy() {
    }

    public FilterConfig getFilterConfig() {
        return (this.filterConfig);
    }

    public void setFilterConfig(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    @Override
    public String toString() {
        if (filterConfig == null) {
            return ("LogFilter()");
        }
        StringBuffer sb = new StringBuffer("LogFilter(");
        sb.append(filterConfig);
        sb.append(")");
        return (sb.toString());
    }

    private void sendProcessingError(Throwable t, ServletResponse response) {
        String stackTrace = getStackTrace(t);

        if (stackTrace != null && !stackTrace.equals("")) {
            try {
                response.setContentType("text/html");
                PrintStream ps = new PrintStream(response.getOutputStream());
                PrintWriter pw = new PrintWriter(ps);
                pw.print("<html>\n<head>\n<title>Error</title>\n</head>\n<body>\n"); //NOI18N

                // PENDING! Localize this for next official release
                pw.print("<h1>The resource did not process correctly</h1>\n<pre>\n");
                pw.print(stackTrace);
                pw.print("</pre></body>\n</html>"); //NOI18N
                pw.close();
                ps.close();
                response.getOutputStream().close();
            } catch (Exception ex) {
            }
        } else {
            try {
                PrintStream ps = new PrintStream(response.getOutputStream());
                t.printStackTrace(ps);
                ps.close();
                response.getOutputStream().close();
            } catch (Exception ex) {
            }
        }
    }

    public static String getStackTrace(Throwable t) {
        String stackTrace = null;
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            pw.close();
            sw.close();
            stackTrace = sw.getBuffer().toString();
        } catch (Exception ex) {
        }
        return stackTrace;
    }

    public void log(String msg) {
        filterConfig.getServletContext().log(msg);
    }

}

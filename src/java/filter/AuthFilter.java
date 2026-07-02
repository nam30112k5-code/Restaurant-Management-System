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
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author Nguyen Dang Hung
 */
public class AuthFilter implements Filter {

    private static final boolean debug = true;
    private FilterConfig filterConfig = null;

    public AuthFilter() {
    }

    private void doBeforeProcessing(ServletRequest request, ServletResponse response)
            throws IOException, ServletException {
        if (debug) {
            log("AuthFilter:DoBeforeProcessing");
        }
    }

    private void doAfterProcessing(ServletRequest request, ServletResponse response)
            throws IOException, ServletException {
        if (debug) {
            log("AuthFilter:DoAfterProcessing");
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String path = req.getServletPath();

        // 1. CHO PHÉP TRUY CẬP TỰ DO (Public Resources)
        if (path.endsWith(".css") || path.endsWith(".js") || path.endsWith(".png") || path.endsWith(".jpg")
                || path.startsWith("/assets/")
                || path.equals("/Login")
                || path.equals("/Register")
                || path.equals("/Home")
                || path.equals("/index.jsp")) {
            chain.doFilter(request, response);
            return;
        }

        // 2. KIỂM TRA ĐĂNG NHẬP
        HttpSession session = req.getSession(false);
        boolean isLoggedIn = (session != null && session.getAttribute("account") != null);
        if (!isLoggedIn) {
            res.sendRedirect(req.getContextPath() + "/Home");
            return;
        }

        // 3. LẤY ROLE ĐỂ PHÂN QUYỀN
        String role = (String) session.getAttribute("role"); // "employee" hoặc "guest"
        Integer roleId = (Integer) session.getAttribute("roleId"); // 1: Admin, 2: Staff (Chỉ có khi là employee)

        // KHU VỰC 1: ADMIN (Quản lý nhân viên, bàn, khách hàng)
        if (path.startsWith("/Employee") || path.startsWith("/Table") || path.startsWith("/Guest") || path.startsWith("/Admin")) {
            if ("employee".equals(role) && roleId == 1) {
                // Là Admin -> Cho qua
                chain.doFilter(request, response);
            } else {
                // Không phải Admin -> Chặn (Báo lỗi 403 Forbidden)
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền Admin để truy cập trang này.");
            }
            return;
        }

        // KHU VỰC 2: STAFF / RECEPTIONIST (Lễ tân đặt bàn tại quán)
        if (path.startsWith("/Staff")) {
            if ("employee".equals(role) && roleId == 2) {
                // Là Staff -> Cho qua
                chain.doFilter(request, response);
            } else {
                // Không phải Staff -> Chặn (Báo lỗi 403 Forbidden)
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Trang này chỉ dành cho Lễ tân (Staff).");
            }
            return;
        }

        // KHU VỰC 3: GUEST (Khách đặt bàn Online)
        if ((path.startsWith("/Booking") || path.startsWith("/History") || path.startsWith("/Feedback")) && !path.startsWith("/Staff")) {
            if ("guest".equals(role)) {
                // Là Guest -> Cho qua
                chain.doFilter(request, response);
            } else {
                // Nếu là Nhân viên mà lỡ bấm vào link của khách -> Điều hướng về đúng Dashboard của họ
                if ("employee".equals(role)) {
                    if (roleId == 1) {
                        res.sendRedirect(req.getContextPath() + "/Employee"); // Admin về trang Admin
                    } else {
                        res.sendRedirect(req.getContextPath() + "/Staff/Booking"); // Staff về trang Staff
                    }
                }
            }
            return;
        }
        // KHU VỰC CHUNG (Ai đăng nhập rồi cũng vào được)
        chain.doFilter(request, response);
    }

    public FilterConfig getFilterConfig() {
        return (this.filterConfig);
    }

    public void setFilterConfig(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
        if (filterConfig != null) {
            if (debug) {
                log("AuthFilter:Initializing filter");
            }
        }
    }

    @Override
    public String toString() {
        if (filterConfig == null) {
            return ("AuthFilter()");
        }
        StringBuffer sb = new StringBuffer("AuthFilter(");
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

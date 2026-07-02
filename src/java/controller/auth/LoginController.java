/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.auth;

import dal.EmployeeDAO;
import dal.GuestDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Employee;
import model.Guest;
import org.mindrot.jbcrypt.BCrypt;
import util.VerifyRecaptcha;

/**
 *
 * @author Nguyen Dang Hung
 */
public class LoginController extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet LoginController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet LoginController at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //processRequest(request, response);
        HttpSession session = request.getSession();
        Object account = session.getAttribute("account");
        if (account != null) {
            String role = (String) session.getAttribute("role");
            if ("guest".equals(role)) {
                response.sendRedirect("Home");
            } else if ("employee".equals(role)) {
                int roleId = (int) session.getAttribute("roleId");
                if (roleId == 1) {
                    response.sendRedirect("Employee");
                } else {
                    response.sendRedirect("Staff");
                }
            }
            return;
        }
        request.getRequestDispatcher("/views/auth/Login.jsp").forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String u = request.getParameter("username");
        String p = request.getParameter("password");
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        VerifyRecaptcha vr = new VerifyRecaptcha();
        boolean isCaptchaValid = vr.verify(gRecaptchaResponse);
        if (!isCaptchaValid) {
            request.setAttribute("error", "Vui lòng xác thực: Bạn không phải là người máy!");
            request.setAttribute("username", request.getParameter("username"));
            request.getRequestDispatcher("/views/auth/Login.jsp")
                    .forward(request, response);
            return;
        }
        if (u == null || p == null || u.trim().isEmpty() || p.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập đầy đủ thông tin!");
            request.getRequestDispatcher("/views/auth/Login.jsp").forward(request, response);
            return;
        }
        EmployeeDAO empDao = new EmployeeDAO();
        GuestDAO guestDao = new GuestDAO();
        HttpSession session = request.getSession();
        Employee emp = empDao.getByUsername(u);
        if (emp != null) {
            if (BCrypt.checkpw(p, emp.getPassword())) {
                session.setAttribute("account", emp);
                session.setAttribute("role", "employee");
                session.setAttribute("roleId", emp.getRoleId());
                if (emp.getRoleId() == 1) {
                    response.sendRedirect("Employee");
                } else {
                    response.sendRedirect("Staff/Booking");
                }
                return;
            }
        }
        Guest guest = guestDao.getByUsername(u);
        if (guest != null) {
            if (BCrypt.checkpw(p, guest.getPassword())) {
                session.setAttribute("account", guest);
                session.setAttribute("role", "guest");
                response.sendRedirect("Home");
                return;
            }
        }
        request.setAttribute("error", "Sai tên đăng nhập hoặc mật khẩu!");
        request.setAttribute("username", u);
        request.getRequestDispatcher("/views/auth/Login.jsp").forward(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}

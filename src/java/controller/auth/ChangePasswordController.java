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

/**
 *
 * @author Nguyen Dang Hung
 */
public class ChangePasswordController extends HttpServlet {

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
            out.println("<title>Servlet ChangePasswordController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ChangePasswordController at " + request.getContextPath() + "</h1>");
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
        if (request.getSession().getAttribute("account") == null) {
            response.sendRedirect("Login");
            return;
        }
        request.getRequestDispatcher("/views/common/ChangePassword.jsp").forward(request, response);
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
        //processRequest(request, response);
        HttpSession session = request.getSession();
        Object account = session.getAttribute("account");
        String role = (String) session.getAttribute("role");
        String oldPass = request.getParameter("oldPass");
        String newPass = request.getParameter("newPass");
        String confirmPass = request.getParameter("confirmPass");
        if (!newPass.equals(confirmPass)) {
            request.setAttribute("error", "Mật khẩu xác nhận không khớp!");
            request.getRequestDispatcher("/views/common/ChangePassword.jsp").forward(request, response);
            return;
        }
        String currentHash = "";
        int id = 0;
        if ("guest".equals(role)) {
            Guest g = (Guest) account;
            currentHash = g.getPassword();
            id = g.getGuestId();
        } else {
            Employee e = (Employee) account;
            currentHash = e.getPassword();
            id = e.getEmployeeId();
        }
        if (!BCrypt.checkpw(oldPass, currentHash)) {
            request.setAttribute("error", "Mật khẩu cũ không chính xác!");
            request.getRequestDispatcher("/views/common/ChangePassword.jsp").forward(request, response);
            return;
        }
        String newHash = BCrypt.hashpw(newPass, BCrypt.gensalt(12));
        if ("guest".equals(role)) {
            GuestDAO dao = new GuestDAO();
            dao.changePassword(id, newHash);
            // Cập nhật pass trong session
            ((Guest) account).setPassword(newHash);
        } else {
            EmployeeDAO dao = new EmployeeDAO();
            dao.changePassword(id, newHash);
            ((Employee) account).setPassword(newHash);
        }
        request.setAttribute("success", "Đổi mật khẩu thành công!");
        request.getRequestDispatcher("/views/common/ChangePassword.jsp").forward(request, response);
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

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
import java.sql.Date;
import model.Guest;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author Nguyen Dang Hung
 */
public class RegisterController extends HttpServlet {

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
            out.println("<title>Servlet RegisterController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet RegisterController at " + request.getContextPath() + "</h1>");
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
        request.getRequestDispatcher("/views/auth/Register.jsp").forward(request, response);
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
        try {
            request.setCharacterEncoding("UTF-8");
            String user = request.getParameter("username");
            String pass = request.getParameter("password");
            String rePass = request.getParameter("repassword");
            String name = request.getParameter("name");
            String identity = request.getParameter("identityNumber");
            String phone = request.getParameter("phoneNumber");
            String dobRaw = request.getParameter("dateOfBirth");
            String gender = request.getParameter("gender");
            if (!pass.equals(rePass)) {
                request.setAttribute("error", "Mật khẩu xác nhận không khớp!");
                request.getRequestDispatcher("/views/auth/Register.jsp").forward(request, response);
                return;
            }
            GuestDAO gDao = new GuestDAO();
            EmployeeDAO eDao = new EmployeeDAO();
            if (gDao.getByUsername(user) != null) {
                request.setAttribute("error", "Tên đăng nhập đã tồn tại (Khách hàng)!");
                request.getRequestDispatcher("/views/auth/Register.jsp").forward(request, response);
                return;
            }
            if (eDao.getByUsername(user) != null) {
                request.setAttribute("error", "Tên đăng nhập đã tồn tại (Nhân viên)!");
                request.getRequestDispatcher("/views/auth/Register.jsp").forward(request, response);
                return;
            }
            Date sqlDate = null;
            if (dobRaw != null && !dobRaw.isEmpty()) {
                sqlDate = Date.valueOf(dobRaw);
            } else {
                request.setAttribute("error", "Vui lòng chọn ngày sinh!");
                request.getRequestDispatcher("/views/auth/Register.jsp").forward(request, response);
                return;
            }
            String hashPass = BCrypt.hashpw(pass, BCrypt.gensalt(12));
            Guest g = new Guest();
            g.setUsername(user);
            g.setPassword(hashPass);
            g.setName(name);
            g.setIdentityNumber(identity);
            g.setPhoneNumber(phone);
            g.setDateOfBirth(sqlDate);
            g.setGender(gender);
            gDao.insert(g);
            request.setAttribute("success", "Đăng ký thành công! Vui lòng đăng nhập.");
            request.getRequestDispatcher("/views/auth/Login.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Lỗi hệ thống: " + e.getMessage());
            request.getRequestDispatcher("/views/auth/Register.jsp").forward(request, response);
        }
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

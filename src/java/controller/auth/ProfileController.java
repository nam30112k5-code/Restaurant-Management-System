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
import java.sql.Date;
import model.Employee;
import model.Guest;

/**
 *
 * @author Nguyen Dang Hung
 */
public class ProfileController extends HttpServlet {

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
            out.println("<title>Servlet ProfileController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ProfileController at " + request.getContextPath() + "</h1>");
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
        if (session.getAttribute("account") == null) {
            response.sendRedirect("Login");
            return;
        }
        request.getRequestDispatcher("/views/common/Profile.jsp").forward(request, response);
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
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        Object account = session.getAttribute("account");
        String role = (String) session.getAttribute("role");
        if (account == null) {
            response.sendRedirect("Login");
            return;
        }
        try {
            String phone = request.getParameter("phoneNumber");
            String identity = request.getParameter("identityNumber");
            Date dob = Date.valueOf(request.getParameter("dateOfBirth"));
            String gender = request.getParameter("gender");
            if ("guest".equals(role)) {
                Guest g = (Guest) account;
                String name = request.getParameter("name");
                g.setName(name);
                g.setPhoneNumber(phone);
                g.setIdentityNumber(identity);
                g.setDateOfBirth(dob);
                g.setGender(gender);
                GuestDAO dao = new GuestDAO();
                dao.updateProfile(g);
                session.setAttribute("account", g);
                request.setAttribute("success", "Cập nhật hồ sơ thành công!");

            } else {
                Employee e = (Employee) account;
                e.setPhoneNumber(phone);
                e.setIdentityNumber(identity);
                e.setDateOfBirth(dob);
                e.setGender(gender);
                EmployeeDAO dao = new EmployeeDAO();
                dao.updateProfile(e);
                session.setAttribute("account", e);
                request.setAttribute("success", "Cập nhật hồ sơ thành công!");
            }
        } catch (Exception ex) {
            request.setAttribute("error", "Lỗi dữ liệu: " + ex.getMessage());
        }
        request.getRequestDispatcher("/views/common/Profile.jsp").forward(request, response);
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

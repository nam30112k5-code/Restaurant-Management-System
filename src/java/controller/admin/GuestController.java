/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.admin;

import dal.GuestDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.Date;
import java.util.List;
import model.Guest;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author Nguyen Dang Hung
 */
public class GuestController extends HttpServlet {

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
            out.println("<title>Servlet GuestController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet GuestController at " + request.getContextPath() + "</h1>");
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
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }
        GuestDAO dao = new GuestDAO();
        switch (action) {
            case "create":
                request.getRequestDispatcher("/views/admin/guest/CreateGuest.jsp").forward(request, response);
                break;
            case "block":
                int idBlock = Integer.parseInt(request.getParameter("id"));
                dao.updateStatus(idBlock, false);
                response.sendRedirect("Guest");
                break;
            case "active":
                int idActive = Integer.parseInt(request.getParameter("id"));
                dao.updateStatus(idActive, true);
                response.sendRedirect("Guest");
                break;
            case "delete":
                int idDel = Integer.parseInt(request.getParameter("id"));
                dao.delete(idDel);
                response.sendRedirect("Guest");
                break;
            case "reset_pass":
                int idReset = Integer.parseInt(request.getParameter("id"));
                Guest g = dao.getById(idReset);
                request.setAttribute("guest", g);
                request.getRequestDispatcher("/views/admin/guest/ResetPassword.jsp").forward(request, response);
                break;
            case "list":
            default:
                List<Guest> list = dao.getAll();
                request.setAttribute("guests", list);
                request.getRequestDispatcher("/views/admin/guest/ListGuest.jsp").forward(request, response);
                break;
        }
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
        String action = request.getParameter("action");
        GuestDAO dao = new GuestDAO();
        if ("store".equals(action)) {
            try {
                String user = request.getParameter("username");
                String pass = request.getParameter("password");
                String name = request.getParameter("name");
                String phone = request.getParameter("phoneNumber");
                String identity = request.getParameter("identityNumber");
                String gender = request.getParameter("gender");
                Date dob = Date.valueOf(request.getParameter("dateOfBirth"));
                if (dao.getByUsername(user) != null) {
                    request.setAttribute("error", "Username '" + user + "' đã tồn tại!");
                    request.setAttribute("guest", new Guest(0, user, "", name, identity, phone, dob, gender, true));
                    request.getRequestDispatcher("/views/admin/guest/CreateGuest.jsp").forward(request, response);
                    return;
                }
                String hashPass = BCrypt.hashpw(pass, BCrypt.gensalt(12));
                Guest g = new Guest();
                g.setUsername(user);
                g.setPassword(hashPass);
                g.setName(name);
                g.setPhoneNumber(phone);
                g.setIdentityNumber(identity);
                g.setDateOfBirth(dob);
                g.setGender(gender);
                g.setIsActive(true);
                dao.insert(g);
                response.sendRedirect("Guest?message=CreateSuccess");
            } catch (Exception e) {
                request.setAttribute("error", "Lỗi: " + e.getMessage());
                request.getRequestDispatcher("/views/admin/guest/CreateGuest.jsp").forward(request, response);
            }
            return;
        }

        if ("update_pass".equals(action)) {
            try {
                int id = Integer.parseInt(request.getParameter("guestId"));
                String newPass = request.getParameter("newPassword");
                String hash = BCrypt.hashpw(newPass, BCrypt.gensalt(12));
                dao.updatePassword(id, hash);
                response.sendRedirect("Guest");
            } catch (Exception e) {
                response.sendRedirect("Guest");
            }
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

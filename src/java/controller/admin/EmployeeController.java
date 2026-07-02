/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.admin;

import dal.EmployeeDAO;
import dal.GuestDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.Date;
import java.util.List;
import model.Employee;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author Nguyen Dang Hung
 */
public class EmployeeController extends HttpServlet {

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
            out.println("<title>Servlet EmployeeController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet EmployeeController at " + request.getContextPath() + "</h1>");
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
        switch (action) {
            case "create":
                request.getRequestDispatcher("/views/admin/employee/CreateEmployee.jsp").forward(request, response);
                break;
            case "edit":
                showEditForm(request, response);
                break;
            case "delete":
                deleteEmployee(request, response);
                break;
            case "list":
            default:
                listEmployees(request, response);
                break;
        }
    }

    private void listEmployees(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        EmployeeDAO dao = new EmployeeDAO();
        List<Employee> list = dao.getAll();
        request.setAttribute("employees", list);
        request.getRequestDispatcher("/views/admin/employee/ListEmployee.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        EmployeeDAO dao = new EmployeeDAO();
        Employee e = dao.getById(id);
        request.setAttribute("employee", e);
        request.getRequestDispatcher("/views/admin/employee/EditEmployee.jsp").forward(request, response);
    }

    private void deleteEmployee(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        EmployeeDAO dao = new EmployeeDAO();
        dao.delete(id);
        response.sendRedirect("Employee?action=list");
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
        if ("store".equals(action)) {
            storeEmployee(request, response);
        } else if ("update".equals(action)) {
            updateEmployee(request, response);
        }
    }

    // HÀM TẠO MỚI (STORE)
    private void storeEmployee(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            request.setCharacterEncoding("UTF-8");
            String username = request.getParameter("username");
            String identity = request.getParameter("identityNumber");
            String phone = request.getParameter("phoneNumber");
            String rawPass = request.getParameter("password");
            EmployeeDAO eDao = new EmployeeDAO();
            GuestDAO gDao = new GuestDAO(); // Thêm dòng này
            String error = null;
            if (eDao.getByUsername(username) != null) {
                error = "Username đã tồn tại (là Nhân viên)!";
            } else if (gDao.getByUsername(username) != null) {
                error = "Username đã tồn tại (là Khách hàng)!";
            }
            if (username.length() > 10) {
                error = "Username quá dài! Tối đa 10 ký tự.";
            } else if (identity != null && identity.length() > 12) {
                error = "CCCD quá dài! Tối đa 12 ký tự.";
            } else if (phone != null && phone.length() > 10) {
                error = "Số điện thoại quá dài! Tối đa 10 ký tự.";
            } else {
                EmployeeDAO dao = new EmployeeDAO();
                if (dao.getByUsername(username) != null) {
                    error = "Username '" + username + "' đã tồn tại!";
                }
            }
            if (error != null) {
                request.setAttribute("error", error);
                Employee backup = new Employee();
                backup.setUsername(username);
                backup.setIdentityNumber(identity);
                backup.setPhoneNumber(phone);
                // Các trường khác backup tương tự nếu cần
                request.setAttribute("employee", backup);
                request.getRequestDispatcher("/views/admin/employee/CreateEmployee.jsp").forward(request, response);
                return;
            }
            Employee e = new Employee();
            e.setUsername(username);
            e.setPassword(BCrypt.hashpw(rawPass, BCrypt.gensalt(12)));
            e.setIdentityNumber(identity);
            e.setPhoneNumber(phone);
            String dobRaw = request.getParameter("dateOfBirth");
            if (dobRaw != null && !dobRaw.isEmpty()) {
                e.setDateOfBirth(Date.valueOf(dobRaw));
            }
            e.setGender(request.getParameter("gender"));
            e.setRoleId(Integer.parseInt(request.getParameter("roleId")));
            e.setIsActive(request.getParameter("isActive") != null);
            EmployeeDAO dao = new EmployeeDAO();
            dao.insert(e);
            response.sendRedirect("Employee?action=list");
        } catch (Exception e) {
            request.setAttribute("error", "Lỗi dữ liệu: " + e.getMessage());
            request.getRequestDispatcher("/views/admin/employee/CreateEmployee.jsp").forward(request, response);
        }
    }

    // HÀM CẬP NHẬT (UPDATE)
    private void updateEmployee(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            request.setCharacterEncoding("UTF-8");
            int id = Integer.parseInt(request.getParameter("employeeId"));
            String identity = request.getParameter("identityNumber");
            String phone = request.getParameter("phoneNumber");
            String error = null;
            if (identity != null && identity.length() > 12) {
                error = "CCCD quá dài! Tối đa 12 ký tự.";
            } else if (phone != null && phone.length() > 10) {
                error = "Số điện thoại quá dài! Tối đa 10 ký tự.";
            }
            if (error != null) {
                request.setAttribute("error", error);
                Employee backup = new Employee();
                backup.setEmployeeId(id);
                EmployeeDAO dao = new EmployeeDAO();
                Employee oldData = dao.getById(id);
                backup.setUsername(oldData.getUsername());
                backup.setIdentityNumber(identity);
                backup.setPhoneNumber(phone);
                String dobRaw = request.getParameter("dateOfBirth");
                if (dobRaw != null && !dobRaw.isEmpty()) {
                    backup.setDateOfBirth(Date.valueOf(dobRaw));
                }
                backup.setGender(request.getParameter("gender"));
                backup.setRoleId(Integer.parseInt(request.getParameter("roleId")));
                backup.setIsActive(request.getParameter("isActive") != null);
                request.setAttribute("employee", backup);
                request.getRequestDispatcher("/views/admin/employee/EditEmployee.jsp").forward(request, response);
                return;
            }
            Employee e = new Employee();
            e.setEmployeeId(id);
            e.setIdentityNumber(identity);
            e.setPhoneNumber(phone);
            String dobRaw = request.getParameter("dateOfBirth");
            if (dobRaw != null && !dobRaw.isEmpty()) {
                e.setDateOfBirth(Date.valueOf(dobRaw));
            }
            e.setGender(request.getParameter("gender"));
            e.setRoleId(Integer.parseInt(request.getParameter("roleId")));
            e.setIsActive(request.getParameter("isActive") != null);
            EmployeeDAO dao = new EmployeeDAO();
            dao.update(e);
            response.sendRedirect("Employee?action=list");
        } catch (Exception ex) {
            response.sendRedirect("Employee?action=list");
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

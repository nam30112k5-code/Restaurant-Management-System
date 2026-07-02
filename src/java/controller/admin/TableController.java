/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.admin;

import dal.AppointmentDAO;
import dal.GuestDAO;
import dal.TableDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.List;
import model.Appointment;
import model.Employee;
import model.Table;

/**
 *
 * @author Nguyen Dang Hung
 */
public class TableController extends HttpServlet {

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
            out.println("<title>Servlet TableController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet TableController at " + request.getContextPath() + "</h1>");
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
        AppointmentDAO appDao = new AppointmentDAO();
        switch (action) {
            case "create":
                request.getRequestDispatcher("/views/admin/table/CreateTable.jsp").forward(request, response);
                break;
            case "edit":
                int id = Integer.parseInt(request.getParameter("id"));
                TableDAO tDao = new TableDAO();
                request.setAttribute("table", tDao.getById(id));
                request.getRequestDispatcher("/views/admin/table/EditTable.jsp").forward(request, response);
                break;
            case "delete":
                deleteTable(request, response);
                break;
            case "list":
            default:
                listTablesWithFilter(request, response);
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
        request.setCharacterEncoding("UTF-8");
        if ("book_admin".equals(action)) {
            bookTableAdmin(request, response);
            return;
        }
        TableDAO dao = new TableDAO();
        String name = request.getParameter("tableName");
        if ("store".equals(action)) {
            if (name != null && !name.trim().isEmpty()) {
                model.Table t = new model.Table();
                t.setTableName(name);
                dao.insert(t);
            }
        } else if ("update".equals(action)) {
            int id = Integer.parseInt(request.getParameter("tableId"));
            model.Table t = new model.Table(id, name);
            dao.update(t);
        }
        response.sendRedirect("Table");
    }

    // 1. Hàm hiển thị danh sách có bộ lọc
    private void listTablesWithFilter(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String dateRaw = request.getParameter("date");
        String timeRaw = request.getParameter("time");
        Date date;
        Time start, end;
        if (dateRaw == null || timeRaw == null) {
            long millis = System.currentTimeMillis();
            date = new Date(millis);
            LocalTime now = LocalTime.now();
            start = Time.valueOf(now);
            end = Time.valueOf(now.plusHours(2)); // Mặc định xem 2 tiếng
            timeRaw = now.format(DateTimeFormatter.ofPattern("HH:mm"));
            dateRaw = date.toString();
        } else {
            date = Date.valueOf(dateRaw);
            start = Time.valueOf(timeRaw + ":00");
            LocalTime localStart = LocalTime.parse(timeRaw);
            end = Time.valueOf(localStart.plusHours(2) + ":00");
        }
        AppointmentDAO appDao = new AppointmentDAO();
        List<Table> list = appDao.getTablesStatus(date, start, end);
        request.setAttribute("tables", list);
        request.setAttribute("filterDate", dateRaw);
        request.setAttribute("filterTime", timeRaw);
        request.getRequestDispatcher("/views/admin/table/ListTable.jsp").forward(request, response);
    }

    // 2. Hàm xử lý Admin đặt bàn
    private void bookTableAdmin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Employee admin = (Employee) session.getAttribute("account");
        try {
            String dateRaw = request.getParameter("date");
            String timeRaw = request.getParameter("time");
            String name = request.getParameter("guestName");
            String phone = request.getParameter("guestPhone");
            int tableId = Integer.parseInt(request.getParameter("tableId"));
            Date date = Date.valueOf(dateRaw);
            Time start = Time.valueOf(timeRaw + ":00");

            LocalTime localStart = LocalTime.parse(timeRaw);
            Time end = Time.valueOf(localStart.plusHours(2) + ":00");

            LocalDateTime bookingTime = LocalDateTime.of(date.toLocalDate(), localStart);
            LocalDateTime now = LocalDateTime.now();

            if (bookingTime.isBefore(now)) {
                request.setAttribute("error", "Không thể đặt bàn trong quá khứ! Vui lòng chọn thời gian khác.");
                listTablesWithFilter(request, response);
                return;
            }
            GuestDAO gDao = new GuestDAO();
            int guestId = gDao.createWalkInGuest(name, phone);
            if (guestId <= 0) {
                request.setAttribute("error", "Lỗi tạo thông tin khách hàng! Có thể SĐT đã bị trùng hoặc lỗi hệ thống.");
                listTablesWithFilter(request, response);
                return;
            }
            AppointmentDAO aDao = new AppointmentDAO();
            Appointment app = new Appointment();
            app.setGuestId(guestId);
            app.setTableId(tableId);
            app.setDate(date);
            app.setStartTime(start);
            app.setEndTime(end);
            app.setStatus("confirmed");
            app.setCreateBy(admin.getUsername());
            boolean isSuccess = aDao.insert(app);
            if (isSuccess) {
                response.sendRedirect("Table?date=" + request.getParameter("date") + "&time=" + request.getParameter("time") + "&message=BookingSuccess");
            } else {
                request.setAttribute("error", "Lỗi Database: Không thể lưu đơn đặt bàn. Vui lòng kiểm tra Console (Output) để xem chi tiết lỗi SQL.");
                listTablesWithFilter(request, response);
            }

        } catch (Exception e) {
            request.setAttribute("error", "Lỗi hệ thống: " + e.getMessage());
            listTablesWithFilter(request, response);
        }
    }

    private void listTables(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        TableDAO dao = new TableDAO();
        List<Table> list = dao.getAllWithRealTimeStatus();

        request.setAttribute("tables", list);
        request.getRequestDispatcher("/views/admin/table/ListTable.jsp").forward(request, response);
    }

    private void deleteTable(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            TableDAO dao = new TableDAO();
            dao.delete(id);
            response.sendRedirect("Table");
        } catch (RuntimeException e) {
            request.setAttribute("error", e.getMessage());
            listTables(request, response);
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

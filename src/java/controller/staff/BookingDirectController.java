/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.staff;

import dal.AppointmentDAO;
import dal.GuestDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import model.Appointment;
import model.Employee;
import model.Table;

/**
 *
 * @author Nguyen Dang Hung
 */
public class BookingDirectController extends HttpServlet {

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
            out.println("<title>Servlet BookingDirectController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet BookingDirectController at " + request.getContextPath() + "</h1>");
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
        loadPage(request, response);
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
        String action = request.getParameter("action");
        if ("book_walkin".equals(action)) {
            bookWalkIn(request, response);
        } else {
            loadPage(request, response);
        }
    }

    // Hàm hiển thị Sơ đồ bàn
    private void loadPage(HttpServletRequest request, HttpServletResponse response)
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
            end = Time.valueOf(now.plusHours(2));
            timeRaw = now.format(DateTimeFormatter.ofPattern("HH:mm"));
            dateRaw = date.toString();
        } else {
            date = Date.valueOf(dateRaw);
            start = Time.valueOf(timeRaw + ":00");
            LocalTime localStart = LocalTime.parse(timeRaw);
            end = Time.valueOf(localStart.plusHours(2) + ":00");
        }
        AppointmentDAO dao = new AppointmentDAO();
        List<Table> tables = dao.getTablesStatus(date, start, end);
        request.setAttribute("tables", tables);
        request.setAttribute("currentDate", dateRaw);
        request.setAttribute("currentTime", timeRaw);
        request.getRequestDispatcher("/views/staff/BookDirect.jsp").forward(request, response);
    }

    // Hàm đặt bàn cho khách vãng lai
    private void bookWalkIn(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Employee staff = (Employee) session.getAttribute("account");
        try {
            String name = request.getParameter("guestName");
            String phone = request.getParameter("guestPhone");
            int tableId = Integer.parseInt(request.getParameter("tableId"));
            Date date = Date.valueOf(request.getParameter("date"));
            String timeRaw = request.getParameter("time");
            Time start = Time.valueOf(timeRaw + ":00");
            Time end = Time.valueOf(LocalTime.parse(timeRaw).plusHours(2) + ":00");
            GuestDAO gDao = new GuestDAO();
            int guestId = gDao.createWalkInGuest(name, phone);
            if (guestId == -1) {
                request.setAttribute("error", "Lỗi tạo khách hàng!");
                loadPage(request, response);
                return;
            }
            AppointmentDAO aDao = new AppointmentDAO();
            Appointment app = new Appointment();
            app.setGuestId(guestId);
            app.setTableId(tableId);
            app.setDate(date);
            app.setStartTime(start);
            app.setEndTime(end);
            app.setCreateBy(staff.getUsername());
            app.setStatus("confirmed");
            boolean isSuccess = aDao.insert(app);
            if (isSuccess) {
                request.setAttribute("success", "Đặt bàn thành công cho khách: " + name);
            } else {
                request.setAttribute("error", "Lỗi Database khi lưu đơn.");
            }
            loadPage(request, response);
        } catch (Exception e) {
            loadPage(request, response);
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

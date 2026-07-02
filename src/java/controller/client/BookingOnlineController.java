/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.client;

import dal.AppointmentDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.Date;
import java.sql.Time;
import java.util.List;
import model.Appointment;
import model.Guest;
import model.Table;
import java.time.LocalDateTime;

/**
 *
 * @author Nguyen Dang Hung
 */
public class BookingOnlineController extends HttpServlet {

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
            out.println("<title>Servlet BookingOnlineController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet BookingOnlineController at " + request.getContextPath() + "</h1>");
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
        request.getRequestDispatcher("/views/client/Booking.jsp").forward(request, response);
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
        if ("check".equals(action)) {
            checkAvailability(request, response);
        } else if ("book".equals(action)) {
            confirmBooking(request, response);
        }
    }

    // 1.Kiểm tra ngày/giờ hợp lệ. Không cho đặt quá khứ. Lấy danh sách bàn trống
    private void checkAvailability(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String dateRaw = request.getParameter("date");
            String startRaw = request.getParameter("startTime");
            String endRaw = request.getParameter("endTime");
            Date date = Date.valueOf(dateRaw);
            Time startTime = Time.valueOf(startRaw + ":00");
            Time endTime = Time.valueOf(endRaw + ":00");
            if (startTime.after(endTime) || startTime.equals(endTime)) {
                request.setAttribute("error", "Giờ kết thúc phải sau giờ bắt đầu!");
                request.getRequestDispatcher("/views/client/Booking.jsp").forward(request, response);
                return;
            }
            LocalDateTime inputDateTime = LocalDateTime.of(date.toLocalDate(), startTime.toLocalTime());
            LocalDateTime now = LocalDateTime.now();
            if (inputDateTime.isBefore(now)) {
                request.setAttribute("error", "Không thể đặt bàn ở thời điểm trong quá khứ!");
                request.getRequestDispatcher("/views/client/Booking.jsp").forward(request, response);
                return;
            }
            AppointmentDAO dao = new AppointmentDAO();
            List<Table> availableTables = dao.getAvailableTables(date, startTime, endTime);
            request.setAttribute("tables", availableTables);
            request.setAttribute("searchDate", dateRaw);
            request.setAttribute("searchStart", startRaw);
            request.setAttribute("searchEnd", endRaw);
            request.setAttribute("searched", true);
            request.getRequestDispatcher("/views/client/Booking.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("error", "Dữ liệu ngày giờ không hợp lệ!");
            request.getRequestDispatcher("/views/client/Booking.jsp").forward(request, response);
        }
    }

    // 2. Cập nhật hàm confirmBooking (Thêm kiểm tra Pending)
    private void confirmBooking(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Guest guest = (Guest) session.getAttribute("account");
        if (guest == null) {
            response.sendRedirect("Login");
            return;
        }
        AppointmentDAO dao = new AppointmentDAO();
        if (dao.hasPendingBooking(guest.getGuestId())) {
            request.setAttribute("error", "Bạn đang có đơn chờ xác nhận. Vui lòng đợi nhân viên xác nhận hoặc hủy đơn cũ trước khi đặt mới.");
            request.getRequestDispatcher("/views/client/Booking.jsp").forward(request, response);
            return;
        }
        try {
            int tableId = Integer.parseInt(request.getParameter("tableId"));
            Date date = Date.valueOf(request.getParameter("date"));
            Time startTime = Time.valueOf(request.getParameter("startTime") + ":00");
            Time endTime = Time.valueOf(request.getParameter("endTime") + ":00");
            Appointment app = new Appointment();
            app.setGuestId(guest.getGuestId());
            app.setTableId(tableId);
            app.setDate(date);
            app.setStartTime(startTime);
            app.setEndTime(endTime);
            app.setStatus("pending");
            app.setCreateBy(guest.getUsername());
            dao.insert(app);
            request.setAttribute("success", "Đặt bàn thành công! Vui lòng chờ xác nhận.");
            request.getRequestDispatcher("/views/client/Booking.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("error", "Có lỗi xảy ra khi đặt bàn!");
            request.getRequestDispatcher("/views/client/Booking.jsp").forward(request, response);
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

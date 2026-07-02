<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="Booking Requests">
    <jsp:include page="../common/StaffNavbar.jsp" />

    <div class="container pb-5">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h3 class="text-white border-start border-4 border-warning ps-3">Booking Requests</h3>
        </div>

        <div class="card card-resto shadow">
            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table table-dark table-hover align-middle mb-0">
                        <thead class="bg-secondary text-white">
                            <tr>
                                <th class="ps-4">Date & Time</th>
                                <th>Table Info</th>
                                <th>Guest Info</th>
                                <th>Status</th>
                                <th class="text-end pe-4">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${appointments}" var="a">
                                <tr>
                                    <td class="ps-4">
                                        <div class="fw-bold text-white">${a.date}</div>
                                        <small class="text-muted">${a.startTime} - ${a.endTime}</small>
                                    </td>

                                    <td>
                                        <span class="text-orange fw-bold">${a.tableName}</span>
                                    </td>

                                    <td>
                                        <div class="text-white">${a.guestName}</div>
                                        <small class="text-muted"><i class="bi bi-telephone"></i> Contact via Phone</small>
                                    </td>

                                    <td>
                                        <c:choose>
                                            <c:when test="${a.status == 'pending'}"><span class="badge bg-warning text-dark"><i class="bi bi-hourglass-split"></i> Pending</span></c:when>
                                            <c:when test="${a.status == 'confirmed'}"><span class="badge bg-primary"><i class="bi bi-check2-circle"></i> Confirmed</span></c:when>
                                            <c:when test="${a.status == 'completed'}"><span class="badge bg-success"><i class="bi bi-flag-fill"></i> Completed</span></c:when>
                                            <c:when test="${a.status == 'cancelled'}"><span class="badge bg-danger"><i class="bi bi-x-circle"></i> Cancelled</span></c:when>
                                        </c:choose>
                                    </td>

                                    <td class="text-end pe-4">
                                        <c:if test="${a.status == 'pending'}">
                                            <a href="BookingManager?action=confirm&id=${a.appointmentId}" class="btn btn-sm btn-success me-1" title="Approve">
                                                <i class="bi bi-check-lg"></i> Confirm
                                            </a>
                                            <a href="BookingManager?action=cancel&id=${a.appointmentId}" class="btn btn-sm btn-outline-danger" title="Reject" onclick="return confirm('Reject this booking request?');">
                                                <i class="bi bi-x-lg"></i>
                                            </a>
                                        </c:if>

                                        <c:if test="${a.status == 'confirmed'}">
                                            <a href="BookingManager?action=complete&id=${a.appointmentId}" class="btn btn-sm btn-primary me-1" title="Mark as Completed">
                                                <i class="bi bi-check2-all"></i> Complete
                                            </a>
                                            <a href="BookingManager?action=cancel&id=${a.appointmentId}" class="btn btn-sm btn-outline-secondary" title="Cancel Booking" onclick="return confirm('Cancel this confirmed booking?');">
                                                Cancel
                                            </a>
                                        </c:if>

                                        <c:if test="${a.status == 'completed' || a.status == 'cancelled'}">
                                            <span class="text-muted small fst-italic">No actions</span>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>

                            <c:if test="${empty appointments}">
                                <tr>
                                    <td colspan="5" class="text-center py-5 text-muted">
                                        <i class="bi bi-inbox display-4 d-block mb-3"></i>
                                        No booking requests found.
                                    </td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</t:layout>
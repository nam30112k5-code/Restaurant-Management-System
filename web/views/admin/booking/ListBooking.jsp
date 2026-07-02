<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="Admin Booking Manager">
    <jsp:include page="../../common/AdminNavbar.jsp" />

    <div class="container pb-5">
        <h3 class="text-white border-start border-4 border-warning ps-3 mb-4">All Booking History</h3>

        <div class="card card-resto shadow">
            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table table-dark table-hover align-middle mb-0">
                        <thead class="bg-secondary text-white">
                            <tr>
                                <th class="ps-4">Date & Time</th>
                                <th>Table</th>
                                <th>Guest</th>
                                <th>Status</th>
                                <th class="text-end pe-4">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${appointments}" var="a">
                                <tr>
                                    <td class="ps-4">
                                        <div class="fw-bold">${a.date}</div>
                                        <small class="text-muted">${a.startTime} - ${a.endTime}</small>
                                    </td>
                                    <td class="text-orange fw-bold">${a.tableName}</td>
                                    <td>${a.guestName}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${a.status == 'pending'}"><span class="badge bg-warning text-dark">Pending</span></c:when>
                                            <c:when test="${a.status == 'confirmed'}"><span class="badge bg-primary">Confirmed</span></c:when>
                                            <c:when test="${a.status == 'completed'}"><span class="badge bg-success">Completed</span></c:when>
                                            <c:when test="${a.status == 'cancelled'}"><span class="badge bg-danger">Cancelled</span></c:when>
                                        </c:choose>
                                    </td>
                                    <td class="text-end pe-4">
                                        <c:if test="${a.status == 'pending'}">
                                            <a href="BookingManager?action=confirm&id=${a.appointmentId}" class="btn btn-sm btn-success" title="Confirm"><i class="bi bi-check-lg"></i></a>
                                            <a href="BookingManager?action=cancel&id=${a.appointmentId}" class="btn btn-sm btn-danger" title="Cancel"><i class="bi bi-x-lg"></i></a>
                                            </c:if>
                                            <c:if test="${a.status == 'confirmed'}">
                                            <a href="BookingManager?action=complete&id=${a.appointmentId}" class="btn btn-sm btn-primary">Done</a>
                                            <a href="BookingManager?action=cancel&id=${a.appointmentId}" class="btn btn-sm btn-secondary">Cancel</a>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</t:layout>
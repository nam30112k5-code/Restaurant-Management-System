<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="My History">
    <div class="container py-5">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2 class="text-white font-resto">My Booking History</h2>
            <a href="Booking" class="btn btn-outline-warning">
                <i class="bi bi-plus-lg"></i> Book New Table
            </a>
        </div>

        <div class="card card-resto shadow-lg">
            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table table-dark table-hover align-middle mb-0">
                        <thead class="bg-secondary text-white">
                            <tr>
                                <th class="ps-4">Date</th>
                                <th>Time Slot</th>
                                <th>Table</th>
                                <th>Status</th>
                                <th class="text-end pe-4">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${history}" var="h">
                                <tr>
                                    <td class="ps-4 fw-bold text-white">${h.date}</td>
                                    <td class="text-muted small">${h.startTime} - ${h.endTime}</td>
                                    <td class="text-orange fw-bold">${h.tableName}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${h.status == 'pending'}">
                                                <span class="badge bg-warning text-dark"><i class="bi bi-hourglass-split"></i> Processing</span>
                                            </c:when>
                                            <c:when test="${h.status == 'confirmed'}">
                                                <span class="badge bg-primary"><i class="bi bi-check-circle-fill"></i> Confirmed</span>
                                            </c:when>
                                            <c:when test="${h.status == 'completed'}">
                                                <span class="badge bg-success"><i class="bi bi-check2-all"></i> Done</span>
                                            </c:when>
                                            <c:when test="${h.status == 'cancelled'}">
                                                <span class="badge bg-danger"><i class="bi bi-x-circle-fill"></i> Cancelled</span>
                                            </c:when>
                                        </c:choose>
                                    </td>
                                    <td class="text-end pe-4">
                                        <c:if test="${h.status == 'pending' || h.status == 'confirmed'}">
                                            <a href="History?action=cancel&id=${h.appointmentId}" 
                                               class="btn btn-sm btn-outline-danger"
                                               onclick="return confirm('Cancel this booking?');">
                                                <i class="bi bi-trash"></i> Cancel
                                            </a>
                                        </c:if>

                                        <c:if test="${h.status == 'completed'}">
                                            <c:choose>
                                                <c:when test="${!h.rated}">
                                                    <a href="Feedback?appointmentId=${h.appointmentId}" class="btn btn-sm btn-warning text-dark fw-bold">
                                                        <i class="bi bi-star-fill"></i> Rate
                                                    </a>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-success text-white p-2">
                                                        <i class="bi bi-check-circle-fill"></i> Rated
                                                    </span>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:if>

                                        <c:if test="${h.status == 'cancelled'}">
                                            <span class="text-muted small fst-italic">No action</span>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>

                            <c:if test="${empty history}">
                                <tr>
                                    <td colspan="5" class="text-center py-5">
                                        <div class="text-muted">
                                            <i class="bi bi-calendar-x display-4 mb-3 d-block"></i>
                                            You haven't booked any tables yet.
                                        </div>
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
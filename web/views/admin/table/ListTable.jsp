<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="Manage Tables">
    <jsp:include page="../../common/AdminNavbar.jsp" />

    <div class="container pb-5">
        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show">
                ${error} <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        <c:if test="${param.message == 'BookingSuccess'}">
            <div class="alert alert-success alert-dismissible fade show">
                Booking Successful! <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <div class="card card-resto mb-4 border-secondary">
            <div class="card-body">
                <div class="row align-items-center">
                    <div class="col-md-8">
                        <form action="Table" method="GET" class="row g-2 align-items-center">
                            <div class="col-auto"><span class="text-orange fw-bold">Check Status:</span></div>
                            <div class="col-auto">
                                <input type="date" name="date" value="${filterDate}" class="form-control form-control-sm bg-dark text-white border-secondary" required>
                            </div>
                            <div class="col-auto">
                                <input type="time" name="time" value="${filterTime}" class="form-control form-control-sm bg-dark text-white border-secondary" required>
                            </div>
                            <div class="col-auto">
                                <button type="submit" class="btn btn-sm btn-outline-warning"><i class="bi bi-search"></i> Check</button>
                            </div>
                        </form>
                    </div>
                    <div class="col-md-4 text-end">
                        <a href="Table?action=create" class="btn btn-sm btn-success fw-bold"><i class="bi bi-plus-lg"></i> Add Table</a>
                    </div>
                </div>
            </div>
        </div>

        <div class="card card-resto shadow">
            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table table-dark table-hover align-middle mb-0">
                        <thead class="bg-secondary text-white">
                            <tr>
                                <th class="ps-4">Table Name</th>
                                <th>Status (at ${filterTime})</th>
                                <th>Guest Info</th>
                                <th class="text-end pe-4">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${tables}" var="t">
                                <tr>
                                    <td class="ps-4 fw-bold text-orange fs-5">${t.tableName}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${t.booked}">
                                                <span class="badge bg-danger"><i class="bi bi-x-circle"></i> Occupied</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-success"><i class="bi bi-check-circle"></i> Available</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:if test="${t.booked}">
                                            <div class="text-white">${t.currentGuestName}</div>
                                            <small class="text-muted"><i class="bi bi-clock"></i> ${t.timeRange}</small>
                                        </c:if>
                                        <c:if test="${!t.booked}"><span class="text-muted small">-</span></c:if>
                                        </td>
                                        <td class="text-end pe-4">
                                        <c:if test="${!t.booked}">
                                            <button class="btn btn-sm btn-outline-success me-1" onclick="openAdminBookingModal('${t.tableId}', '${t.tableName}')">
                                                <i class="bi bi-journal-plus"></i> Book
                                            </button>
                                            <a href="Table?action=edit&id=${t.tableId}" class="btn btn-sm btn-outline-secondary me-1"><i class="bi bi-pencil"></i></a>
                                            <a href="Table?action=delete&id=${t.tableId}" class="btn btn-sm btn-outline-danger" onclick="return confirm('Delete ${t.tableName}?')"><i class="bi bi-trash"></i></a>
                                            </c:if>
                                            <c:if test="${t.booked}">
                                            <span class="text-muted small fst-italic">In Use</span>
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

    <div class="modal fade" id="adminBookModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content bg-dark text-white border-secondary">
                <div class="modal-header border-secondary">
                    <h5 class="modal-title text-orange">Admin Booking</h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <form action="Table" method="POST">
                    <div class="modal-body">
                        <input type="hidden" name="action" value="book_admin">
                        <input type="hidden" name="tableId" id="modalTableId">
                        <input type="hidden" name="date" value="${filterDate}">
                        <input type="hidden" name="time" value="${filterTime}">

                        <div class="mb-3">
                            <label>Table</label>
                            <input type="text" id="modalTableName" class="form-control bg-secondary text-white border-0" readonly>
                        </div>
                        <div class="mb-3">
                            <label>Guest Name</label>
                            <input type="text" name="guestName" class="form-control bg-dark text-white border-secondary" required>
                        </div>
                        <div class="mb-3">
                            <label>Phone</label>
                            <input type="text" name="guestPhone" class="form-control bg-dark text-white border-secondary" required>
                        </div>
                    </div>
                    <div class="modal-footer border-secondary">
                        <button type="submit" class="btn btn-resto">Confirm</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</t:layout>
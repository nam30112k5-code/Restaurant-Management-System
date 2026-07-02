<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="Direct Booking Map">
    <jsp:include page="../common/StaffNavbar.jsp" />

    <div class="container pb-5">

        <div class="card card-resto mb-4 border-secondary shadow">
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/Staff/Booking" method="POST" class="row align-items-end g-3">
                    <div class="col-md-4">
                        <label class="form-label text-muted small text-uppercase fw-bold">Select Date</label>
                        <input type="date" name="date" value="${currentDate}" class="form-control bg-dark text-white border-secondary" required>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label text-muted small text-uppercase fw-bold">Start Time (View 2 Hours)</label>
                        <input type="time" name="time" value="${currentTime}" class="form-control bg-dark text-white border-secondary" required>
                    </div>
                    <div class="col-md-4">
                        <button type="submit" name="action" value="check" class="btn btn-warning w-100 fw-bold">
                            <i class="bi bi-arrow-clockwise"></i> Refresh Map Status
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <c:if test="${not empty success}">
            <div class="alert alert-success bg-dark border-success text-success alert-dismissible fade show">
                <i class="bi bi-check-circle-fill"></i> ${success}
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="alert alert-danger bg-dark border-danger text-danger alert-dismissible fade show">
                <i class="bi bi-exclamation-triangle-fill"></i> ${error}
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <h4 class="mb-4 text-white border-start border-4 border-warning ps-3">Table Map Status</h4>

        <div class="row g-4">
            <c:forEach items="${tables}" var="t">
                <div class="col-6 col-md-4 col-lg-3">
                    <c:choose>
                        <c:when test="${t.booked}">
                            <div class="card h-100 text-center py-4 border-danger bg-dark shadow" style="border-width: 2px; opacity: 0.7;">
                                <div class="card-body">
                                    <div class="mb-2 text-danger display-6"><i class="bi bi-x-circle-fill"></i></div>
                                    <h5 class="fw-bold text-white">${t.tableName}</h5>
                                    <span class="badge bg-danger mt-2">Occupied</span>
                                </div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="card h-100 text-center py-4 border-success bg-dark shadow table-hover-effect" 
                                 style="border-width: 2px; cursor: pointer; transition: transform 0.2s;"
                                 onclick="openBookingModal('${t.tableId}', '${t.tableName}')"
                                 onmouseover="this.style.transform = 'translateY(-5px)'"
                                 onmouseout="this.style.transform = 'translateY(0)'">
                                <div class="card-body">
                                    <div class="mb-2 text-success display-6"><i class="bi bi-check-circle-fill"></i></div>
                                    <h5 class="fw-bold text-white">${t.tableName}</h5>
                                    <span class="badge bg-success mt-2">Available</span>
                                </div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </c:forEach>
        </div>
    </div>

    <div class="modal fade" id="bookingModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content bg-dark text-white border-secondary">
                <div class="modal-header border-secondary">
                    <h5 class="modal-title text-success"><i class="bi bi-journal-plus"></i> Walk-in Booking</h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <form action="${pageContext.request.contextPath}/Staff/Booking" method="POST">
                    <div class="modal-body">
                        <input type="hidden" name="action" value="book_walkin">
                        <input type="hidden" name="tableId" id="modalTableId">
                        <input type="hidden" name="date" value="${currentDate}">
                        <input type="hidden" name="time" value="${currentTime}">
                        <div class="mb-3">
                            <label class="fw-bold text-muted">Selected Table</label>
                            <input type="text" id="modalTableName" class="form-control bg-secondary text-white border-0 fw-bold" readonly>
                        </div>
                        <div class="mb-3">
                            <label class="form-label text-muted">Guest Name <span class="text-danger">*</span></label>
                            <input type="text" name="guestName" class="form-control bg-dark text-white border-secondary" required placeholder="Ex: Mr. John">
                        </div>
                        <div class="mb-3">
                            <label class="form-label text-muted">Phone Number <span class="text-danger">*</span></label>
                            <input type="text" name="guestPhone" class="form-control bg-dark text-white border-secondary" required placeholder="Ex: 0912345678">
                        </div>
                        <div class="alert alert-info bg-dark border-info text-info py-2 d-flex align-items-center small">
                            <i class="bi bi-info-circle-fill me-2"></i>
                            <div>Booking for <strong>2 hours</strong> starting from <strong>${currentTime}</strong>.</div>
                        </div>
                    </div>
                    <div class="modal-footer border-secondary">
                        <button type="button" class="btn btn-outline-light" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-success fw-bold">Confirm Booking</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</t:layout>
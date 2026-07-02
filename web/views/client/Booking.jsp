<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="Book a Table">

    <div class="container py-5">
        <div class="text-center mb-5">
            <h5 class="text-orange text-uppercase letter-spacing-2">Reservation</h5>
            <h2 class="font-resto display-4 text-white">Book Your Table</h2>
        </div>

        <c:if test="${not empty error}">
            <div class="alert alert-danger bg-dark border-danger text-danger text-center shadow">
                <i class="bi bi-exclamation-triangle-fill"></i> ${error}
            </div>
        </c:if>
        <c:if test="${not empty success}">
            <div class="alert alert-success bg-dark border-success text-success text-center shadow">
                <i class="bi bi-check-circle-fill"></i> ${success}
            </div>
        </c:if>

        <div class="card card-resto shadow-lg mb-5 border-warning border-top border-3">
            <div class="card-body p-4 p-lg-5">
                <form action="Booking?action=check" method="POST">
                    <div class="row g-3">
                        <div class="col-md-4">
                            <label class="form-label text-muted text-uppercase small fw-bold">Select Date</label>
                            <input type="date" name="date" value="${searchDate}" class="form-control bg-dark text-white border-secondary" required>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label text-muted text-uppercase small fw-bold">Start Time</label>
                            <input type="time" name="startTime" value="${searchStart}" class="form-control bg-dark text-white border-secondary" required>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label text-muted text-uppercase small fw-bold">End Time</label>
                            <input type="time" name="endTime" value="${searchEnd}" class="form-control bg-dark text-white border-secondary" required>
                        </div>
                        <div class="col-12 text-center mt-4">
                            <button type="submit" class="btn btn-resto btn-lg px-5 shadow">
                                <i class="bi bi-search"></i> Check Availability
                            </button>
                        </div>
                    </div>
                </form>
            </div>
        </div>

        <c:if test="${searched}">
            <div class="d-flex align-items-center mb-4 border-bottom border-secondary pb-2">
                <h4 class="text-white mb-0 me-3">
                    <i class="bi bi-calendar-check text-orange"></i> Available Tables
                </h4>
                <small class="text-muted ms-auto">
                    ${searchDate} <span class="mx-2">|</span> ${searchStart} - ${searchEnd}
                </small>
            </div>

            <c:if test="${empty tables}">
                <div class="text-center py-5 text-muted">
                    <i class="bi bi-emoji-frown display-1 mb-3"></i>
                    <p class="lead">Sorry, no tables available for this time slot.</p>
                    <p>Please try a different time or date.</p>
                </div>
            </c:if>

            <div class="row g-4">
                <c:forEach items="${tables}" var="t">
                    <div class="col-6 col-md-4 col-lg-3">
                        <div class="card bg-dark border border-success h-100 shadow text-center booking-card" 
                             style="transition: transform 0.3s;">
                            <div class="card-body d-flex flex-column justify-content-center">
                                <div class="mb-3 text-success">
                                    <i class="bi bi-check-circle-fill display-4"></i>
                                </div>
                                <h5 class="card-title text-white fw-bold mb-1">${t.tableName}</h5>
                                <span class="badge bg-success bg-opacity-25 text-success mb-3">Available</span>

                                <form action="Booking?action=book" method="POST" class="mt-auto">
                                    <input type="hidden" name="tableId" value="${t.tableId}">
                                    <input type="hidden" name="date" value="${searchDate}">
                                    <input type="hidden" name="startTime" value="${searchStart}">
                                    <input type="hidden" name="endTime" value="${searchEnd}">

                                    <button type="submit" class="btn btn-outline-success w-100 fw-bold" 
                                            onclick="return confirm('Book ${t.tableName} for ${searchDate}?')">
                                        Book Now
                                    </button>
                                </form>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:if>
    </div>
</t:layout>
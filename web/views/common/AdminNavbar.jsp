<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="bg-dark border-bottom border-secondary mb-4 shadow-sm">
    <div class="container">
        <nav class="navbar navbar-expand-lg navbar-dark p-0">
            <div class="container-fluid px-0">
                <span class="navbar-brand text-warning fw-bold fs-5 me-4" style="font-family: 'Dancing Script', cursive;">
                    <i class="bi bi-speedometer2"></i> Admin Panel
                </span>

                <button class="navbar-toggler my-2" type="button" data-bs-toggle="collapse" data-bs-target="#adminNav">
                    <span class="navbar-toggler-icon"></span>
                </button>

                <div class="collapse navbar-collapse" id="adminNav">
                    <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                        <li class="nav-item">
                            <a class="nav-link ${pageContext.request.servletPath.contains('Employee') ? 'active text-orange' : ''}" 
                               href="${pageContext.request.contextPath}/Employee">
                                <i class="bi bi-people"></i> Employees
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link ${pageContext.request.servletPath.contains('Table') ? 'active text-orange' : ''}" 
                               href="${pageContext.request.contextPath}/Table">
                                <i class="bi bi-grid-3x3"></i> Tables
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link ${pageContext.request.servletPath.contains('BookingManager') ? 'active text-orange' : ''}" 
                               href="${pageContext.request.contextPath}/Admin/BookingManager">
                                <i class="bi bi-calendar-check"></i> Bookings
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link ${pageContext.request.servletPath.contains('Guest') ? 'active text-orange' : ''}" 
                               href="${pageContext.request.contextPath}/Guest">
                                <i class="bi bi-person-badge"></i> Guests
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link ${pageContext.request.servletPath.contains('Feedback') ? 'active text-orange' : ''}" 
                               href="${pageContext.request.contextPath}/Admin/Feedback">
                                <i class="bi bi-chat-quote"></i> Feedbacks
                            </a>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>
    </div>
</div>
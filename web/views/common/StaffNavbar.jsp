<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="bg-dark border-bottom border-secondary mb-4 shadow-sm">
    <div class="container">
        <nav class="navbar navbar-expand-lg navbar-dark p-0">
            <div class="container-fluid px-0">
                <!-- Logo / Title -->
                <span class="navbar-brand text-orange fw-bold fs-5 me-4 font-resto">
                    <i class="bi bi-person-workspace"></i> Receptionist
                </span>

                <button class="navbar-toggler my-2" type="button" data-bs-toggle="collapse" data-bs-target="#staffNav">
                    <span class="navbar-toggler-icon"></span>
                </button>

                <div class="collapse navbar-collapse" id="staffNav">
                    <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                        <li class="nav-item">
                            <a class="nav-link ${pageContext.request.servletPath.contains('BookingDirect') || pageContext.request.servletPath.endsWith('Booking') ? 'active text-orange' : ''}" 
                               href="${pageContext.request.contextPath}/Staff/Booking">
                                <i class="bi bi-grid-3x3"></i> Direct Booking (Map)
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link ${pageContext.request.servletPath.contains('BookingManager') ? 'active text-orange' : ''}" 
                               href="${pageContext.request.contextPath}/Staff/BookingManager">
                                <i class="bi bi-list-check"></i> Booking Requests
                            </a>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>
    </div>
</div>
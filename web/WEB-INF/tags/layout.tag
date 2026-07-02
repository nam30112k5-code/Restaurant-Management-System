<%@tag description="Main Layout" pageEncoding="UTF-8"%>
<%@attribute name="title" required="true" rtexprvalue="true" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%-- Lấy đường dẫn hiện tại để check Active --%>
<c:set var="currentPath" value="${pageContext.request.servletPath}" />

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title} - Resto Management</title>
    <!-- Bootstrap 5 -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <!-- Custom CSS -->
    <link href="${pageContext.request.contextPath}/static/css/style.css" rel="stylesheet">
    <!-- Recaptcha -->
    <script src="https://www.google.com/recaptcha/api.js" async defer></script>
</head>
<body class="d-flex flex-column min-vh-100">

    <!-- NAVBAR -->
    <nav class="navbar navbar-expand-lg navbar-dark navbar-custom fixed-top">
        <div class="container">
            <a class="navbar-brand font-resto fs-3 text-orange" href="${pageContext.request.contextPath}/Home">Resto Restaurant</a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNav">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="collapse navbar-collapse" id="mainNav">
                <ul class="navbar-nav ms-auto align-items-center">
                    
                    <!-- 1. COMMON LINKS -->
                    <li class="nav-item">
                        <a class="nav-link ${title == 'Home' ? 'active-orange' : ''}" 
                           href="${pageContext.request.contextPath}/Home">Home</a>
                    </li>

                    <!-- 2. ROLE BASED LINKS -->
                    <c:if test="${not empty sessionScope.account}">
                        
                        <%-- === GUEST MENU === --%>
                        <c:if test="${sessionScope.role == 'guest'}">
                            <!-- DEBUG PATH: ${currentPath} -->
                            <!-- Logic: Check nếu URL chứa /Booking -->
                            <li class="nav-item">
                                <a class="nav-link ${currentPath.contains('Booking') && !currentPath.contains('History') ? 'active-orange' : ''}" 
                                   href="${pageContext.request.contextPath}/Booking">Book Table</a>
                            </li>
                            <!-- Logic: Check nếu URL chứa /History -->
                            <li class="nav-item">
                                <a class="nav-link ${currentPath.contains('BookingHistory') || currentPath.contains('CreateFeedback') ? 'active-orange' : ''}" 
                                   href="${pageContext.request.contextPath}/History">History</a>
                            </li>
                        </c:if>

                        <%-- === STAFF / RECEPTIONIST (RoleID = 2) === --%>
                        <c:if test="${sessionScope.role == 'employee' && sessionScope.roleId == 2}">
                            <!-- DEBUG PATH: ${currentPath} -->
                            <!-- Logic: Booking Requests (/Staff/BookingManager) -->
                            <li class="nav-item">
                                <a class="nav-link ${currentPath.contains('ManageBooking') ? 'active-orange' : ''}" 
                                   href="${pageContext.request.contextPath}/Staff/BookingManager">Requests</a>
                            </li>
                            
                            <!-- Logic: Table Map (/Staff/Booking) 
                                 Lưu ý: Phải loại trừ trường hợp BookingManager để không bị active nhầm 
                            -->
                            <li class="nav-item">
                                <a class="nav-link ${currentPath.contains('BookDirect') && !currentPath.contains('Manager') ? 'active-orange' : ''}" 
                                   href="${pageContext.request.contextPath}/Staff/Booking">Table Map</a>
                            </li>
                        </c:if>

                        <%-- === ADMIN (RoleID = 1) === --%>
                        <c:if test="${sessionScope.role == 'employee' && sessionScope.roleId == 1}">
                            <!-- DEBUG PATH: ${currentPath} -->
                            <!-- Logic Active cho Dropdown cha: Nếu đang ở bất kỳ trang admin nào -->
                            <c:set var="isAdminActive" value="${currentPath.contains('Employee') || currentPath.contains('Table') || currentPath.contains('Guest') || currentPath.contains('Booking') || currentPath.contains('Feedback')}" />
                            
                            <li class="nav-item dropdown">
                                <a class="nav-link dropdown-toggle ${isAdminActive ? 'active-orange' : ''} text-warning" 
                                   href="#" data-bs-toggle="dropdown">Admin Panel</a>
                                
                                <ul class="dropdown-menu dropdown-menu-dark bg-dark shadow border-secondary">
                                    <li><a class="dropdown-item ${currentPath.contains('Employee') ? 'text-orange' : ''}" href="${pageContext.request.contextPath}/Employee">Employees</a></li>
                                    <li><a class="dropdown-item ${currentPath.contains('Table') ? 'text-orange' : ''}" href="${pageContext.request.contextPath}/Table">Tables</a></li>
                                    <li><a class="dropdown-item ${currentPath.contains('Guest') ? 'text-orange' : ''}" href="${pageContext.request.contextPath}/Guest">Guests</a></li>
                                    <li><a class="dropdown-item ${currentPath.contains('Admin/Feedback') ? 'text-orange' : ''}" href="${pageContext.request.contextPath}/Admin/Feedback">Feedbacks</a></li>
                                    <li><hr class="dropdown-divider"></li>
                                    <li><a class="dropdown-item ${currentPath.contains('Admin/Booking') ? 'text-orange' : ''}" href="${pageContext.request.contextPath}/Admin/BookingManager">All Bookings</a></li>
                                </ul>
                            </li>
                        </c:if>

                    </c:if>

                    <!-- 3. AUTH LINKS (LOGIN/REGISTER/PROFILE) -->
                    <c:choose>
                        <c:when test="${empty sessionScope.account}">
                            <li class="nav-item">
                                <a class="nav-link ${currentPath.contains('/Login') ? 'active-orange' : ''}" 
                                   href="${pageContext.request.contextPath}/Login">Login</a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link ${currentPath.contains('/Register') ? 'active-orange' : ''}" 
                                   href="${pageContext.request.contextPath}/Register">Register</a>
                            </li>
                        </c:when>
                        <c:otherwise>
                            <!-- Profile Dropdown -->
                            <c:set var="isProfileActive" value="${currentPath.contains('Profile') || currentPath.contains('ChangePassword')}" />
                            
                            <li class="nav-item dropdown ms-3">
                                <a class="nav-link dropdown-toggle ${isProfileActive ? 'active-orange' : ''} text-orange" href="#" data-bs-toggle="dropdown">
                                    <i class="bi bi-person-circle"></i> 
                                    
                                    <%-- LOGIC HIỂN THỊ TÊN --%>
                                    <c:choose>
                                        <%-- Nếu là Guest: Hiển thị Full Name --%>
                                        <c:when test="${sessionScope.role == 'guest'}">
                                            ${sessionScope.account.name}
                                        </c:when>
                                        <%-- Nếu là Employee (Admin/Staff): Hiển thị Username (Vì object Employee không có thuộc tính name) --%>
                                        <c:otherwise>
                                            ${sessionScope.account.username}
                                        </c:otherwise>
                                    </c:choose>
                                </a>
                                
                                <ul class="dropdown-menu dropdown-menu-dark bg-dark shadow border-secondary">
                                    <li><a class="dropdown-item ${currentPath.contains('Profile') ? 'text-orange' : ''}" href="${pageContext.request.contextPath}/Profile">Profile</a></li>
                                    <li><hr class="dropdown-divider"></li>
                                    <li><a class="dropdown-item ${currentPath.contains('ChangePassword') ? 'text-orange' : ''}" href="${pageContext.request.contextPath}/ChangePassword">Change Password</a></li>
                                    <li><hr class="dropdown-divider"></li>
                                    <li><a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/Logout">Logout</a></li>
                                </ul>
                            </li>
                        </c:otherwise>
                    </c:choose>
                </ul>
            </div>
        </div>
    </nav>

    <!-- CONTENT -->
    <div style="padding-top: 80px; flex: 1;">
        <jsp:doBody />
    </div>

    <!-- FOOTER -->
    <footer class="bg-dark text-white text-center py-4 mt-auto border-top border-secondary">
        <div class="container">
            <h5 class="font-resto text-orange">Resto Restaurant</h5>
            <p class="small text-muted mb-0">&copy; 2023 Designed with JSP & Bootstrap 5.</p>
        </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/js/main.js"></script>

</body>
</html>
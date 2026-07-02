<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="My Profile">
    <div class="container py-5">

        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2 class="text-orange font-resto">My Profile</h2>

            <c:choose>
                <c:when test="${sessionScope.role == 'guest'}">
                    <a href="Home" class="btn btn-outline-light btn-sm"><i class="bi bi-house"></i> Back to Home</a>
                </c:when>
                <c:when test="${sessionScope.role == 'employee' && sessionScope.roleId == 1}">
                    <a href="Employee" class="btn btn-outline-light btn-sm"><i class="bi bi-speedometer2"></i> Dashboard</a>
                </c:when>
                <c:when test="${sessionScope.role == 'employee' && sessionScope.roleId == 2}">
                    <a href="Staff/BookingManager" class="btn btn-outline-light btn-sm"><i class="bi bi-speedometer2"></i> Dashboard</a>
                </c:when>
            </c:choose>
        </div>

        <div class="row justify-content-center">
            <div class="col-md-4 mb-4">
                <div class="card card-resto shadow h-100 text-center py-5">
                    <div class="card-body">
                        <div class="mb-3">
                            <i class="bi bi-person-circle display-1 text-secondary"></i>
                        </div>
                        <h4 class="text-white">${sessionScope.account.username}</h4>
                        <p class="text-muted mb-4 text-uppercase small ls-1">
                            ${sessionScope.role == 'guest' ? 'Customer' : (sessionScope.roleId == 1 ? 'Administrator' : 'Receptionist')}
                        </p>
                        <a href="ChangePassword" class="btn btn-outline-warning w-100">
                            <i class="bi bi-key"></i> Change Password
                        </a>
                    </div>
                </div>
            </div>

            <div class="col-md-8 mb-4">
                <div class="card card-resto shadow h-100">
                    <div class="card-header bg-transparent border-bottom border-secondary pt-3">
                        <h5 class="text-white mb-0"><i class="bi bi-pencil-fill text-orange"></i> Edit Details</h5>
                    </div>
                    <div class="card-body p-4">

                        <c:if test="${not empty success}">
                            <div class="alert alert-success bg-dark border-success text-success mb-3 small">
                                <i class="bi bi-check-circle"></i> ${success}
                            </div>
                        </c:if>
                        <c:if test="${not empty error}">
                            <div class="alert alert-danger bg-dark border-danger text-danger mb-3 small">
                                <i class="bi bi-exclamation-circle"></i> ${error}
                            </div>
                        </c:if>

                        <form action="Profile" method="POST">
                            <div class="row g-3">
                                <div class="col-md-6">
                                    <label class="form-label text-muted small text-uppercase fw-bold">Username</label>
                                    <input type="text" class="form-control bg-secondary text-white border-0" 
                                           value="${sessionScope.account.username}" disabled readonly>
                                </div>

                                <c:if test="${sessionScope.role == 'guest'}">
                                    <div class="col-md-6">
                                        <label class="form-label text-muted small text-uppercase fw-bold">Full Name</label>
                                        <input type="text" name="name" class="form-control bg-dark text-white border-secondary" 
                                               value="${sessionScope.account.name}" required>
                                    </div>
                                </c:if>

                                <c:if test="${sessionScope.role == 'employee'}">
                                    <div class="col-md-6">
                                        <label class="form-label text-muted small text-uppercase fw-bold">Role</label>
                                        <input type="text" class="form-control bg-secondary text-white border-0" 
                                               value="${sessionScope.roleId == 1 ? 'Admin' : 'Receptionist'}" disabled readonly>
                                    </div>
                                </c:if>

                                <div class="col-md-6">
                                    <label class="form-label text-muted small text-uppercase fw-bold">Phone Number</label>
                                    <input type="text" name="phoneNumber" class="form-control bg-dark text-white border-secondary" 
                                           value="${sessionScope.account.phoneNumber}" maxlength="10">
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label text-muted small text-uppercase fw-bold">Identity Number</label>
                                    <input type="text" name="identityNumber" class="form-control bg-dark text-white border-secondary" 
                                           value="${sessionScope.account.identityNumber}" maxlength="12">
                                </div>

                                <div class="col-md-6">
                                    <label class="form-label text-muted small text-uppercase fw-bold">Date of Birth</label>
                                    <input type="date" name="dateOfBirth" class="form-control bg-dark text-white border-secondary" 
                                           value="${sessionScope.account.dateOfBirth}" required>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label text-muted small text-uppercase fw-bold">Gender</label>
                                    <select name="gender" class="form-select bg-dark text-white border-secondary">
                                        <option value="male" ${sessionScope.account.gender == 'male' ? 'selected' : ''}>Male</option>
                                        <option value="female" ${sessionScope.account.gender == 'female' ? 'selected' : ''}>Female</option>
                                    </select>
                                </div>
                            </div>

                            <div class="mt-4 text-end">
                                <button type="submit" class="btn btn-resto px-4 shadow">Update Profile</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</t:layout>
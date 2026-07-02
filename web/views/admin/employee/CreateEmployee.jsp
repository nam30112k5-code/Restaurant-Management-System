<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="Create Employee">
    <jsp:include page="../../common/AdminNavbar.jsp" />

    <div class="container pb-5">
        <div class="row justify-content-center">
            <div class="col-lg-8">
                <div class="card card-resto shadow-lg">
                    <div class="card-header bg-transparent border-bottom border-secondary">
                        <h4 class="text-orange mb-0"><i class="bi bi-person-plus"></i> Add New Employee</h4>
                    </div>
                    <div class="card-body p-4">
                        <c:if test="${not empty error}"><div class="alert alert-danger">${error}</div></c:if>

                            <form action="Employee?action=store" method="POST">
                                <div class="row g-3">
                                    <div class="col-md-6">
                                        <label class="form-label text-muted">Username (Max 10)</label>
                                        <input type="text" name="username" class="form-control" required maxlength="10" value="${employee.username}">
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label text-muted">Password</label>
                                    <input type="password" name="password" class="form-control" required>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label text-muted">Identity Number (12)</label>
                                    <input type="text" name="identityNumber" class="form-control" maxlength="12" value="${employee.identityNumber}">
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label text-muted">Phone Number (10)</label>
                                    <input type="text" name="phoneNumber" class="form-control" maxlength="10" value="${employee.phoneNumber}">
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label text-muted">Date of Birth</label>
                                    <input type="date" name="dateOfBirth" class="form-control" required>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label text-muted">Gender</label>
                                    <select name="gender" class="form-select bg-dark text-white border-secondary">
                                        <option value="male">Male</option>
                                        <option value="female">Female</option>
                                    </select>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label text-muted">Role</label>
                                    <select name="roleId" class="form-select bg-dark text-white border-secondary">
                                        <option value="1">Admin</option>
                                        <option value="2">Receptionist</option>
                                    </select>
                                </div>
                                <div class="col-md-6 d-flex align-items-end">
                                    <div class="form-check">
                                        <input class="form-check-input" type="checkbox" name="isActive" checked id="activeCheck">
                                        <label class="form-check-label text-white" for="activeCheck">Account Active?</label>
                                    </div>
                                </div>
                            </div>
                            <div class="mt-4 text-end">
                                <a href="Employee?action=list" class="btn btn-outline-light me-2">Cancel</a>
                                <button type="submit" class="btn btn-resto">Create Employee</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</t:layout>
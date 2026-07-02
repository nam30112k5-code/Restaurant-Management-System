<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="Edit Employee">
    <jsp:include page="../../common/AdminNavbar.jsp" />

    <div class="container pb-5">
        <div class="row justify-content-center">
            <div class="col-lg-8">

                <div class="card card-resto shadow-lg border-0">

                    <div class="card-header bg-transparent border-bottom border-secondary pt-4 pb-3">
                        <h4 class="text-orange mb-0 font-resto" style="font-family: sans-serif;">
                            <i class="bi bi-pencil-square"></i> Edit Employee Information
                        </h4>
                    </div>

                    <div class="card-body p-4">

                        <c:if test="${not empty error}">
                            <div class="alert alert-danger bg-dark border-danger text-danger mb-4">
                                <i class="bi bi-exclamation-triangle-fill"></i> ${error}
                            </div>
                        </c:if>

                        <form action="Employee?action=update" method="POST">
                            <input type="hidden" name="employeeId" value="${employee.employeeId}">

                            <h6 class="text-secondary text-uppercase small ls-1 mb-3">Account Details</h6>
                            <div class="mb-4">
                                <label class="form-label text-muted">Username</label>
                                <input type="text" class="form-control bg-secondary text-white border-0" 
                                       value="${employee.username}" disabled readonly>
                                <div class="form-text text-muted"><i class="bi bi-info-circle"></i> Username cannot be changed.</div>
                            </div>

                            <h6 class="text-secondary text-uppercase small ls-1 mb-3 pt-2 border-top border-secondary">Personal Information</h6>

                            <div class="row g-3 mb-3">
                                <div class="col-md-6">
                                    <label class="form-label text-muted">Identity Number (12)</label>
                                    <input type="text" name="identityNumber" value="${employee.identityNumber}" 
                                           class="form-control bg-dark text-white border-secondary" maxlength="12">
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label text-muted">Phone Number (10)</label>
                                    <input type="text" name="phoneNumber" value="${employee.phoneNumber}" 
                                           class="form-control bg-dark text-white border-secondary" maxlength="10">
                                </div>
                            </div>

                            <div class="row g-3 mb-4">
                                <div class="col-md-6">
                                    <label class="form-label text-muted">Date of Birth</label>
                                    <input type="date" name="dateOfBirth" value="${employee.dateOfBirth}" 
                                           class="form-control bg-dark text-white border-secondary" required>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label text-muted">Gender</label>
                                    <select name="gender" class="form-select bg-dark text-white border-secondary">
                                        <option value="male" ${employee.gender == 'male' ? 'selected' : ''}>Male</option>
                                        <option value="female" ${employee.gender == 'female' ? 'selected' : ''}>Female</option>
                                    </select>
                                </div>
                            </div>

                            <h6 class="text-secondary text-uppercase small ls-1 mb-3 pt-2 border-top border-secondary">Role & Status</h6>

                            <div class="row g-3 mb-4">
                                <div class="col-md-6">
                                    <label class="form-label text-muted">Role Assignment</label>
                                    <select name="roleId" class="form-select bg-dark text-white border-secondary">
                                        <option value="1" ${employee.roleId == 1 ? 'selected' : ''}>Admin</option>
                                        <option value="2" ${employee.roleId == 2 ? 'selected' : ''}>Receptionist</option>
                                    </select>
                                </div>
                                <div class="col-md-6 d-flex align-items-end">
                                    <div class="form-check p-3 border border-secondary rounded w-100 bg-dark">
                                        <input class="form-check-input" type="checkbox" name="isActive" id="status" 
                                               ${employee.isActive ? 'checked' : ''}>
                                        <label class="form-check-label text-white fw-bold" for="status">
                                            Active Account
                                        </label>
                                        <div class="small text-muted ps-0 mt-1">Uncheck to block this employee access.</div>
                                    </div>
                                </div>
                            </div>

                            <div class="d-flex justify-content-end gap-2 mt-4">
                                <a href="Employee?action=list" class="btn btn-outline-light px-4">Cancel</a>
                                <button type="submit" class="btn btn-warning fw-bold px-4 text-dark">
                                    <i class="bi bi-save"></i> Save Changes
                                </button>
                            </div>

                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</t:layout>
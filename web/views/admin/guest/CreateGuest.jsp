<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="Create Guest">
    <jsp:include page="../../common/AdminNavbar.jsp" />

    <div class="container pb-5">
        <div class="row justify-content-center">
            <div class="col-lg-8">
                <div class="card card-resto shadow-lg">
                    <div class="card-header bg-transparent border-bottom border-secondary pt-3">
                        <h4 class="text-orange mb-0"><i class="bi bi-person-plus-fill"></i> Add New Guest</h4>
                    </div>
                    <div class="card-body p-4">

                        <c:if test="${not empty error}">
                            <div class="alert alert-danger bg-dark border-danger text-danger mb-4">
                                <i class="bi bi-exclamation-triangle"></i> ${error}
                            </div>
                        </c:if>

                        <form action="Guest?action=store" method="POST">
                            <h5 class="text-white mb-3 border-bottom border-secondary pb-2">Account Information</h5>
                            <div class="row g-3 mb-4">
                                <div class="col-md-6">
                                    <label class="form-label text-muted">Username <span class="text-danger">*</span></label>
                                    <input type="text" name="username" class="form-control bg-dark text-white border-secondary" 
                                           required value="${guest.username}">
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label text-muted">Password <span class="text-danger">*</span></label>
                                    <input type="password" name="password" class="form-control bg-dark text-white border-secondary" required>
                                </div>
                            </div>

                            <h5 class="text-white mb-3 border-bottom border-secondary pb-2">Personal Details</h5>
                            <div class="mb-3">
                                <label class="form-label text-muted">Full Name <span class="text-danger">*</span></label>
                                <input type="text" name="name" class="form-control bg-dark text-white border-secondary" 
                                       required value="${guest.name}">
                            </div>

                            <div class="row g-3 mb-3">
                                <div class="col-md-6">
                                    <label class="form-label text-muted">Phone Number</label>
                                    <input type="text" name="phoneNumber" class="form-control bg-dark text-white border-secondary" 
                                           maxlength="10" value="${guest.phoneNumber}">
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label text-muted">Identity Number (CCCD)</label>
                                    <input type="text" name="identityNumber" class="form-control bg-dark text-white border-secondary" 
                                           maxlength="12" value="${guest.identityNumber}">
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label text-muted">Date of Birth <span class="text-danger">*</span></label>
                                    <input type="date" name="dateOfBirth" class="form-control bg-dark text-white border-secondary" 
                                           required value="${guest.dateOfBirth}">
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label text-muted">Gender</label>
                                    <select name="gender" class="form-select bg-dark text-white border-secondary">
                                        <option value="male" ${guest.gender == 'male' ? 'selected' : ''}>Male</option>
                                        <option value="female" ${guest.gender == 'female' ? 'selected' : ''}>Female</option>
                                    </select>
                                </div>
                            </div>

                            <div class="d-flex justify-content-end gap-2 mt-4">
                                <a href="Guest" class="btn btn-outline-light">Cancel</a>
                                <button type="submit" class="btn btn-resto px-4">Create Guest</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</t:layout>
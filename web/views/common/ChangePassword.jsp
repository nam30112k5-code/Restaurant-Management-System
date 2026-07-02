<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="Change Password">
    <div class="container py-5">
        <div class="row justify-content-center">
            <div class="col-md-6 col-lg-5">

                <div class="card card-resto shadow-lg">
                    <div class="card-header bg-transparent border-bottom border-secondary pt-4 text-center">
                        <h3 class="text-orange font-resto">Security Check</h3>
                        <p class="text-muted small">Update your password to keep account safe</p>
                    </div>

                    <div class="card-body p-4">

                        <c:if test="${not empty success}">
                            <div class="alert alert-success bg-dark border-success text-success small">
                                <i class="bi bi-check-circle-fill"></i> ${success}
                            </div>
                        </c:if>
                        <c:if test="${not empty error}">
                            <div class="alert alert-danger bg-dark border-danger text-danger small">
                                <i class="bi bi-exclamation-triangle-fill"></i> ${error}
                            </div>
                        </c:if>

                        <form action="ChangePassword" method="POST">
                            <div class="mb-3">
                                <label class="form-label text-muted">Current Password</label>
                                <div class="input-group">
                                    <span class="input-group-text bg-dark border-secondary text-secondary"><i class="bi bi-lock"></i></span>
                                    <input type="password" name="oldPass" class="form-control bg-dark text-white border-secondary" 
                                           required placeholder="Enter current password">
                                </div>
                            </div>

                            <div class="mb-3">
                                <label class="form-label text-muted">New Password</label>
                                <div class="input-group">
                                    <span class="input-group-text bg-dark border-secondary text-secondary"><i class="bi bi-key"></i></span>
                                    <input type="password" name="newPass" class="form-control bg-dark text-white border-secondary" 
                                           required placeholder="Enter new password">
                                </div>
                            </div>

                            <div class="mb-4">
                                <label class="form-label text-muted">Confirm New Password</label>
                                <div class="input-group">
                                    <span class="input-group-text bg-dark border-secondary text-secondary"><i class="bi bi-key-fill"></i></span>
                                    <input type="password" name="confirmPass" class="form-control bg-dark text-white border-secondary" 
                                           required placeholder="Re-enter new password">
                                </div>
                            </div>

                            <div class="d-grid gap-2">
                                <button type="submit" class="btn btn-resto">Save Changes</button>
                                <a href="Profile" class="btn btn-outline-secondary">Back to Profile</a>
                            </div>
                        </form>
                    </div>
                </div>

            </div>
        </div>
    </div>
</t:layout>
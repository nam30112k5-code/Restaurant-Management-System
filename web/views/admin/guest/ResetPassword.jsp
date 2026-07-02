<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="Reset Password">
    <jsp:include page="../../common/AdminNavbar.jsp" />

    <div class="container pb-5">
        <div class="row justify-content-center">
            <div class="col-md-6">
                <div class="card card-resto shadow-lg border-warning border-start border-3">
                    <div class="card-header bg-transparent border-bottom border-secondary pt-3">
                        <h4 class="text-white mb-0">Reset Password</h4>
                        <p class="text-muted small mb-0">Set a new password for the guest account.</p>
                    </div>
                    <div class="card-body p-4">
                        <form action="Guest?action=update_pass" method="POST">
                            <input type="hidden" name="guestId" value="${guest.guestId}">

                            <div class="mb-3">
                                <label class="form-label text-muted">Username</label>
                                <input type="text" class="form-control bg-secondary text-white border-0" 
                                       value="${guest.username}" disabled readonly>
                            </div>
                            <div class="mb-3">
                                <label class="form-label text-muted">Full Name</label>
                                <input type="text" class="form-control bg-secondary text-white border-0" 
                                       value="${guest.name}" disabled readonly>
                            </div>

                            <hr class="border-secondary my-4">

                            <div class="mb-4">
                                <label class="form-label text-warning fw-bold">New Password</label>
                                <input type="text" name="newPassword" class="form-control bg-dark text-white border-warning" 
                                       required placeholder="Enter new password (e.g. 123456)">
                                <div class="form-text text-secondary">
                                    <i class="bi bi-info-circle"></i> Type the raw password. The system will encrypt it automatically.
                                </div>
                            </div>

                            <div class="d-grid gap-2">
                                <button type="submit" class="btn btn-warning text-dark fw-bold">Update Password</button>
                                <a href="Guest" class="btn btn-outline-light">Cancel</a>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</t:layout>
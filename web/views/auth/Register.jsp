<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="Register">
    <div class="container py-5">
        <div class="row justify-content-center">
            <div class="col-md-8">
                <div class="card card-resto shadow-lg">
                    <div class="card-header bg-transparent border-0 text-center pt-4">
                        <h2 class="font-resto text-orange">Create Account</h2>
                    </div>
                    <div class="card-body p-4">

                        <c:if test="${not empty error}">
                            <div class="alert alert-danger bg-transparent text-danger border-danger">${error}</div>
                        </c:if>

                        <form action="Register" method="POST">
                            <div class="row">
                                <div class="col-md-12 mb-3">
                                    <label class="form-label">Username (*)</label>
                                    <input type="text" name="username" class="form-control" required>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Password (*)</label>
                                    <input type="password" name="password" class="form-control" required>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Confirm Password (*)</label>
                                    <input type="password" name="repassword" class="form-control" required>
                                </div>
                            </div>

                            <div class="mb-3">
                                <label class="form-label">Full Name</label>
                                <input type="text" name="name" class="form-control" required>
                            </div>

                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Identity Number</label>
                                    <input type="text" name="identityNumber" class="form-control">
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Phone Number</label>
                                    <input type="text" name="phoneNumber" class="form-control">
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Date of Birth</label>
                                    <input type="date" name="dateOfBirth" class="form-control" required>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Gender</label>
                                    <select name="gender" class="form-select bg-dark text-white border-secondary">
                                        <option value="male">Male</option>
                                        <option value="female">Female</option>
                                    </select>
                                </div>
                            </div>

                            <div class="d-grid mt-4">
                                <button type="submit" class="btn btn-resto">REGISTER NOW</button>
                            </div>
                        </form>
                    </div>
                    <div class="card-footer bg-transparent border-top border-secondary text-center py-3">
                        <a href="Login" class="text-decoration-none text-muted">Already have an account? Login here</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</t:layout>
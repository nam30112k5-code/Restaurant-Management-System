<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="Login">
    <div class="container py-5">
        <div class="row justify-content-center">
            <div class="col-auto">

                <div class="card card-resto shadow-lg p-3" style="max-width: 360px; width: 100%;">

                    <div class="text-center mb-3">
                        <h2 class="font-resto text-orange">Welcome Back</h2>
                        <p class="text-muted small">Please login to continue</p>
                    </div>

                    <c:if test="${not empty success}">
                        <div class="alert alert-success bg-transparent text-success border-success p-2 small">${success}</div>
                    </c:if>
                    <c:if test="${not empty error}">
                        <div class="alert alert-danger bg-transparent text-danger border-danger p-2 small">${error}</div>
                    </c:if>

                    <form action="Login" method="POST">
                        <div class="mb-3">
                            <label class="form-label small text-muted">Username</label>
                            <input type="text" name="username" class="form-control" required 
                                   placeholder="Enter username" 
                                   value="${username}">
                        </div>

                        <div class="mb-3">
                            <label class="form-label small text-muted">Password</label>
                            <div class="input-group">
                                <input type="password" name="password" id="id_password" class="form-control" required placeholder="Enter password" style="border-right: 0;">

                                <span class="input-group-text" 
                                      id="togglePassword" 
                                      style="cursor: pointer; background-color: #333; border: 1px solid #444; border-left: 0; color: #eee;">
                                    <i class="bi bi-eye-slash" id="eyeIcon"></i>
                                </span>
                            </div>
                        </div>

                        <div class="mb-3 d-flex justify-content-center">
                            <div class="g-recaptcha" 
                                 data-sitekey="6LcAviwsAAAAAGKIudNAMh40LlgZP5ic49odI-2N" 
                                 data-theme="dark">
                            </div>
                        </div>

                        <div class="d-grid gap-2">
                            <button type="submit" class="btn btn-resto">LOGIN</button>
                        </div>
                    </form>

                    <div class="text-center mt-3">
                        <span class="text-muted small">Don't have an account? </span>
                        <a href="Register" class="text-orange text-decoration-none fw-bold small">Sign Up</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</t:layout>
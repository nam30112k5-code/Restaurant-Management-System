<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="Manage Employees">
    <jsp:include page="../../common/AdminNavbar.jsp" />

    <div class="container pb-5">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h3 class="text-white border-start border-4 border-warning ps-3">Employee Management</h3>
            <a href="Employee?action=create" class="btn btn-resto">
                <i class="bi bi-person-plus-fill"></i> Add Employee
            </a>
        </div>

        <c:if test="${not empty error}"><div class="alert alert-danger">${error}</div></c:if>

            <div class="card card-resto shadow-lg">
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-dark table-hover align-middle mb-0">
                            <thead class="bg-secondary text-white">
                                <tr>
                                    <th class="ps-4">Username</th>
                                    <th>Contact Info</th>
                                    <th>Role</th>
                                    <th>Status</th>
                                    <th class="text-end pe-4">Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${employees}" var="e">
                                <tr>
                                    <td class="ps-4 fw-bold text-orange">${e.username}</td>
                                    <td>
                                        <div><i class="bi bi-telephone-fill small text-muted"></i> ${e.phoneNumber}</div>
                                        <div class="small text-muted">ID: ${e.identityNumber}</div>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${e.roleId == 1}"><span class="badge bg-danger">Admin</span></c:when>
                                            <c:otherwise><span class="badge bg-info text-dark">Receptionist</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:if test="${e.isActive}"><span class="badge bg-success">Active</span></c:if>
                                        <c:if test="${!e.isActive}"><span class="badge bg-secondary">Inactive</span></c:if>
                                        </td>
                                        <td class="text-end pe-4">
                                            <a href="Employee?action=edit&id=${e.employeeId}" class="btn btn-sm btn-outline-warning me-1"><i class="bi bi-pencil"></i></a>
                                        <a href="Employee?action=delete&id=${e.employeeId}" class="btn btn-sm btn-outline-danger" onclick="return confirm('Delete ${e.username}?')"><i class="bi bi-trash"></i></a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</t:layout>
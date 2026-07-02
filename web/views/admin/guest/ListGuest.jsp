<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="Guest Management">
    <jsp:include page="../../common/AdminNavbar.jsp" />

    <div class="container pb-5">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h3 class="text-white border-start border-4 border-warning ps-3">Registered Guests</h3>
            <a href="Guest?action=create" class="btn btn-resto">
                <i class="bi bi-person-plus-fill"></i> Add Guest
            </a>
        </div>

        <div class="card card-resto shadow">
            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table table-dark table-hover align-middle mb-0">
                        <thead class="bg-secondary text-white">
                            <tr>
                                <th class="ps-4">Account</th>
                                <th>Personal Info</th>
                                <th>Contact</th>
                                <th>Status</th>
                                <th class="text-end pe-4">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${guests}" var="g">
                                <tr>
                                    <td class="ps-4">
                                        <span class="text-orange fw-bold">${g.username}</span>
                                        <div class="small text-muted">ID: ${g.guestId}</div>
                                    </td>
                                    <td>
                                        <div>${g.name}</div>
                                        <small class="text-muted">${g.gender}</small>
                                    </td>
                                    <td>
                                        <div>${g.phoneNumber}</div>
                                        <small class="text-muted">CCCD: ${g.identityNumber}</small>
                                    </td>
                                    <td>
                                        <c:if test="${g.isActive}"><span class="badge bg-success">Active</span></c:if>
                                        <c:if test="${!g.isActive}"><span class="badge bg-danger">Locked</span></c:if>
                                        </td>
                                        <td class="text-end pe-4">
                                            <div class="btn-group">
                                            <c:if test="${g.isActive}">
                                                <a href="Guest?action=block&id=${g.guestId}" class="btn btn-sm btn-outline-warning" onclick="return confirm('Lock account?')"><i class="bi bi-lock-fill"></i></a>
                                                </c:if>
                                                <c:if test="${!g.isActive}">
                                                <a href="Guest?action=active&id=${g.guestId}" class="btn btn-sm btn-outline-success" onclick="return confirm('Unlock account?')"><i class="bi bi-unlock-fill"></i></a>
                                                </c:if>
                                            <a href="Guest?action=reset_pass&id=${g.guestId}" class="btn btn-sm btn-outline-primary"><i class="bi bi-key-fill"></i></a>
                                            <button onclick="showDeleteModal('${g.guestId}', '${g.username}')" class="btn btn-sm btn-outline-danger"><i class="bi bi-trash-fill"></i></button>
                                        </div>
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
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="Customer Feedbacks">
    <jsp:include page="../../common/AdminNavbar.jsp" />

    <div class="container pb-5">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h3 class="text-white border-start border-4 border-warning ps-3">Feedback List</h3>
            <div class="bg-dark border border-secondary px-3 py-2 rounded">
                <span class="text-muted">Avg Rating: </span>
                <span class="text-warning fw-bold fs-5">${avgRating} <i class="bi bi-star-fill"></i></span>
            </div>
        </div>

        <div class="card card-resto shadow">
            <div class="card-body p-0">
                <table class="table table-dark table-hover align-middle mb-0">
                    <thead class="bg-secondary text-white">
                        <tr>
                            <th class="ps-4">Guest</th>
                            <th>Service Info</th>
                            <th>Rating</th>
                            <th>Comment</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${feedbacks}" var="f">
                            <tr>
                                <td class="ps-4 fw-bold text-orange">${f.guestName}</td>
                                <td>
                                    <div><i class="bi bi-geo-alt-fill"></i> ${f.tableName}</div>
                                    <small class="text-muted">Bill #${f.appointmentId}</small>
                                </td>
                                <td>
                                    <c:forEach begin="1" end="5" var="i">
                                        <i class="bi ${i <= f.rating ? 'bi-star-fill text-warning' : 'bi-star text-muted'}"></i>
                                    </c:forEach>
                                </td>
                                <td class="w-50">
                                    <div class="p-2 bg-secondary bg-opacity-25 rounded fst-italic text-light">
                                        "${f.content}"
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty feedbacks}">
                            <tr><td colspan="4" class="text-center py-4 text-muted">No feedbacks available.</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</t:layout>
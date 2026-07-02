<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="Home">
    <div style="background: linear-gradient(rgba(0,0,0,0.7), rgba(0,0,0,0.7)), url('${pageContext.request.contextPath}/static/img/background.jpg') center/cover; min-height: 90vh; display: flex; align-items: center; margin-top: -90px;">
        <div class="container text-center">
            <h1 class="display-1 fw-bold text-white mb-3">Welcome to <span class="text-orange">Resto</span></h1>
            <p class="lead text-light opacity-75 mb-5 w-75 mx-auto">Experience the finest dining with our exclusive menu and atmosphere.</p>

            <c:choose>
                <c:when test="${empty sessionScope.account}">
                    <a href="Login" class="btn btn-resto btn-lg px-5 mx-2">Login To Book</a>
                    <a href="Register" class="btn btn-outline-light btn-lg px-5 mx-2">Sign Up</a>
                </c:when>
                <c:otherwise>
                    <a href="Booking" class="btn btn-resto btn-lg px-5 shadow">Book A Table Now</a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</t:layout>
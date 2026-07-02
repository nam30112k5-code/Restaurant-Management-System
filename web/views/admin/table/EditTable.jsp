<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="Edit Table">
    <jsp:include page="../../common/AdminNavbar.jsp" />

    <div class="container pb-5">
        <div class="row justify-content-center">
            <div class="col-md-6 col-lg-5">
                <div class="card card-resto shadow-lg">
                    <div class="card-header bg-transparent border-bottom border-secondary pt-3">
                        <h4 class="text-warning mb-0"><i class="bi bi-pencil-square"></i> Edit Table</h4>
                    </div>
                    <div class="card-body p-4">
                        <form action="Table?action=update" method="POST">
                            <input type="hidden" name="tableId" value="${table.tableId}">

                            <div class="mb-4">
                                <label class="form-label text-muted">Table Name <span class="text-danger">*</span></label>
                                <input type="text" name="tableName" value="${table.tableName}" 
                                       class="form-control bg-dark text-white border-secondary" required>
                            </div>

                            <div class="d-flex justify-content-end gap-2">
                                <a href="Table" class="btn btn-outline-light">Cancel</a>
                                <button type="submit" class="btn btn-warning text-dark fw-bold">Update</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</t:layout>
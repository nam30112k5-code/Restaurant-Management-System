<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout title="Create Table">
    <jsp:include page="../../common/AdminNavbar.jsp" />

    <div class="container pb-5">
        <div class="row justify-content-center">
            <div class="col-md-6 col-lg-5">
                <div class="card card-resto shadow-lg">
                    <div class="card-header bg-transparent border-bottom border-secondary pt-3">
                        <h4 class="text-orange mb-0"><i class="bi bi-plus-square"></i> Create New Table</h4>
                    </div>
                    <div class="card-body p-4">
                        <form action="Table?action=store" method="POST">
                            <div class="mb-4">
                                <label class="form-label text-muted">Table Name <span class="text-danger">*</span></label>
                                <input type="text" name="tableName" class="form-control bg-dark text-white border-secondary" 
                                       required placeholder="e.g. VIP Table 1">
                                <div class="form-text text-secondary">Enter a unique name for the table.</div>
                            </div>

                            <div class="d-flex justify-content-end gap-2">
                                <a href="Table" class="btn btn-outline-light">Cancel</a>
                                <button type="submit" class="btn btn-resto">Save Table</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</t:layout>
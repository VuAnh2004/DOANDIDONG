<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.bean.QLHocKy"%>

<%
    QLHocKy q = (QLHocKy) request.getAttribute("q");
%>

<main id="main" class="main">
    <div class="pagetitle">
        <h2>Xóa thông tin Học Kỳ</h2>
    </div>

    <div class="row mb-3">
        <div class="col-md-6">
            <label>Mã Học Kỳ</label>
            <input type="text" class="form-control mb-3" value="<%= q.getSemesterId() %>" disabled />
        </div>
        <div class="col-md-6">
            <label>Tên Học Kỳ</label>
            <input type="text" class="form-control mb-3" value="<%= q.getSemesterName() %>" disabled />
        </div>
        <div class="col-md-6">
            <label>Mã/Năm Học Kỳ</label>
            <input type="text" class="form-control mb-3" value="<%= q.getSemesterCode() %>" disabled />
        </div>
    </div>

    <!-- Begin Form -->
    <form action="<%= request.getContextPath() %>/admin/QLHocKy/Delete" method="post">
        <input type="hidden" name="SemesterID" value="<%= q.getSemesterId() %>" />
        <a class="btn btn-lg btn-warning p-2 me-2" href="javascript:history.back()">
            <i class="bi bi-arrow-left-circle"></i> Quay lại
        </a>
        <button type="submit" class="btn btn-lg btn-danger p-2">
            <i class="bi bi-trash"></i> Xóa
        </button>
    </form>
    <!-- End Form -->
</main>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.bean.QLKhoaHoc"%>

<%
    QLKhoaHoc course = (QLKhoaHoc) request.getAttribute("course");
%>

<main id="main" class="main">
    <div class="pagetitle">
        <h2>Xóa thông tin Khóa Học</h2>
    </div>

    <div class="row mb-3">
        <div class="col-md-6">
            <label>Mã Khóa Học</label>
            <input type="text" class="form-control mb-3" value="<%= course.getCourseID() %>" disabled />
        </div>
        <div class="col-md-6">
            <label>Năm Bắt Đầu</label>
            <input type="text" class="form-control mb-3" value="<%= course.getStartYear() %>" disabled />
        </div>
        <div class="col-md-6">
            <label>Năm Kết Thúc</label>
            <input type="text" class="form-control mb-3" value="<%= course.getEndYear() %>" disabled />
        </div>
        <div class="col-md-6">
            <label>Cohort</label>
            <input type="text" class="form-control mb-3" value="<%= course.getCohort() %>" disabled />
        </div>
     
    
    </div>

    <!-- Begin Form -->
    <form action="<%= request.getContextPath() %>/admin/QLKhoaHoc/Delete" method="post">
        <input type="hidden" name="CourseID" value="<%= course.getCourseID() %>" />
        <a class="btn btn-lg btn-warning p-2 me-2" href="javascript:history.back()">
            <i class="bi bi-arrow-left-circle"></i> Quay lại
        </a>
        <button type="submit" class="btn btn-lg btn-danger p-2">
            <i class="bi bi-trash"></i> Xóa
        </button>
    </form>
    <!-- End Form -->
</main>

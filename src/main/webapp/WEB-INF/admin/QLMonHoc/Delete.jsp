<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="model.bean.QLMonHoc"%>

<%
// Dữ liệu từ Servlet được đặt tên là "q"
QLMonHoc q = (QLMonHoc) request.getAttribute("q");
if (q == null) {
	response.sendRedirect(request.getContextPath() + "/admin/QLMonHoc/Index");
	return;
}
%>

<main id="main" class="main">
	<div class="pagetitle">
		<h2>Xóa thông tin Môn Học</h2>
	</div>

	<div class="row mb-3">
		<div class="col-md-6">
			<label>Mã Môn Học</label> <input type="text"
				class="form-control mb-3" value="<%=q.getSubjectID()%>" disabled />
		</div>
		<div class="col-md-6">
			<label>Tên Môn Học</label> <input type="text"
				class="form-control mb-3" value="<%=q.getSubjectName()%>" disabled />
		</div>
		<div class="col-md-6">
			<label>Số Tiết</label> <input type="text" class="form-control mb-3"
				value="<%=q.getNumberOfLesson()%>" disabled />
		</div>
		<div class="mb-3">
			<label class="form-label">Học Kỳ Áp Dụng</label> <select
				name="Semester" class="form-control">
				<option value="Cả 2 Học kỳ">Cả 2 Học kỳ</option>
				<option value="Học kỳ 1">Học kỳ 1</option>
				<option value="Học kỳ 2">Học kỳ 2</option>

			</select>
		</div>
	</div>

	<form action="<%=request.getContextPath()%>/admin/QLMonHoc/Delete"
		method="post">
		<input type="hidden" name="SubjectID" value="<%=q.getSubjectID()%>" />
		<a class="btn btn-lg btn-warning p-2 me-2"
			href="javascript:history.back()"> <i
			class="bi bi-arrow-left-circle"></i> Quay lại
		</a>
		<button type="submit" class="btn btn-lg btn-danger p-2">
			<i class="bi bi-trash"></i> Xóa
		</button>
	</form>
</main>
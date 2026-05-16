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
	<div
		class="pagetitle d-flex justify-content-between align-items-center">
		<h2>Sửa Môn Học</h2>
	</div>


	<form action="<%=request.getContextPath()%>/admin/QLMonHoc/Edit"
		method="post">
		<input type="hidden" name="SubjectID" value="<%=q.getSubjectID()%>">

		<div class="mb-3">
			<label class="form-label">Tên Môn Học</label> <input type="text"
				name="SubjectName" class="form-control"
				value="<%=q.getSubjectName()%>" required>
		</div>

		<div class="mb-3">
			<label class="form-label">Số Tiết Học</label> <input type="number"
				name="NumberOfLesson" class="form-control"
				value="<%=q.getNumberOfLesson()%>" required min="1">
		</div>

		<%
		// Đảm bảo biến q là đối tượng QLMonHoc có sẵn
		String currentSemester = q.getSemester() != null ? q.getSemester() : "";
		%>

		<div class="mb-3">
			<label class="form-label">Học Kỳ Áp Dụng</label> <select
				name="Semester" class="form-control">
				<option value="Cả 2 Học kỳ"
					<%="Cả 2 Học kỳ".equals(currentSemester) ? "selected" : ""%>>
					Cả 2 Học kỳ</option>
				<option value="Học kỳ 1"
					<%="Học kỳ 1".equals(currentSemester) ? "selected" : ""%>>
					Học kỳ 1</option>
				<option value="Học kỳ 2"
					<%="Học kỳ 2".equals(currentSemester) ? "selected" : ""%>>
					Học kỳ 2</option>
			</select>
		</div>

		<div class="form-check mb-3">
			<input type="checkbox" name="IsActive" class="form-check-input"
				<%=q.isActive() ? "checked" : ""%>> <label
				class="form-check-label">Hiển thị</label>
		</div>

		<div class="d-flex">
			<button type="submit" class="btn btn-primary me-2">
				<i class="bi bi-save"></i> Cập nhật
			</button>
			<a href="<%=request.getContextPath()%>/admin/QLMonHoc/Index"
				class="btn btn-outline-secondary"> Hủy </a>
		</div>
	</form>
</main>